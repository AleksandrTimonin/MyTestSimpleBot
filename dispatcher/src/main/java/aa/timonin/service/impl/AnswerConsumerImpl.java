package aa.timonin.service.impl;

import aa.timonin.service.AnswerConsumer;
import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
@Service
@Log4j
public class AnswerConsumerImpl implements AnswerConsumer {

    @Override
    public void consume(SendMessage sendMessage) {

    }
}
