package cn.bosenkeji;


import cn.bosenkeji.messaging.MySink;
import cn.bosenkeji.messaging.MySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableBinding({MySource.class, MySink.class})
@EnableDiscoveryClient
@EnableFeignClients("cn.bosenkeji.service")
public class DealApp {

    public static void main(String[] args) {
        SpringApplication.run(DealApp.class, args);
    }
}
