package com.example.orders.frontend.server.config;

import com.example.orders.contract.OrdersSoapService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FrontendRootConfig {

    @Bean
    public OrdersSoapService ordersSoapService(Environment environment) {
        String address = environment.getProperty("orders.soap.url", "http://localhost:8080/soap/orders");
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(OrdersSoapService.class);
        factory.setAddress(address);
        return (OrdersSoapService) factory.create();
    }
}