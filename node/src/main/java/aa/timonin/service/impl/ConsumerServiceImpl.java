package aa.timonin.service.impl;

import aa.timonin.service.ConsumerService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static aa.timonin.RabbitQueue.*;

@Log4j
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerServiceImpl  producerService;

    public ConsumerServiceImpl(ProducerServiceImpl producerService) {
        this.producerService = producerService;
    }

    @Override
    @RabbitListener(queues = TEXT_UPDATE_QUEUE)
    public void consumeTextMessageUpdates(Update update) {

        var message = update.getMessage();
        log.debug("NODE: text received " + message.getText());
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from NODE");
        producerService.produceAnswer(sendMessage);

    }

    @Override
    @RabbitListener(queues = DOC_UPDATE_QUEUE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: doc received");

    }

    @Override
    @RabbitListener(queues = PHOTO_UPDATE_QUEUE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: photo received");

    }
}
