package com.bistro.shared.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic reservationConfirmedTopic() {
        return TopicBuilder.name("reservation-confirmed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationConfirmedV2Topic() {
        return TopicBuilder.name("reservation-confirmed-v2")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationRejectedTopic() {
        return TopicBuilder.name("reservation-rejected")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationCancelledTopic() {
        return TopicBuilder.name("reservation-cancelled")
                .partitions(3)
                .replicas(1)
                .build();
    }
}














