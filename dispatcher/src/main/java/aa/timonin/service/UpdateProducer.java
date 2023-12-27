package aa.timonin.service;


import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void produce(String queueName, Update update);
}
