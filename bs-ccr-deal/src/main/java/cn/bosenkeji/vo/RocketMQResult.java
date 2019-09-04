package cn.bosenkeji.vo;

/**
 *
 *
 *
 * @author hjh
 *
 */

public class RocketMQResult {

    private String accessKey;
    private String secretKey;
    private String symbol;
    private String type;
    private Integer finishedOrder;

    public RocketMQResult() { }

    @Override
    public String toString() {
        return "RocketMQResult{" +
                "accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", finishedOrder=" + finishedOrder +
                '}';
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFinishedOrder(Integer finishedOrder) {
        this.finishedOrder = finishedOrder;
    }
}