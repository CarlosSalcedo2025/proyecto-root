package org.quind.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.quind.orderservice.infrastructure.adapter.out.persistence.R2dbcOrderRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.R2dbcOrderItemRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "org.quind.orderservice.infrastructure.adapter.out.persistence", includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        R2dbcOrderRepository.class,
        R2dbcOrderItemRepository.class }))
@EnableReactiveMongoRepositories(basePackages = "org.quind.orderservice.infrastructure.adapter.out.persistence", includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MongoOrderEventRepository.class))
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
