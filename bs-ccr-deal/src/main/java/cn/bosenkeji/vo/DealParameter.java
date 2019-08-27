package cn.bosenkeji.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * deal所需参数
 * @author hjh
 */

public class DealParameter {

    private String redisKey;
    private JSONObject jsonObject;

    private String accessKey;
    private String SecretKey;
    private String symbol;

    private Integer canSendMsgToNode; //是否给node端发送消息
    private Double positionCost; //持仓费用
    private Double positionNum; //持仓数量

    //买需要的参数
    private Integer finishedOrder; //已做单数
    private Integer maxTradeOrder; //最大交易单数
    private Double storeSplit; //建仓间隔

    private Map<Double,Double> buyVolume; //买入量
    private Double followLowerRatio; //追踪下调比
    private Double followCallbackRatio; //追踪回调比

    private Double minAveragePrice; //最小交易均价
    private Double firstOrderPrice; //首单现价


    //卖需要的参数
    private Double targetProfitPrice; //止盈金额
    private Integer isUseFollowTargetProfit;  //是否使用追踪止盈
    private Double turnDownRatio; //追踪止盈触发比例
    private Double emitRatio; //追踪止盈回调比例


    public DealParameter() { }

    public DealParameter(String accessKey, String secretKey, String symbol,
                         Integer canSendMsgToNode, Double positionCost, Double positionNum,
                         Integer finishedOrder, Integer maxTradeOrder, Double storeSplit,
                         Map<Double, Double> buyVolume, Double followLowerRatio, Double followCallbackRatio,
                         Double minAveragePrice, Double firstOrderPrice, Double targetProfitPrice,
                         Integer isUseFollowTargetProfit, Double turnDownRatio, Double emitRatio,
                         String redisKey, JSONObject jsonObject) {
        this.accessKey = accessKey;
        SecretKey = secretKey;
        this.symbol = symbol;
        this.canSendMsgToNode = canSendMsgToNode;
        this.positionCost = positionCost;
        this.positionNum = positionNum;
        this.finishedOrder = finishedOrder;
        this.maxTradeOrder = maxTradeOrder;
        this.storeSplit = storeSplit;
        this.buyVolume = buyVolume;
        this.followLowerRatio = followLowerRatio;
        this.followCallbackRatio = followCallbackRatio;
        this.minAveragePrice = minAveragePrice;
        this.firstOrderPrice = firstOrderPrice;
        this.targetProfitPrice = targetProfitPrice;
        this.isUseFollowTargetProfit = isUseFollowTargetProfit;
        this.turnDownRatio = turnDownRatio;
        this.emitRatio = emitRatio;
        this.redisKey = redisKey;
        this.jsonObject = jsonObject;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return SecretKey;
    }

    public void setSecretKey(String secretKey) {
        SecretKey = secretKey;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getCanSendMsgToNode() {
        return canSendMsgToNode;
    }

    public void setCanSendMsgToNode(Integer canSendMsgToNode) {
        this.canSendMsgToNode = canSendMsgToNode;
    }

    public Double getPositionCost() {
        return positionCost;
    }

    public void setPositionCost(Double positionCost) {
        this.positionCost = positionCost;
    }

    public Double getPositionNum() {
        return positionNum;
    }

    public void setPositionNum(Double positionNum) {
        this.positionNum = positionNum;
    }

    public Integer getFinishedOrder() {
        return finishedOrder;
    }

    public void setFinishedOrder(Integer finishedOrder) {
        this.finishedOrder = finishedOrder;
    }

    public Integer getMaxTradeOrder() {
        return maxTradeOrder;
    }

    public void setMaxTradeOrder(Integer maxTradeOrder) {
        this.maxTradeOrder = maxTradeOrder;
    }

    public Double getStoreSplit() {
        return storeSplit;
    }

    public void setStoreSplit(Double storeSplit) {
        this.storeSplit = storeSplit;
    }

    public Map<Double, Double> getBuyVolume() {
        return buyVolume;
    }

    public void setBuyVolume(Map<Double, Double> buyVolume) {
        this.buyVolume = buyVolume;
    }

    public Double getFollowLowerRatio() {
        return followLowerRatio;
    }

    public void setFollowLowerRatio(Double followLowerRatio) {
        this.followLowerRatio = followLowerRatio;
    }

    public Double getFollowCallbackRatio() {
        return followCallbackRatio;
    }

    public void setFollowCallbackRatio(Double followCallbackRatio) {
        this.followCallbackRatio = followCallbackRatio;
    }

    public Double getMinAveragePrice() {
        return minAveragePrice;
    }

    public void setMinAveragePrice(Double minAveragePrice) {
        this.minAveragePrice = minAveragePrice;
    }

    public Double getFirstOrderPrice() {
        return firstOrderPrice;
    }

    public void setFirstOrderPrice(Double firstOrderPrice) {
        this.firstOrderPrice = firstOrderPrice;
    }

    public Double getTargetProfitPrice() {
        return targetProfitPrice;
    }

    public void setTargetProfitPrice(Double targetProfitPrice) {
        this.targetProfitPrice = targetProfitPrice;
    }

    public Integer getIsUseFollowTargetProfit() {
        return isUseFollowTargetProfit;
    }

    public void setIsUseFollowTargetProfit(Integer isUseFollowTargetProfit) {
        this.isUseFollowTargetProfit = isUseFollowTargetProfit;
    }

    public Double getTurnDownRatio() {
        return turnDownRatio;
    }

    public void setTurnDownRatio(Double turnDownRatio) {
        this.turnDownRatio = turnDownRatio;
    }

    public Double getEmitRatio() {
        return emitRatio;
    }

    public void setEmitRatio(Double emitRatio) {
        this.emitRatio = emitRatio;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}