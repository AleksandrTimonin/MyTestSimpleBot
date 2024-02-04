package aa.timonin.service.impl;

import aa.timonin.entity.RawData;
import aa.timonin.repository.RawDataRepository;
import aa.timonin.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository repository;
    private final ProducerServiceImpl producerService;

    public MainServiceImpl(RawDataRepository repository, ProducerServiceImpl producerService) {
        this.repository = repository;
        this.producerService = producerService;
    }
    
    @Override
    public void processTextMessage(Update update) {

        Long id = saveRawData(update);
        SendMessage answer = new SendMessage();
        answer.setText("Сообщение получено ..");
        answer.setChatId(update.getMessage().getChatId());
        producerService.produceAnswer(answer);
        log.debug(repository.findById(id).orElseThrow().getEvent().getMessage().getText());

    }

    private Long saveRawData(Update update) {
        RawData rawData = RawData.builder()
                                .event(update)
                                .build();
        return repository.save(rawData).getId();

    }
}
