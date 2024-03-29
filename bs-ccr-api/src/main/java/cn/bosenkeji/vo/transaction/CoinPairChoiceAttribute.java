package cn.bosenkeji.vo.transaction;

import cn.bosenkeji.vo.strategy.StrategyOther;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author CAJR
 * @create 2019/7/17 10:10
 */

public class CoinPairChoiceAttribute {
    @ApiModelProperty("自选货币对属性 id")
    private int id;

    @ApiModelProperty("货币对 id")
    private int coinPairChoiceId;

    @ApiModelProperty("预算")
    private double expectMoney;

    @ApiModelProperty("策略 id")
    private int strategyId;

    @ApiModelProperty("是否自定义属性")
    private int isCustom;

    private int status;

    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;

    @ApiModelProperty(hidden = true)
    private Date updatedAt;

    private StrategyOther strategy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoinPairChoiceId() {
        return coinPairChoiceId;
    }

    public void setCoinPairChoiceId(int coinPairChoiceId) {
        this.coinPairChoiceId = coinPairChoiceId;
    }

    public double getExpectMoney() {
        return expectMoney;
    }

    public void setExpectMoney(double expectMoney) {
        this.expectMoney = expectMoney;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public int getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(int isCustom) {
        this.isCustom = isCustom;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StrategyOther getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyOther strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "CoinPairChoiceAttribute{" +
                "id=" + id +
                ", coinPairChoiceId=" + coinPairChoiceId +
                ", expectMoney=" + expectMoney +
                ", strategyId=" + strategyId +
                ", isCustom=" + isCustom +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}