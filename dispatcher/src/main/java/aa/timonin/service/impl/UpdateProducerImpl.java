package aa.timonin.service.impl;

import aa.timonin.service.UpdateProducer;
import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {

    @Override
    public void produce(String queueName, Update update) {
        log.debug(update.getMessage().getText());
    }
}
