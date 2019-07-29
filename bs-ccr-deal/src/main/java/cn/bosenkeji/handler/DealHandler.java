package cn.bosenkeji.handler;

import cn.bosenkeji.messaging.MySink;
import cn.bosenkeji.messaging.MySource;
import cn.bosenkeji.service.ICoinPairChoiceClientService;
import cn.bosenkeji.service.ITradePlatformApiClientService;
import cn.bosenkeji.vo.tradeplatform.TradePlatformApi;
import cn.bosenkeji.vo.transaction.CoinPairChoiceJoinCoinPair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class DealHandler {

    @Autowired
    private MySource source;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ICoinPairChoiceClientService coinPairChoiceClientService;

    @Autowired
    private ITradePlatformApiClientService tradePlatformApiClientService;


    @StreamListener("input1")
    private void consumerMessage(String msg) {
        //将json字符串转换为json对象
        JSONObject jsonObject = JSON.parseObject(msg);

        //获取实时价格
        Double price = Double.valueOf(jsonObject.get("price").toString());

        //获取深度
        Map<Integer,Double> deep = new HashMap<>();

        JSONArray deepArray = (JSONArray) jsonObject.get("deep");

        for (int i=0;i<deepArray.size();i++) {
            JSONArray o = (JSONArray) deepArray.get(i);
            deep.put(Integer.valueOf(o.get(0).toString()),Double.valueOf(o.get(1).toString()));
        }

        //获取货币对的值
        String symbol = jsonObject.get("symbol").toString();

        //获取 自选货币对信息
        List<CoinPairChoiceJoinCoinPair> coinPairChoiceList = coinPairChoiceClientService.listCoinPairChoice();

        //过滤暂停和停止、不是该货币对的信息
        List<CoinPairChoiceJoinCoinPair> filterList = coinPairChoiceList.stream()
                .filter((e) -> (e.getIsStart() == 1) && (symbol.equals(e.getCoinPairName())))
                .collect(Collectors.toList());

        //遍历自选货币对 执行判断逻辑
        filterList.parallelStream().forEach((e)->{
            //通过userId获取平台对应的key
            int userId = e.getUserId();
            TradePlatformApi api = tradePlatformApiClientService.selectByUserId(userId);
            String accessKey = api.getAccessKey();
            String secretKey = api.getSecretKey();

            //获取该用户redis中的数据
            //String redisKey = accessKey+"_"+secretKey+"_"+symbol;
            String redisKey = "asdf";
            Object result = redisTemplate.opsForValue().get(redisKey);
            JSONObject resultJOSNObject = JSON.parseObject(result.toString());

            if (resultJOSNObject == null) {
                return;
            }

            //计算实时收益比   判断买卖
            //持仓费用
            Double positionCost = Double.valueOf(resultJOSNObject.get("position_cost").toString());
            //持仓数量
            Double positionNum = Double.valueOf(resultJOSNObject.get("position_num").toString());
            //实时收益比
            Double realTimeEarningRatio = countRealTimeEarningRatio(positionNum,positionCost,price);

            if (realTimeEarningRatio >= 1) {
            //if (true) {
            //判断是否卖
                /*
                    获取判断买的参数
                    double positionPrice, 上面的positionCost
                    int stopProfitType,
                    double stopProfitPrice,
                    double stopProfitRatio, 和止盈触发比一致
                    double realTimeEarningRatio, 上面
                    double triggerRatio, 和上面的stopProfitRatio
                    double callBackRatio
                 */
                int stopProfitType = Integer.valueOf(resultJOSNObject.get("is_use_follow_target_profit").toString());
                double stopProfitPrice = Double.valueOf(resultJOSNObject.get("target_profit_price").toString());
                double stopProfitRatio = Double.valueOf(resultJOSNObject.get("turn_down_ratio").toString());
                double callBackRatio = Double.valueOf(resultJOSNObject.get("emit_ratio").toString());
                boolean isSell = isSell(positionCost, stopProfitType, stopProfitPrice, stopProfitRatio,
                        realTimeEarningRatio, stopProfitRatio, callBackRatio,redisKey);

                if (isSell) {
                    //mq发送卖的消息

                }

            } else {
                //判断是否买
                /*
                    获取判断买的参数
                    double realTimePrice, 上面的price
                    int orderNumber,
                    int maxOrderNumber,
                    double averagePosition,
                    double buildPositionInterval,
                    double averagePrice,
                    double followLowerRatio,
                    double followCallbackRatio,
                    double minAveragePrice
                    double firstOrderPrice

                 */
                int orderNumber = Integer.valueOf(resultJOSNObject.get("finished_order").toString());
                int maxOrderNumber = Integer.valueOf(resultJOSNObject.get("max_trade_order").toString());
                double averagePosition = positionCost/positionNum;
                double buildPositionInterval = Double.valueOf(resultJOSNObject.get("store_split").toString());
                //获取交易量，计算拟买入均价
                double buyVolume = Double.valueOf(resultJOSNObject.getJSONObject("buy_volume").get(orderNumber+"").toString());
                double averagePrice = countAveragePrice(deep,buyVolume);
                double followLowerRatio = Double.valueOf(resultJOSNObject.get("follow_lower_ratio").toString());
                double followCallbackRatio = Double.valueOf(resultJOSNObject.get("follow_callback_ratio").toString());
                double minAveragePrice = Double.valueOf(resultJOSNObject.get("min_averagePrice").toString());
                double firstOrderPrice = Double.valueOf(resultJOSNObject.get("first_order_price").toString());
                boolean isBuy = isBuy( orderNumber, maxOrderNumber, averagePosition,
                        buildPositionInterval, averagePrice,followLowerRatio,followCallbackRatio,minAveragePrice,firstOrderPrice);
                if (true) {
                    //mq发送买的消息
                    Message<String> test = MessageBuilder.withPayload("test").build();
                    source.output2().send(test);
                }
            }
        });




    }


    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("aaa");
        strings.add("bbb");
        strings.add("ccc");

        String s = "aaa";

        strings.stream().filter((e) -> e.equals(s))
                .forEach(System.out::println);


    }



    @GetMapping("/test")
    public void test() {
        String s = "{\"max_trade_order\":6,\"finished_order\":0,\"trade_times\":197,\"policy_series\":[1,2,4,8,16,32],\"buy_volume\":{\"0\":\"19.70000000\",\"1\":\"39.40000000\",\"2\":\"78.80000000\",\"3\":\"157.60000000\",\"4\":\"315.20000000\",\"5\":\"630.40000000\"},\"first_order_price\":0.0444,\"isFollowBuild\":\"0\",\"isNeedRecordMaxRiskBenefitRatio\":\"0\",\"min_averagePrice\":0,\"store_split\":\"0.0044053333333333\",\"trade_status\":\"0\",\"history_max_riskBenefitRatio\":\"0\",\"position_average\":\"0\",\"position_cost\":\"0\",\"position_num\":\"0\",\"emit_ratio\":0.2,\"turn_down_ratio\":0.1,\"follow_lower_ratio\":0.01,\"follow_callback_ratio\":0.1,\"is_use_follow_target_profit\":\"1\",\"target_profit_price\":50}";
        JSONObject jsonObject = JSON.parseObject(s);
        jsonObject.toJSONString();
        redisTemplate.opsForValue().set("asdf",s);
    }


    /**
     * 计算实时收益比  买价*持仓数量/持仓费用  精确小数点后4位
     * @param number 持仓数量
     * @param cost 持仓费用
     * @param realTimePrice 实时买价
     * @return 实时收益比
     */
    private double countRealTimeEarningRatio(double number, double cost, double realTimePrice) {
        return realTimePrice*number/cost;
    }

    /**
     * 计算拟买入均价
     * @param deep 实时深度
     * @param quantity 某交易量
     * @return 拟买入均价
     */
    private double countAveragePrice(Map<Integer,Double> deep,double quantity) {
        double priceSum = 0;
        double deepSum = 0;
        for (Map.Entry<Integer, Double> entry : deep.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            if ( quantity >= value) {
                priceSum += key*value;
                quantity -= value;
                deepSum += value;
            } else {
                priceSum += key*quantity;
                deepSum += quantity;
            }
        }
        return priceSum/deepSum;
    }


    /**
     * 是否买的逻辑
     * @param orderNumber 已做单数 redis获取
     * @param maxOrderNumber 最大交易单数 redis获取
     * @param averagePosition 持仓均价 调用上面的公式
     * @param buildPositionInterval 建仓间隔 redis获取
     * @param averagePrice 拟买入均价 调用上面公式
     * @return 是否买
     */

    public boolean isBuy(int orderNumber, int maxOrderNumber,
                         double averagePosition, double buildPositionInterval,double averagePrice,
                         double followLowerRatio,double followCallbackRatio,double minAveragePrice,
                         double firstOrderPrice
                         ) {
        //是否需要判断？ 达到最大交易单数？
        if ( orderNumber == maxOrderNumber ) {
            return false;
        }
        //是否为第一单？ 第一单直接购买
        if ( orderNumber == 0 ) {
            return true;
        }
        //设置策略时现价是否小于等于整体持仓均价-建仓间隔*最大建仓数减1？
        if ( firstOrderPrice > (buildPositionInterval*(maxOrderNumber-1)) ) {
            return false;
        }

        //计算拟买入均价 参数传入

        //追踪下调比
        //获取下调均价 下调均价=(整体持仓均价-建仓间隔)-(整体持仓均价*追踪下调比)
        double lowerAveragePrice = (averagePosition - buildPositionInterval) - (averagePosition*followLowerRatio);

        //拟买入均价小于等于下调均价？ 触发追踪建仓
        if (averagePrice > lowerAveragePrice) {
            return false;
        }

        //TODO 记录最小拟买入均价

        //计算回调均价 回调均价=最小均价+整体持仓均价*追踪回调比
        double callbackAveragePrice = minAveragePrice + averagePosition*followCallbackRatio;

        //拟买入均价是否大于等于回调均价？ 是则确定买入
        if ( averagePrice >= callbackAveragePrice ) {
            return true;
        }
        return false;
    }

    /**
     *  确定卖的逻辑
     * @param positionPrice 持仓费用
     * @param stopProfitType 止盈方式
     * @param stopProfitPrice 止盈金额
     * @param stopProfitRatio 止盈比例
     * @param realTimeEarningRatio 实时收益比
     * @param triggerRatio 触发比例
     * @param callBackRatio 回调比例
     * @param redisKey 查询redis用的key
     * @return 是否卖
     */

    public boolean isSell(double positionPrice, int stopProfitType, double stopProfitPrice
            ,double stopProfitRatio ,double realTimeEarningRatio,double triggerRatio, double callBackRatio,String redisKey) {
        //读取止盈金额和止盈比例，两种止盈方式，达到一种即可 参数传入
        //判断是否启用追踪止盈 if-else  1为追踪止盈，2为固定止盈
        boolean isStopProfitTrace = stopProfitType==1 ;
        //判断是否开启金额止盈
        boolean isStopProfitPrice = stopProfitPrice!=1 ;


        if (isStopProfitTrace) {
            //追踪止盈逻辑
            //收益比≥1+触发比例？ 追踪止盈
            if (realTimeEarningRatio >= (1 + triggerRatio)) {
            //if (true) {
                //记录实时收益比的最高数值
                updateRedisString(redisKey,"history_max_riskBenefitRatio",realTimeEarningRatio);

                JSONObject jsonObject = JSON.parseObject(redisTemplate.opsForValue().get(redisKey).toString());
                double maxEarningRation = Double.valueOf(jsonObject.get("history_max_riskBenefitRatio").toString());
                //实时收益比≤最高实时收益比-回降比例？ 确定卖出
                if (realTimeEarningRatio <= (maxEarningRation-callBackRatio)) {
                    return true;
                }
            }
        } else {
            //收益比≥1+止盈比例？ //确定卖出
            if (realTimeEarningRatio > (1 + stopProfitRatio)) {
                return true;
            }

        }
        //金额止盈
        if (isStopProfitPrice) {
            if ( (positionPrice * (realTimeEarningRatio-1)) >= stopProfitPrice ) {
                return true;
            }
        }
        return false;
    }

    private void updateRedisString(String redisKey,String valueKey, Double newValue) {
        JSONObject jsonObject = JSON.parseObject(redisTemplate.opsForValue().get(redisKey).toString());
        String replace = jsonObject.replace(valueKey, newValue).toString();
        redisTemplate.opsForValue().set(redisKey,jsonObject.toJSONString());
    }


}