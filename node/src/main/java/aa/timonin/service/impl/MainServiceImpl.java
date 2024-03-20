package aa.timonin.service.impl;

import aa.timonin.entity.RawData;
import aa.timonin.repository.RawDataRepository;
import aa.timonin.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import timonin.aa.entity.AppUser;
import timonin.aa.entity.enums.UserState;
import timonin.aa.repository.AppUserRepository;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository repository;
    private final ProducerServiceImpl producerService;
    private final AppUserRepository appUserRepository;

    public MainServiceImpl(RawDataRepository repository, ProducerServiceImpl producerService, AppUserRepository appUserRepository) {
        this.repository = repository;
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
    }
    
    @Override
    public void processTextMessage(Update update) {
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

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
    private AppUser findOrSaveAppUser(User telegramUser){
        AppUser persistentAppUser = appUserRepository.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    //TODO: изменить поле isActive и state  когда допишу червис регистрации
                    .isActive(true)
                    .state(UserState.BASIC_STATE)
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser;

    }
}
