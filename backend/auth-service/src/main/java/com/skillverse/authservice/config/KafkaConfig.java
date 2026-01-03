package com.skillverse.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Kafka topics temporarily disabled - Kafka not available in production
    // Re-enable when Kafka is added to docker-compose.prod.yml

    /*
     * @Bean
     * public NewTopic userCreatedTopic() {
     * return TopicBuilder.name("user.created")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     * 
     * @Bean
     * public NewTopic userUpdatedTopic() {
     * return TopicBuilder.name("user.updated")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     * 
     * @Bean
     * public NewTopic userDeletedTopic() {
     * return TopicBuilder.name("user.deleted")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     * 
     * @Bean
     * public NewTopic userLoginTopic() {
     * return TopicBuilder.name("user.login")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     * 
     * @Bean
     * public NewTopic userLogoutTopic() {
     * return TopicBuilder.name("user.logout")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     * 
     * @Bean
     * public NewTopic userRoleChangedTopic() {
     * return TopicBuilder.name("user.role.changed")
     * .partitions(3)
     * .replicas(1)
     * .build();
     * }
     */
}
