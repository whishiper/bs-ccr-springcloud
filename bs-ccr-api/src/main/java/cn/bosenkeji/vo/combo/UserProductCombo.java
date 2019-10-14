package cn.bosenkeji.vo.combo;

import cn.bosenkeji.vo.User;
import cn.bosenkeji.vo.combo.ProductCombo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.sql.Timestamp;

@Api
@JsonIgnoreProperties(value = {"handler"})
public class UserProductCombo implements Serializable {
    private int id;

    private int userId;

    private String orderNumber;

    private int productComboId;

    private String remark;

    private int status;

    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;

    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;

    //非数据库属性 剩余时长
    @ApiModelProperty(hidden = true)
    private int remainTime=0;

    private String redisKey;
    private int redisKeyId;

    //一对一
    @ApiModelProperty(hidden = true)
    private ProductCombo productCombo;

    @ApiModelProperty(hidden = true)
    private User user;

    @ApiModelProperty(hidden = true)
    private ComboRedisKey comboRedisKey;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public ProductCombo getProductCombo() {
        return productCombo;
    }

    public void setProductCombo(ProductCombo productCombo) {
        this.productCombo = productCombo;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public UserProductCombo() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getProductComboId() {
        return productComboId;
    }

    public void setProductComboId(int productComboId) {
        this.productComboId = productComboId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public int getRedisKeyId() {
        return redisKeyId;
    }

    public void setRedisKeyId(int redisKeyId) {
        this.redisKeyId = redisKeyId;
    }

    public ComboRedisKey getComboRedisKey() {
        return comboRedisKey;
    }

    public void setComboRedisKey(ComboRedisKey comboRedisKey) {
        this.comboRedisKey = comboRedisKey;
    }

    @Override
    public String toString() {
        return "UserProductCombo{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderNumber='" + orderNumber + '\'' +
                ", productComboId=" + productComboId +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", remainTime=" + remainTime +
                ", redisKey='" + redisKey + '\'' +
                ", redisKeyId=" + redisKeyId +
                ", productCombo=" + productCombo +
                ", user=" + user +
                ", comboRedisKey=" + comboRedisKey +
                '}';
    }
}