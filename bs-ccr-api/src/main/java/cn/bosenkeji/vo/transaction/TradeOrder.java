package cn.bosenkeji.vo.transaction;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * @author CAJR
 * @date 2019/11/1 4:54 下午
 */
@JsonIgnoreProperties(value = {"handler"})
public class TradeOrder {

    @ApiModelProperty("订单id")
    private int id;

    @JSONField(name="order_group_id")
    @ApiModelProperty("订单组 id")
    private int orderGroupId;

    /**
    * 交易均价
    */
    @JSONField(name = "trade_average_price")
    @ApiModelProperty("交易均价")
    private double tradeAveragePrice;

    /**
    * 交易数量
    */
    @JSONField(name = "trade_numbers")
    @ApiModelProperty("交易数量")
    private double tradeNumbers;

    /**
    * 交易费用
    */
    @JSONField(name = "trade_cost")
    @ApiModelProperty("交易费用")
    private double tradeCost;

    /**
    * 卖出收益
    */
    @JSONField(name = "sell_profit")
    @ApiModelProperty("卖出收益")
    private double sellProfit;

    /**
     * 额外收益
     */
    @JSONField(name = "extra_profit")
    @ApiModelProperty("额外收益")
    private double extraProfit;

    /**
     * 理论建仓价
     */
    @JSONField(name = "theoretical_build_price")
    @ApiModelProperty("理论建仓价")
    private double theoreticalBuildPrice;

    /**
     * 收益比
     */
    @JSONField(name = "profit_ratio")
    @ApiModelProperty("收益比")
    private double profitRatio;

    /**
    * 交易方式
    */
    @JSONField(name = "trade_type")
    @ApiModelProperty("交易方式")
    private int tradeType;

    @ApiModelProperty(value = "状态",hidden = true)
    private int status;

    /**
    * 订单创建时间
    */
    @JSONField(name = "created_at")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp createdAt;

    /**
    * 订单更新时间
    */
    @JSONField(name = "updated_at")
    @ApiModelProperty(value = "订单更新时间",hidden = true)
    private Timestamp updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderGroupId() {
        return orderGroupId;
    }

    public void setOrderGroupId(int orderGroupId) {
        this.orderGroupId = orderGroupId;
    }

    public double getTradeAveragePrice() {
        return tradeAveragePrice;
    }

    public void setTradeAveragePrice(double tradeAveragePrice) {
        this.tradeAveragePrice = tradeAveragePrice;
    }

    public double getTradeNumbers() {
        return tradeNumbers;
    }

    public void setTradeNumbers(double tradeNumbers) {
        this.tradeNumbers = tradeNumbers;
    }

    public double getTradeCost() {
        return tradeCost;
    }

    public void setTradeCost(double tradeCost) {
        this.tradeCost = tradeCost;
    }

    public double getExtraProfit() {
        return extraProfit;
    }

    public double getTheoreticalBuildPrice() {
        return theoreticalBuildPrice;
    }

    public void setTheoreticalBuildPrice(double theoreticalBuildPrice) {
        this.theoreticalBuildPrice = theoreticalBuildPrice;
    }

    public double getProfitRatio() {
        return profitRatio;
    }

    public void setProfitRatio(double profitRatio) {
        this.profitRatio = profitRatio;
    }

    public void setExtraProfit(double extraProfit) {
        this.extraProfit = extraProfit;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getSellProfit() {
        return sellProfit;
    }

    public void setSellProfit(double sellProfit) {
        this.sellProfit = sellProfit;
    }
}