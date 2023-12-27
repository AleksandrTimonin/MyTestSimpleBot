package aa.timonin.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static aa.timonin.RabbitQueue.*;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue(){
        return new Queue(TEXT_UPDATE_QUEUE);
    }
    @Bean
    public Queue photoMessageQueue(){
        return new Queue(PHOTO_UPDATE_QUEUE);
    }@Bean
    public Queue docMessageQueue(){
        return new Queue(DOC_UPDATE_QUEUE);
    }@Bean
    public Queue answerQueue(){
        return new Queue(ANSWER_MESSAGE_QUEUE);
    }



}
