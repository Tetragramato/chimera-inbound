package com.tetragramato;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Rabbit MQ.
 *
 * @author vivienbrissat
 * Date: 2019-01-09
 */
@Configuration
public class RabbitConfiguration {

    private String topicExchangeName;

    private String queueName;

    private String routingKey;

    public RabbitConfiguration(@Value("${chimera.amqp.exchange}") final String topicExchangeName,
                               @Value("${chimera.amqp.queue}") final String queueName,
                               @Value("${chimera.amqp.routingKey}") final String routingKey) {
        this.topicExchangeName = topicExchangeName;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    /**
     * Custom ObjectMapper for Jackson.
     * Needed for zonedDateTime format in JSON.
     * @return ObjectMapper
     */
    private ObjectMapper jsonObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(jsonObjectMapper()));
        return rabbitTemplate;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
