package cn.bosenkeji;

import cn.bosenkeji.messaging.OrderSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author CAJR
 * @date 2019/12/26 7:24 下午
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableBinding({OrderSink.class})
@EnableDiscoveryClient
@EnableFeignClients("cn.bosenkeji.service")
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class,args);
    }
}
