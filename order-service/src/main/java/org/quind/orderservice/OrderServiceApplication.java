package org.quind.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "org.quind.orderservice.infrastructure.adapter.out.persistence", includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = {
        org.quind.orderservice.infrastructure.adapter.out.persistence.R2dbcOrderRepository.class,
        org.quind.orderservice.infrastructure.adapter.out.persistence.R2dbcOrderItemRepository.class }))
@EnableReactiveMongoRepositories(basePackages = "org.quind.orderservice.infrastructure.adapter.out.persistence", includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository.class))
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
