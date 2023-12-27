package aa.timonin.service;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void produce(String queueName, Update update);
}
