package cn.bosenkeji.utils;

import cn.bosenkeji.messaging.MySource;
import cn.bosenkeji.vo.DealParameter;
import cn.bosenkeji.vo.RealTimeTradeParameter;
import cn.bosenkeji.vo.RedisParameter;
import cn.bosenkeji.vo.RocketMQResult;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * deal模块通用的方法
 *
 * @author hjh
 */

public class DealUtil {

    private static final Logger log = LoggerFactory.getLogger(DealUtil.class);

    public static final String TRADE_TYPE_BUY = "buy";
    public static final String TRADE_TYPE_SELL = "sell";


    static final String IS_TRIGGER_TRACE_STOP_PROFIT = "is_trigger_trace_stop_profit";  //是否触发追踪止盈
    static final String IS_FOLLOW_BUILD = "is_follow_build";  //是否触发追踪建仓

    static final String MIN_AVERAGE_PRICE = "min_average_price"; //最小拟买入均价
    static final String HISTORY_MAX_BENEFIT_RATIO = "history_max_benefit_ratio"; //历史最高收益比
    private static final String REAL_TIME_EARNING_RATIO = "real_time_earning_ratio"; //实时收益比

    static final String TRIGGER_FOLLOW_BUILD_ORDER = "trigger_follow_build_order"; //触发追踪建仓的单数
    static final String TRIGGER_STOP_PROFIT_ORDER = "trigger_stop_profit_order"; //触发追踪止盈的单数

    /**
     *
     * 清除触发追踪建仓标志
     *
     * @param dealParameter  买卖需要的参数
     * @param redisParameter  java管理redis的数据
     * @param realTimeTradeParameter  实时交易信息
     * @param redisTemplate  操作redis
     *
     */
    public static Boolean isClearTriggerFollowBuild(DealParameter dealParameter, RedisParameter redisParameter, RealTimeTradeParameter realTimeTradeParameter, RedisTemplate redisTemplate) {
        //计算实时拟买入均价
        Double averagePrice = DealCalculator.countAveragePrice(realTimeTradeParameter.getSellDeep(), Double.valueOf(dealParameter.getBuyVolume().get(dealParameter.getFinishedOrder().toString()).toString()));

        //获取下调均价 下调均价=(整体持仓均价-建仓间隔)-(整体持仓均价*追踪下调比)
        Double averagePosition = DealCalculator.countAveragePosition(dealParameter.getPositionCost(),dealParameter.getPositionNum());
        Double lowerAveragePrice = DealCalculator.countLowerAveragePrice(averagePosition,dealParameter.getStoreSplit(),dealParameter.getFollowLowerRatio());


        if (
                (averagePrice - lowerAveragePrice) > 0 && redisParameter.getTriggerFollowBuildOrder().equals(dealParameter.getFinishedOrder()) ||
                        !(redisParameter.getTriggerFollowBuildOrder().equals(dealParameter.getFinishedOrder())) ||
                        (dealParameter.getTradeStatus() == 3)
        ) {
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.IS_FOLLOW_BUILD,"0",redisTemplate);
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.TRIGGER_FOLLOW_BUILD_ORDER,"0",redisTemplate);
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.MIN_AVERAGE_PRICE,"1000000.0",redisTemplate);
            return true;
        }
        return false;
    }

    /**
     * 清除触发追踪止盈标志
     *
     * @param dealParameter  判断买卖需要的参数
     * @param redisParameter  java管理redis的数据
     * @param redisTemplate  操作redis
     */
    public static Boolean isClearTriggerStopProfit(DealParameter dealParameter, RedisParameter redisParameter, RedisTemplate redisTemplate) {
        if (
                (redisParameter.getRealTimeEarningRatio() < 1 && redisParameter.getTriggerStopProfitOrder().equals(dealParameter.getFinishedOrder())) ||
                        !(redisParameter.getTriggerStopProfitOrder().equals(dealParameter.getFinishedOrder())) ||
                        (dealParameter.getTradeStatus() == 3)
        ) {
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.IS_TRIGGER_TRACE_STOP_PROFIT,"0",redisTemplate);
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.TRIGGER_STOP_PROFIT_ORDER,"0",redisTemplate);
            DealCalculator.updateRedisHashValue(redisParameter.getRedisKey(),DealUtil.HISTORY_MAX_BENEFIT_RATIO,"0",redisTemplate);
            return true;
        }
        return false;
    }

    /**
     * 记录实时收益比  REAL_TIME_EARNING_RATIO = "real_time_earning_ratio"
     *
     * @param redisKey redis中hash的key
     * @param hashValue redis中hashKey=REAL_TIME_EARNING_RATIO 对应的hashValue
     * @param redisTemplate redisTemplate
     */
    public static void recordRealTimeEarningRatio(String redisKey, String hashValue, RedisTemplate redisTemplate) {
        redisTemplate.opsForHash().put(redisKey,DealUtil.REAL_TIME_EARNING_RATIO,hashValue);
    }


    /**
     *
     * MQ发送消息
     *
     * @param dealParameter 消息体的数据
     * @param type 买或卖的类型
     * @param source mq发送
     * @return 是否发送消息成功
     */

    public static boolean sendMessage(DealParameter dealParameter,String platformName, String type, MySource source) {
        RocketMQResult rocketMQResult = new RocketMQResult();

        String symbol = dealParameter.getSymbol();
        String signId = dealParameter.getSignId();
        rocketMQResult.setSymbol(symbol);
        rocketMQResult.setSignId(signId);
        rocketMQResult.setType(type);
        rocketMQResult.setFinished_order(dealParameter.getFinishedOrder());
        rocketMQResult.setPlantFormName(platformName);

        JSONObject jsonResult = (JSONObject) JSONObject.toJSON(rocketMQResult);
        Message<String> jsonMessage = MessageBuilder.withPayload(jsonResult.toJSONString()).build();

        boolean b;
        if ("okex".equals(platformName)) {
            b = source.okexOutput().send(jsonMessage);
        } else {
            b = source.huobiOutput().send(jsonMessage);
        }

        return b;
    }


    /**
     *
     * java 操作redis的数据  初始化或获取
     *
     * @param dealParameter 需要交易的参数
     * @param redisTemplate 操作redis
     * @return parameter java 获取redis的结果集
     */

    public static RedisParameter javaRedisParameter(DealParameter dealParameter, String platFormName, RedisTemplate redisTemplate) {

        RedisParameter parameter = new RedisParameter();

        String javaRedisKey = "trade-java_" + dealParameter.getSignId() + "_" + dealParameter.getSymbol();
        if ("okex".equals(platFormName)) {
            javaRedisKey = "okex-" + javaRedisKey;
        }


//        Object o = redisTemplate.opsForValue().get(javaRedisKey);
        Map entries = redisTemplate.opsForHash().entries(javaRedisKey);
        if (CollectionUtils.isEmpty(entries)) {
            //初始化数据
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(IS_FOLLOW_BUILD,"0");
            map.put(IS_TRIGGER_TRACE_STOP_PROFIT,"0");
            map.put(MIN_AVERAGE_PRICE,"1000000.0");
            map.put(HISTORY_MAX_BENEFIT_RATIO,"0.0");
            map.put(REAL_TIME_EARNING_RATIO,"0.0");
            map.put(TRIGGER_FOLLOW_BUILD_ORDER,"0");
            map.put(TRIGGER_STOP_PROFIT_ORDER,"0");

            redisTemplate.opsForHash().putAll(javaRedisKey,map);

            parameter.setRedisKey(javaRedisKey);
            parameter.setIsTriggerTraceStopProfit(0);
            parameter.setIsFollowBuild(0);
            parameter.setMinAveragePrice(1000000.0);
            parameter.setHistoryMaxBenefitRatio(0.0);
            parameter.setRealTimeEarningRatio(0.0);
            parameter.setTriggerFollowBuildOrder(0);
            parameter.setTriggerStopProfitOrder(0);

        } else {
            //获取数据
            parameter.setRedisKey(javaRedisKey);
            parameter.setIsTriggerTraceStopProfit(DealUtil.getInteger(entries.get(IS_TRIGGER_TRACE_STOP_PROFIT)));
            parameter.setIsFollowBuild(DealUtil.getInteger(entries.get(IS_FOLLOW_BUILD)));
            parameter.setMinAveragePrice(DealUtil.getDouble(entries.get(MIN_AVERAGE_PRICE)));
            parameter.setHistoryMaxBenefitRatio(DealUtil.getDouble(entries.get(HISTORY_MAX_BENEFIT_RATIO)));
            parameter.setRealTimeEarningRatio(DealUtil.getDouble(entries.get(REAL_TIME_EARNING_RATIO)));
            parameter.setTriggerFollowBuildOrder(DealUtil.getInteger(entries.get(TRIGGER_FOLLOW_BUILD_ORDER)));
            parameter.setTriggerStopProfitOrder(DealUtil.getInteger(entries.get(TRIGGER_STOP_PROFIT_ORDER)));
        }
        return parameter;
    }




    public static String getString(Object o) {
        return o == null ? "" : o.toString();
    }

    public static Integer getInteger(Object o) {
        Integer temp = 0;
        if (o != null) {
            temp = Integer.valueOf(o.toString());
        }
        return temp;
    }

    public static Double getDouble(Object o) {
        Double temp;
        temp = o == null ? Double.valueOf("0.0") : Double.valueOf(o.toString());
        return temp;
    }
}