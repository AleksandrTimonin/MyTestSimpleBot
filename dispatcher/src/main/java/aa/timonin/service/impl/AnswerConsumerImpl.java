package aa.timonin.service.impl;

import aa.timonin.controller.UpdateController;
import aa.timonin.service.AnswerConsumer;
import lombok.extern.log4j.Log4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static aa.timonin.RabbitQueue.ANSWER_MESSAGE_QUEUE;

@Service
@Log4j
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE_QUEUE)
    public void consume(SendMessage sendMessage) {
        log.debug(" Получил сообщение из брокера : " + sendMessage.getText());
        updateController.setView(sendMessage);

    }
}
