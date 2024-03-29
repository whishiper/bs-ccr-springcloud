package cn.bosenkeji.service.Impl;

import cn.bosenkeji.annotation.cache.BatchCacheRemove;
import cn.bosenkeji.interfaces.RedisInterface;
import cn.bosenkeji.mapper.CoinPairChoiceMapper;
import cn.bosenkeji.service.*;
import cn.bosenkeji.util.CommonConstantUtil;
import cn.bosenkeji.vo.coin.Coin;
import cn.bosenkeji.vo.coin.CoinPair;
import cn.bosenkeji.vo.coin.CoinPairCoin;
import cn.bosenkeji.vo.strategy.StrategyOther;
import cn.bosenkeji.vo.tradeplatform.TradePlatformApi;
import cn.bosenkeji.vo.tradeplatform.TradePlatformApiBindProductCombo;
import cn.bosenkeji.vo.transaction.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author CAJR
 * @create 2019/7/17 16:07
 */
@Service
public class CoinPairChoiceServiceImpl implements CoinPairChoiceService {
    private static final Logger log = LoggerFactory.getLogger(CoinPairChoiceServiceImpl.class);

    @Resource
    CoinPairChoiceMapper coinPairChoiceMapper;

    @Autowired
    ICoinPairCoinClientService iCoinPairCoinClientService;

    @Autowired
    ICoinPairClientService iCoinPairClientService;

    @Autowired
    ICoinClientService iCoinClientService;

    @Autowired
    ICoinPairClientService coinPairClientService;

    @Autowired
    CoinPairChoiceAttributeService coinPairChoiceAttributeService;

    @Autowired
    CoinPairChoiceAttributeCustomServiceImpl coinPairChoiceAttributeCustomService;

    @Autowired
    IStrategyService iStrategyService;

    @Autowired
    OrderGroupService orderGroupService;

    @Autowired
    ITradePlatformApiClientService iTradePlatformApiClientService;

    @Autowired
    ITradePlatformApiBindProductComboClientService iTradePlatformApiBindProductComboClientService;


//    @Cacheable(value = RedisInterface.COIN_PAIR_CHOICE_LIST_KEY,key = "#tradePlatformApiBindProductComboId+'-'+#coinId+'-'+#pageNum+'-'+#pageSize")
    @Override
    public PageInfo listByPage(int pageNum, int pageSize,int tradePlatformApiBindProductComboId,int coinId) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(fill( tradePlatformApiBindProductComboId, coinId,CommonConstantUtil.ACTIVATE_STATUS));
    }

    @Override
    public PageInfo recordByPage(int pageNum, int pageSize, int tradePlatformApiBindProductComboId, int coinId, String type) {
        PageHelper.startPage(pageNum, pageSize);
        List<CoinPairChoice> coinPairChoices = fill( tradePlatformApiBindProductComboId, coinId,CommonConstantUtil.ALL_STATUS);
        coinPairChoices = filterIsTradeShell(coinPairChoices,type);
        if (!CollectionUtils.isEmpty(coinPairChoices)){
            List<CoinPairChoiceShellOrBuyResult> coinPairChoiceShellOrBuyResults = new ArrayList<>();
            coinPairChoices.forEach(coinPairChoice -> {
                CoinPairChoiceShellOrBuyResult coinPairChoiceShellOrBuyResult = new CoinPairChoiceShellOrBuyResult();
                coinPairChoiceShellOrBuyResult.setCoinPairChoiceId(coinPairChoice.getId());
                coinPairChoiceShellOrBuyResult.setCoinPairName(coinPairChoice.getCoinPair().getName());
                coinPairChoiceShellOrBuyResults.add(coinPairChoiceShellOrBuyResult);
            });
            return new PageInfo<>(coinPairChoiceShellOrBuyResults);
        }
        return  new PageInfo<>();
    }

    private List<CoinPairChoice> fill(int tradePlatformApiBindProductComboId,int coinId,int status) {
        //货币对Map
        Map<Integer, CoinPair> coinPairMap = new HashMap<>(16);
        //根据tradePlatformApiBindProductComboId查询自选币list
        List<CoinPairChoice>  coinPairChoices;
        //根据货币id查询货币对货币的列表
        List<CoinPairCoin> coinPairCoinList = this.iCoinPairCoinClientService.listByCoinId(coinId);
        //真正返回的结果列表
        List<CoinPairChoice> resultCoinPairChoiceList = new ArrayList<>();

        //获取货币对的id的list
        List<Integer> coinPairIds = new ArrayList<>();
        if (!coinPairCoinList.isEmpty()){
            for (CoinPairCoin c : coinPairCoinList) {
                coinPairIds.add(c.getCoinPairId());
            }
        }else {
            return resultCoinPairChoiceList;
        }

        //根据货币对id列表的填充coinPairMap
        if (!coinPairIds.isEmpty()){
            List<CoinPair> coinPairs = this.iCoinPairClientService.findSection(coinPairIds);
            if (!coinPairs.isEmpty()){
                for (CoinPair c : coinPairs) {
                    coinPairMap.put(c.getId(), c);
                }
            }
        }else {
            return resultCoinPairChoiceList;
        }

        //根据TradePlatformApiBindProductComboId填充自选币的货币对数据
        coinPairChoices = fillByTradePlatformApiBindProductComboId(tradePlatformApiBindProductComboId,status);
        if (!coinPairChoices.isEmpty()){
            for (CoinPairChoice c : coinPairChoices) {
                if (coinPairMap.containsKey(c.getCoinPairId())){
                    c.setCoinPair(coinPairMap.get(c.getCoinPairId()));
                }
            }
            //把货币对不为空的数据填充
           for (CoinPairChoice coinPairChoice : coinPairChoices){
               if (coinPairChoice.getCoinPair() != null){
                   resultCoinPairChoiceList.add(coinPairChoice);
               }
           }
        }

        return filter(coinId,resultCoinPairChoiceList);
    }

    /**
     * 填充 自选币 和 策略 信息方法
     * @param tradePlatformApiBindProductComboId
     * @param coinId
     * @param isStart
     * @return
     */
    private List<CoinPairChoice> fillWithStrategy(int tradePlatformApiBindProductComboId,int coinId,int isStart) {
        //货币对Map
        Map<Integer, CoinPair> coinPairMap = new HashMap<>(16);
        //根据tradePlatformApiBindProductComboId查询自选币list
        List<CoinPairChoice>  coinPairChoices;
        //根据货币id查询货币对货币的列表
        List<CoinPairCoin> coinPairCoinList = this.iCoinPairCoinClientService.listByCoinId(coinId);
        //真正返回的结果列表
        List<CoinPairChoice> resultCoinPairChoiceList = new ArrayList<>();

        //获取货币对的id的list
        List<Integer> coinPairIds = new ArrayList<>();
        if (!coinPairCoinList.isEmpty()){
            for (CoinPairCoin c : coinPairCoinList) {
                coinPairIds.add(c.getCoinPairId());
            }
        }else {
            return resultCoinPairChoiceList;
        }

        //根据货币对id列表的填充coinPairMap
        if (!coinPairIds.isEmpty()){
            List<CoinPair> coinPairs = this.iCoinPairClientService.findSection(coinPairIds);
            if (!coinPairs.isEmpty()){
                for (CoinPair c : coinPairs) {
                    coinPairMap.put(c.getId(), c);
                }
            }
        }else {
            return resultCoinPairChoiceList;
        }

        //根据TradePlatformApiBindProductComboId填充自选币的货币对数据
        coinPairChoices = selectByRobotIdAndIsStart(tradePlatformApiBindProductComboId,isStart);

        if (!coinPairChoices.isEmpty()){
            for (CoinPairChoice c : coinPairChoices) {
                if (coinPairMap.containsKey(c.getCoinPairId())){
                    c.setCoinPair(coinPairMap.get(c.getCoinPairId()));
                }
                if (c.getCoinPairChoiceAttribute() != null) {
                    if (c.getCoinPairChoiceAttribute().getStrategyId() > 0) {
                        StrategyOther strategy = iStrategyService.getStrategy(c.getCoinPairChoiceAttribute().getStrategyId());
                        if (strategy != null) {
                            c.getCoinPairChoiceAttribute().setStrategy(strategy);
                        }
                    }
                }
            }
            //把货币对不为空的数据填充
            for (CoinPairChoice coinPairChoice : coinPairChoices){
                if (coinPairChoice.getCoinPair() != null){
                    resultCoinPairChoiceList.add(coinPairChoice);
                }
            }
        }




        return filter(coinId,resultCoinPairChoiceList);
    }

    /**
     * 根据绑定id中的apiId查询sign，用来查询相同sign的api对应的绑定记录
     * @param tradePlatformApiBindProductComboId 绑定id
     * @return
     */
    @Override
    public List<Integer> getAllSameSignTradePlatformApiBindProductComboIds(int tradePlatformApiBindProductComboId){
        TradePlatformApiBindProductCombo tradePlatformApiBindProductCombo = this.iTradePlatformApiBindProductComboClientService.getOneByPrimaryId(tradePlatformApiBindProductComboId);
        List<Integer> tradePlatformApiBindProductComboIdsResult = new ArrayList<>();
        if (tradePlatformApiBindProductCombo == null){
            return tradePlatformApiBindProductComboIdsResult;
        }
        int apiId = tradePlatformApiBindProductCombo.getTradePlatformApiId();
        TradePlatformApi tradePlatformApi = this.iTradePlatformApiClientService.getOneTradePlatformApi(apiId);
        if (tradePlatformApi == null){
            return tradePlatformApiBindProductComboIdsResult;
        }

        String sign = tradePlatformApi.getSign();
        List<TradePlatformApi> tradePlatformApis = this.iTradePlatformApiClientService.findAllBySign(sign);
        if (!tradePlatformApis.isEmpty()){
            for (TradePlatformApi t : tradePlatformApis) {
                List<TradePlatformApiBindProductCombo> tradePlatformApiBindProductCombos = t.getTradePlatformApiBindProductCombos();
                if (!tradePlatformApiBindProductCombos.isEmpty()){
                    for (TradePlatformApiBindProductCombo bindProductCombo: tradePlatformApiBindProductCombos) {
                        tradePlatformApiBindProductComboIdsResult.add(bindProductCombo.getId());
                    }
                }
            }
            return tradePlatformApiBindProductComboIdsResult;
        }

        return tradePlatformApiBindProductComboIdsResult;
    }

    private List<CoinPairChoice> fillByTradePlatformApiBindProductComboId(int tradePlatformApiBindProductComboId,int status){
        //根据绑定id中的apiId查询sign，用来查询相同sign的api对应的绑定记录id列表
        List<Integer> tradePlatformApiBindProductComboIds = this.getAllSameSignTradePlatformApiBindProductComboIds(tradePlatformApiBindProductComboId);

        if (status == CommonConstantUtil.ACTIVATE_STATUS){
            return this.coinPairChoiceMapper.findAllByTradePlatformApiBindProductComboIdsAndStatus(tradePlatformApiBindProductComboIds);
        }
        return this.coinPairChoiceMapper.findAllByTradePlatformApiBindProductComboIds(tradePlatformApiBindProductComboIds);
    }

    /**
     * 过滤掉不是该计价货币的货币对
     * @param coinId 货币id
     * @param coinPairChoices 填充好的自选币列表
     * @return 过滤好的自选币的列表
     */
    private List<CoinPairChoice> filter(int coinId,List<CoinPairChoice> coinPairChoices){
        Coin coin = iCoinClientService.get(coinId);
        List<CoinPairChoice> result = new ArrayList<>(coinPairChoices);

        String coinName = coin.getName().toUpperCase();
        int coinNameLength = coinName.length();
        for (CoinPairChoice c : coinPairChoices) {
            CoinPair coinPair = c.getCoinPair();

            String coinPairName = coinPair.getName().toUpperCase();
            int coinPairNameLength = coinPairName.length();
            if (coinPairName.lastIndexOf(coinName)+coinNameLength != coinPairNameLength){
                result.remove(c);
            }
        }

        return result;
    }

    /**
     * 过滤掉没有卖买记录的该计价货币的货币对
     * @param coinPairChoices 自选币的列表
     */
    private List<CoinPairChoice> filterIsTradeShell(List<CoinPairChoice> coinPairChoices, String type) {
        if (CollectionUtils.isEmpty(coinPairChoices)){
            return null;
        }

        List<CoinPairChoice> resultCoinPairChoices = new ArrayList<>();
        List<Integer> coinPairChoiceIds = new ArrayList<>();
        coinPairChoices.forEach(coinPairChoice -> {
            coinPairChoiceIds.add(coinPairChoice.getId());
        });

        List<OrderGroup> orderGroups = new ArrayList<>();
        if (!coinPairChoiceIds.isEmpty()){
            orderGroups = this.orderGroupService.partFindByCoinPairChoiceIds(coinPairChoiceIds);
        }

        if (!type.isEmpty() && !orderGroups.isEmpty()){
            //填充订单组中空的
            orderGroups.forEach(orderGroup -> {
                if (orderGroup.getCoinPairChoice() == null){
                    coinPairChoices.forEach(coinPairChoice -> {
                        if (coinPairChoice.getId() == orderGroup.getCoinPairChoiceId()){
                            orderGroup.setCoinPairChoice(coinPairChoice);
                        }
                    });
                }
            });

            if (type.equals(CommonConstantUtil.RECORD_PROFIT)){
                orderGroups.forEach(orderGroup -> {
                    if (orderGroup.getIsEnd() == CommonConstantUtil.ORDER_GROUP_IS_END && orderGroup.getEndType() != CommonConstantUtil.ORDER_GROUP_FORGET){
                        resultCoinPairChoices.add(orderGroup.getCoinPairChoice());
                    }
                });
            }
            if (type.equals(CommonConstantUtil.RECORD_BUY)){
                orderGroups.forEach(orderGroup -> {
                    if (orderGroup.getCoinPairChoice() != null){
                        resultCoinPairChoices.add(orderGroup.getCoinPairChoice());
                    }
                });
            }
        }



        //去重
        List<CoinPairChoice> unique =  resultCoinPairChoices.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(CoinPairChoice::getId))), ArrayList::new));

        List<Integer> uniqueIdResult = new ArrayList<>();
        unique.forEach(coinPairChoice -> uniqueIdResult.add(coinPairChoice.getId()));
        List<CoinPairChoice> result = new ArrayList<>();
        coinPairChoices.forEach(coinPairChoice -> {
            if (uniqueIdResult.contains(coinPairChoice.getId())){
                result.add(coinPairChoice);
            }
        });
        return result;

    }

    @Cacheable(value = RedisInterface.COIN_PAIR_CHOICE_ID_KEY,key = "#id",unless = "#result == null")
    @Override
    public CoinPairChoice get(int id) {
        CoinPairChoice coinPairChoice = this.coinPairChoiceMapper.selectByPrimaryKey(id);
        if (coinPairChoice != null){
            CoinPair coinPair = this.iCoinPairClientService.getCoinPair(coinPairChoice.getCoinPairId());
            coinPairChoice.setCoinPair(coinPair);
            if (coinPairChoice.getCoinPairChoiceAttribute() != null){
                coinPairChoice.getCoinPairChoiceAttribute().setExpectMoney(coinPairChoice.getCoinPairChoiceAttribute().getExpectMoney() / CommonConstantUtil.ACCURACY_RATIO);
            }
            if (coinPairChoice.getCoinPairChoiceAttributeCustom() != null){
                coinPairChoice.getCoinPairChoiceAttributeCustom()
                        .setFirstOpenPrice(coinPairChoice.getCoinPairChoiceAttributeCustom().getFirstOpenPrice() / CommonConstantUtil.ACCURACY_RATIO);
                coinPairChoice.getCoinPairChoiceAttributeCustom()
                        .setStopProfitMoney(coinPairChoice.getCoinPairChoiceAttributeCustom().getStopProfitMoney() / CommonConstantUtil.ACCURACY_RATIO);
            }
        }

        return coinPairChoice;
    }

    @Override
    public CoinPairChoice getByDisregardStatus(int id) {
        return this.coinPairChoiceMapper.selectByPrimaryKey(id);
    }

    @BatchCacheRemove(value = "'ccr:coinPairChoice:list::'+#coinPairChoice.tradePlatformApiBindProductComboId+'-'",condition = "#result != null")
    @Override
    public Optional<Integer> add(CoinPairChoice coinPairChoice) {
        Integer coinPairChoiceId = coinPairChoiceMapper.selectIdByCoinPartnerIdAndRobotIdAndStatus(coinPairChoice.getCoinPairId(),coinPairChoice.getTradePlatformApiBindProductComboId());
        if (coinPairChoiceId != null){
            if (coinPairChoiceId >0){
                return Optional.of(this.coinPairChoiceMapper.logicDelete(coinPairChoiceId,Timestamp.valueOf(LocalDateTime.now()),CommonConstantUtil.ACTIVATE_STATUS));
            }
        }

        return Optional.of(coinPairChoiceMapper.insertSelective(coinPairChoice));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.COIN_PAIR_CHOICE_ID_KEY,key = "#coinPairChoice.id",condition = "#result != null")
            }
    )
    @BatchCacheRemove(value = "'ccr:coinPairChoice:list::'+#coinPairChoice.tradePlatformApiBindProductComboId+'-'",condition = "#result != null")
    @Override
    public Optional<Integer> update(CoinPairChoice coinPairChoice) {
        return Optional.of(coinPairChoiceMapper.updateByPrimaryKeySelective(coinPairChoice));
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public Optional<Integer> delete(int id) {
        try{
            if (this.coinPairChoiceAttributeService.checkByCoinPartnerChoiceId(id).get() == 1){
                this.coinPairChoiceAttributeService.delete(id);
            }

            if (this.coinPairChoiceAttributeCustomService.checkByCoinPartnerChoiceId(id).get() == 1){
                this.coinPairChoiceAttributeCustomService.deleteByCoinPairChoiceId(id);
            }

            coinPairChoiceMapper.logicDelete(id,Timestamp.valueOf(LocalDateTime.now()), CommonConstantUtil.DELETE_STATUS);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Optional.of(CommonConstantUtil.FAIL);
        }

        return Optional.of(CommonConstantUtil.SUCCESS);
    }

    @Override
    public Optional<Integer> checkExistByCoinPartnerNameAndRobotId(String coinPairName, int tradePlatformApiBindProductComboId) {
        CoinPair coinPair = new CoinPair();
        if (this.iCoinPairClientService.getCoinPairByName(coinPairName) != null){
            coinPair = this.iCoinPairClientService.getCoinPairByName(coinPairName);
        }
        return Optional.of(this.coinPairChoiceMapper.checkExistByCoinPartnerIdAndRobotIdAndStatus(coinPair.getId(), tradePlatformApiBindProductComboId));
    }

    @Override
    public Optional<Integer> checkExistByCoinPartnerIdAndRobotId(int coinPairId, int tradePlatformApiBindProductComboId) {
        return Optional.ofNullable(this.coinPairChoiceMapper.checkExistByCoinPartnerIdAndRobotIdAndStatus(coinPairId, tradePlatformApiBindProductComboId));
    }

    @Override
    public List<CoinPairChoice> findAll() {
        List<CoinPairChoice> coinPairChoiceList = coinPairChoiceMapper.findAll();
        List<CoinPair> coinPairList = coinPairClientService.findAll();
        if (!CollectionUtils.isEmpty(coinPairChoiceList) && !CollectionUtils.isEmpty(coinPairList)) {
            for (CoinPairChoice c : coinPairChoiceList) {
                Optional<CoinPair> first = coinPairList.stream().filter((v) -> v.getId() == c.getCoinPairId()).findFirst();
                c.setCoinPair(first.get());
            }
        }
        return coinPairChoiceList;
    }

    //删除多个如何保证缓存同步呢？
    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.COIN_PAIR_CHOICE_ID_KEY,allEntries = true,condition = "#result != null ")
            }
    )
    @BatchCacheRemove(value = "'ccr:coinPairChoice:list::'+#tradePlatformApiBindProductComboId+'-'",condition = "#result != null")
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public Optional<Integer> batchDelete(String idStr,int tradePlatformApiBindProductComboId) {
        //有属性的自选币id集合
        List<Integer> coinPairChoiceAttributeList = new ArrayList<>();

        //转格式
        String[] coinPairChoiceIdStr = idStr.split(",");
        List<Integer> coinPairChoiceIds = new ArrayList<>();
        if (coinPairChoiceIdStr.length != 0){
            for (String s : coinPairChoiceIdStr) {
                coinPairChoiceIds.add(Integer.valueOf(s));
            }
        }

        //验证verification
        List<CoinPairChoice> coinPairChoices = this.coinPairChoiceMapper.findAll();
        //筛选出等于tradePlatformApiBindProductComboId的自选币
        List<CoinPairChoice> coinPairChoicesByTradePlatformApiBindProductComboId;
        //筛选出等于tradePlatformApiBindProductComboId的自选币id
        List<Integer> coinPairChoiceIdList = new ArrayList<>();
        if (!coinPairChoices.isEmpty()){
            coinPairChoicesByTradePlatformApiBindProductComboId = coinPairChoices.stream().filter(coinPairChoice -> coinPairChoice.getTradePlatformApiBindProductComboId() == tradePlatformApiBindProductComboId).collect(Collectors.toList());
            if (!coinPairChoicesByTradePlatformApiBindProductComboId.isEmpty()){
                coinPairChoicesByTradePlatformApiBindProductComboId.forEach(coinPairChoice -> {
                    coinPairChoiceIdList.add(coinPairChoice.getId());
                });
            }else {
                return Optional.of(CommonConstantUtil.FAIL);
            }
        }
        if (!coinPairChoiceIdList.isEmpty()) {
            for (Integer id : coinPairChoiceIds) {
                if (!coinPairChoiceIdList.contains(id)){
                    return Optional.of(CommonConstantUtil.FAIL);
                }
            }
        }

        //分开没有属性的自选币id
        //所有自选币属性中的自选币id
        List<Integer> coinPairChoiceAttributeIdList = this.coinPairChoiceAttributeService.findAllCoinPartnerChoiceId();
        if (!coinPairChoiceAttributeIdList.isEmpty()){
            for (Integer id : coinPairChoiceIds) {
                if (coinPairChoiceAttributeIdList.contains(id)){
                    coinPairChoiceAttributeList.add(id);
                }
            }
        }

        //删除，手动回滚
        if (!coinPairChoiceAttributeList.isEmpty()){
            try{
                this.coinPairChoiceAttributeService.batchDelete(coinPairChoiceAttributeList);
                this.coinPairChoiceAttributeCustomService.batchDelete(coinPairChoiceAttributeList);
                this.coinPairChoiceMapper.batchDelete(coinPairChoiceIds);
            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Optional.of(CommonConstantUtil.FAIL);
            }
        }else{
            try{
                this.coinPairChoiceMapper.batchLogicDelete(coinPairChoiceIds,Timestamp.valueOf(LocalDateTime.now()));
            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Optional.of(CommonConstantUtil.FAIL);
            }
        }
        return Optional.of(CommonConstantUtil.SUCCESS);
    }

    @Override
    public List<Integer> findAllCoinPartnerChoiceId() {
        return this.coinPairChoiceMapper.findAllCoinPairChoiceId();
    }

    @Override
    public CoinPairChoicePositionDetailResult getCoinPairChoicePositionDetail(int coinPairChoiceId) {
        CoinPairChoicePositionDetailResult result = new CoinPairChoicePositionDetailResult();
        CoinPairChoice coinPairChoice = get(coinPairChoiceId);

        if (coinPairChoice == null || coinPairChoice.getCoinPair() == null){
            log.info("该自选币不存在 返回null");
            return null;
        }

        result.setCoinPairChoiceName(coinPairChoice.getCoinPair().getName());
        try {
            if (this.orderGroupService.checkExistByCoinPairChoiceIdAndIsEnd(coinPairChoiceId,0).get() > 0 ){
                if (this.orderGroupService.getByCoinPairChoiceId(coinPairChoiceId).getTradeOrders() != null){
                    List<TradeOrder> tradeOrders = this.orderGroupService.getByCoinPairChoiceId(coinPairChoiceId).getTradeOrders().stream().filter(tradeOrder -> tradeOrder.getTradeType() == 1).collect(Collectors.toList());
                    result.setTradeOrders(tradeOrders);
                    return result;
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
    }

    /**
     * create by xivin
     * @param tradePlatformApiBindProductComboId
     * @param isStart
     * @return
     */
    @Override
    public List<CoinPairChoice> listByRobotIdAndIsStart(int tradePlatformApiBindProductComboId, int isStart, int coinId) {
        return fillWithStrategy(tradePlatformApiBindProductComboId,coinId,isStart);
    }

    public List<CoinPairChoice> selectByRobotIdAndIsStart(int tradePlatformApiBindProductComboId, int isStart) {
        //根据绑定id中的apiId查询sign，用来查询相同sign的api对应的绑定记录id列表
        List<Integer> tradePlatformApiBindProductComboIds = this.getAllSameSignTradePlatformApiBindProductComboIds(tradePlatformApiBindProductComboId);
        return coinPairChoiceMapper.findByTradePlatformApiBindProductComboIdsAndIsStart(tradePlatformApiBindProductComboIds,isStart);
    }

    @BatchCacheRemove(value = "'ccr:coinPairChoice:list::'+#newBindId+'-'",condition = "#result != null")
    @Override
    public int updateByBindId(int originalBindId, int newBindId) {
        return coinPairChoiceMapper.updateByBindId(originalBindId,newBindId);
    }

    /**
     * 通过 tradePlatformApiBindProductComboIds 集合查询 状态存在的 自选币列表
     * 单表查询
     * created by xivin
     * @param tradePlatformApiBindProductComboIds
     * @return
     */
    @Override
    public List<CoinPairChoice> findByTradePlatformApiBindProductComboIdsAndStatus(Set<Integer> tradePlatformApiBindProductComboIds) {

        // 注意不能传空的集合
        if (tradePlatformApiBindProductComboIds.isEmpty()) {
            return null;
        }
        return coinPairChoiceMapper.findByTradePlatformApiBindProductComboIdsAndStatus(tradePlatformApiBindProductComboIds);
    }
}
