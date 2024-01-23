package aa.timonin.service.impl;

import aa.timonin.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static aa.timonin.RabbitQueue.ANSWER_MESSAGE_QUEUE;
@Log4j
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        log.debug("сообщение отправляется : " + sendMessage.getText());
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_QUEUE,sendMessage);

    }
}
