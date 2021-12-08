package per.hp.hospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("per.hp")
@MapperScan("per.hp.hospital.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "per.hp")
public class HospitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class,args);
    }
}
