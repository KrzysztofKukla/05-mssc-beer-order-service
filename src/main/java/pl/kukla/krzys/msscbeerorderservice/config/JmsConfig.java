package pl.kukla.krzys.msscbeerorderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * @author Krzysztof Kukla
 */
@Configuration
public class JmsConfig {

    public static final String MY_QUEUE = "Hello new world QUEUE";
    public static final String MY_SEND_RECEIVE_QUEUE = "replyBackToMe QUEUE";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order-queue";
    public static final String VALIDATE_ORDER_RESPONSE_QUEUE = "valid-order-response-queue";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order-queue";
    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate-order-response-queue";
    public static final String ALLOCATE_FAILURE_QUEUE = "allocation-failure-queue";

    //when we send the message to JMS, Spring converts that message to JMS text message
    //and the payload takes Java object and converts to JSON payload
    //conversion between JMS message and from Java Object into serialized JSON into JMS message
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
