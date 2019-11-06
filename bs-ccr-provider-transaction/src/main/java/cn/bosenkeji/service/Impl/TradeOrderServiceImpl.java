package cn.bosenkeji.service.Impl;

import cn.bosenkeji.OpenSearchPage;
import cn.bosenkeji.interfaces.OpenSearchTimeType;
import cn.bosenkeji.mapper.TradeOrderMapper;
import cn.bosenkeji.service.TradeOrderService;
import cn.bosenkeji.util.CommonConstantUtil;
import cn.bosenkeji.util.DateUtils;
import cn.bosenkeji.vo.OpenSearchFormat;
import cn.bosenkeji.vo.transaction.*;
import com.alibaba.fastjson.JSON;
import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Lists;
import com.aliyun.opensearch.sdk.dependencies.org.json.JSONObject;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.search.Config;
import com.aliyun.opensearch.sdk.generated.search.SearchFormat;
import com.aliyun.opensearch.sdk.generated.search.SearchParams;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.aliyun.opensearch.search.SearchParamsBuilder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * @author CAJR
 * @date 2019/11/4 3:24 下午
 */
@Service
public class TradeOrderServiceImpl implements TradeOrderService {


    private static final String appName = "bs_ccr_trade_dev";
    private static final String orderTable="trade_order";

    private static final ArrayList<String> openSearchFetchField = Lists.newArrayList("id","order_group_id","coin_pair_choice_id","coin_pair_choice","trade_profit_price","trade_numbers","trade_cost","trade_type","status","created_at","updated_at","name","created_time");


    @Resource
    TradeOrderMapper tradeOrderMapper;

    @Resource
    private DocumentClient documentClient;

    @Resource
    private OpenSearchClient openSearchClient;

    @Override
    public PageInfo listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TradeOrder> tradeOrders = this.tradeOrderMapper.findAll();
        if (!CollectionUtils.isEmpty(tradeOrders)){
            tradeOrders.forEach(this::reverseTransformNumericalValue);
        }
        return new PageInfo<>(tradeOrders);
    }

    @Override
    public List<TradeOrder> listByOrderGroupId(int orderGroupId) {
        List<TradeOrder> tradeOrders = this.tradeOrderMapper.findAllByOrderGroupId(orderGroupId);
        if (!CollectionUtils.isEmpty(tradeOrders)){
            tradeOrders.forEach(this::reverseTransformNumericalValue);
        }
        return tradeOrders;
    }

    @Override
    public TradeOrder getById(int tradeOrderId) {
        TradeOrder tradeOrderResult =  this.tradeOrderMapper.selectByPrimaryKey(tradeOrderId);
        reverseTransformNumericalValue(tradeOrderResult);
        return tradeOrderResult;
    }

    private void transformNumericalValue(TradeOrder tradeOrder){
        double tradeAveragePrice = tradeOrder.getTradeAveragePrice()* CommonConstantUtil.ACCURACY;
        double tradeNumbers = tradeOrder.getTradeNumbers()* CommonConstantUtil.ACCURACY;
        double tradeCost = tradeOrder.getTradeCost()* CommonConstantUtil.ACCURACY;
        double shellProfit = tradeOrder.getShellProfit()* CommonConstantUtil.ACCURACY;

        tradeOrder.setStatus(CommonConstantUtil.ACTIVATE_STATUS);
        tradeOrder.setTradeAveragePrice(tradeAveragePrice);
        tradeOrder.setTradeNumbers(tradeNumbers);
        tradeOrder.setTradeCost(tradeCost);
        tradeOrder.setShellProfit(shellProfit);
        tradeOrder.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
    }


    private void reverseTransformNumericalValue(TradeOrder tradeOrder){
        double tradeAveragePrice = tradeOrder.getTradeAveragePrice() / CommonConstantUtil.ACCURACY;
        double tradeNumbers = tradeOrder.getTradeNumbers() / CommonConstantUtil.ACCURACY;
        double tradeCost = tradeOrder.getTradeCost() / CommonConstantUtil.ACCURACY;
        double shellProfit = tradeOrder.getShellProfit() / CommonConstantUtil.ACCURACY;


        tradeOrder.setTradeAveragePrice(tradeAveragePrice);
        tradeOrder.setTradeNumbers(tradeNumbers);
        tradeOrder.setTradeCost(tradeCost);
        tradeOrder.setShellProfit(shellProfit);
    }

    @Override
    public Optional<Integer> add(TradeOrder tradeOrder) {
        transformNumericalValue(tradeOrder);
        Optional<Integer> integer = Optional.of(this.tradeOrderMapper.insertSelective(tradeOrder));
        pushToOpenSearch(tradeOrder.getId());
        return integer;
    }

    /**
     *
     * @param tradeOrderId
     * @return
     */
    public boolean pushToOpenSearch(int tradeOrderId) {

        if(tradeOrderId <= 0)
            return false;
        OpenSearchFormat<TradeOrder> field=new OpenSearchFormat<>();
        TradeOrder tradeOrder = getById(tradeOrderId);
        if(null == tradeOrder)
            return false;

        field.setFields(tradeOrder);
        field.setCmd("ADD");

        List<OpenSearchFormat> list=new ArrayList<>();
        list.add(field);
        String jsonList = JSON.toJSONString(list);
        System.out.println("jsonList = " + jsonList);

        try {
            OpenSearchResult pushResult = documentClient.push(jsonList, appName, orderTable);
            if(pushResult.getResult().equalsIgnoreCase("true")) {
                System.out.println("pushResult = " + pushResult+"推送成功");
                return true;
            }else {
                System.out.println("push 失败 "+pushResult.getTraceInfo());
                return false;
            }

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OpenSearchPage searchTradeOrderByCondition(OrderSearchRequestVo orderSearchRequestVo,int pageNum,int pageSize) {

        SearcherClient searcherClient = new SearcherClient(openSearchClient);
        OpenSearchPage page= new OpenSearchPage();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.countStartRow();

        if(org.apache.commons.lang.StringUtils.isBlank(orderSearchRequestVo.getCoinPairChoiceIds()))
            return page;
        Config config = new Config(Lists.newArrayList(appName));
        config.setSearchFormat(SearchFormat.FULLJSON);
        config.setStart(page.getStartRow());
        config.setHits(pageSize);
        config.setFetchFields(openSearchFetchField);

        SearchParams searchParams = new SearchParams(config);
        StringBuffer otherBuffer = new StringBuffer();
        StringBuffer queryBuffer = new StringBuffer();

        //设置 自选币 条件
        String[] coin_ids = orderSearchRequestVo.getCoinPairChoiceIds().split(",");
        for (String coin_id : coin_ids) {
            System.out.println("coin_id = " + coin_id);
            Integer integer = Integer.valueOf(coin_id);
            if(null != integer && integer>0) {
                queryBuffer.append(" OR coin_pair_choice_id:'"+integer+"' ");
            }
        }
        String query="("+queryBuffer.toString().replaceFirst("OR","")+")";


        if(org.apache.commons.lang.StringUtils.isBlank(queryBuffer.toString())) {
            return page;
        }

        //设置交易类型 0默认全部不处理 1买入 2卖出
        Integer tradeType = orderSearchRequestVo.getTradeType();
        if(null != tradeType && tradeType >0) {
            otherBuffer.append(" AND trade_type:'"+tradeType+"' ");
        }

        //处理时间
        Long startTime = orderSearchRequestVo.getStartTime();
        Long endTime = orderSearchRequestVo.getEndTime();
        if(null != startTime && null!= endTime && startTime >0 && endTime > 0) {

            otherBuffer.append(" AND created_at:[" + orderSearchRequestVo.getStartTime() + ","+orderSearchRequestVo.getEndTime()+"] ");
        }

        if(StringUtils.isNotBlank(otherBuffer.toString())) {
            query = query+otherBuffer.toString();
        }

        searchParams.setQuery(query);
        System.out.println("the query is"+query);
        SearchParamsBuilder searchParamsBuilder = SearchParamsBuilder.create(searchParams);

        try{
            SearchResult execute = searcherClient.execute(searchParamsBuilder);
            String result = execute.getResult();


            //OpenSearchExecuteResult openSearchExecuteResult1 = JSON.parseObject(result, OpenSearchExecuteResult.class);
            OpenSearchExecuteResult openSearchExecuteResult = com.alibaba.fastjson.JSONObject.parseObject(result, OpenSearchExecuteResult.class);
            System.out.println("openSearchExecuteResult = " + openSearchExecuteResult);

            List<OpenSearchField> items = openSearchExecuteResult.getResult().getItems();
            List<OpenSearchOrderVo> openSearchOrderVos=new ArrayList<>();
            for (OpenSearchField item : items) {
                item.getFields().setCoinPairChoice(com.alibaba.fastjson.JSONObject.parseObject(item.getFields().getCoin_pair_choice(),CoinPairChoice.class));
                item.getFields().setCoin_pair_choice("");
                openSearchOrderVos.add(item.getFields());
            }
            page.setList(openSearchOrderVos);
            page.setTotal(openSearchExecuteResult.getResult().getTotal());
            return page;

        }catch (Exception e) {
            e.printStackTrace();
            return page;
        }

    }

    @Override
    public Optional<Integer> delete(int id) {
        return Optional.of(this.tradeOrderMapper.deleteByPrimaryKey(id));
    }

    @Override
    public Integer updateOpenSearchFromSql(int id) {
        return pushToOpenSearch(id)==true?1:0;
    }
}
