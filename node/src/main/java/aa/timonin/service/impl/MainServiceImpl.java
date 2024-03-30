package aa.timonin.service.impl;

import aa.timonin.entity.AppDocument;
import aa.timonin.entity.RawData;
import aa.timonin.exceptions.UploadFileException;
import aa.timonin.repository.RawDataRepository;
import aa.timonin.service.FileService;
import aa.timonin.service.MainService;
import aa.timonin.service.enums.ServiceCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import aa.timonin.entity.AppUser;
import aa.timonin.repository.AppUserRepository;


import static aa.timonin.service.enums.ServiceCommands.*;
import static aa.timonin.entity.enums.UserState.BASIC_STATE;
import static aa.timonin.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository repository;
    private final ProducerServiceImpl producerService;
    private final AppUserRepository appUserRepository;
    private final FileService fileService;

    public MainServiceImpl(RawDataRepository repository, ProducerServiceImpl producerService, AppUserRepository appUserRepository, FileService fileService) {
        this.repository = repository;
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
        this.fileService = fileService;
    }
    
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";
        ServiceCommands command = ServiceCommands.fromValue(text);
        if (CANCEL.equals(command)){
            output = cancelProcess(appUser);
        }else if(BASIC_STATE.equals(userState)){
            output = processServiceCommand(appUser, text);
        }else if(WAIT_FOR_EMAIL_STATE.equals((userState))){
            //TODO : добавить реализацию процесса регистрации
        }else {
            log.error("Unknown user state or something else");
            output = "Неизвестная ошибка : введите /cancel и попробуйте снова";
        }
        var chatId = update.getMessage().getChatId();
        sendAnswer(chatId,output);

        Long id = saveRawData(update);
        SendMessage answer = new SendMessage();
        answer.setText("Сообщение получено ..");
        answer.setChatId(update.getMessage().getChatId());
        producerService.produceAnswer(answer);
        log.debug(repository.findById(id).orElseThrow().getEvent().getMessage().getText());

    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowedToSendContent(chatId,appUser)){
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            //todo добавить генерацию ссылки для скачивания
            var answer = "Документ успешно загружен" + "ссылка для скачивания : https://fl.telegr.ru/getDoc/qolwyiuefgladsbcajubcfvoaugvf7816235";
            sendAnswer(chatId,answer);

        }catch (UploadFileException e ){
            log.error(e);
            sendAnswer(chatId,"к сожалению что-то пошло не так, попробуйте позже ");

        }

    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {

        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свой аккаунт";
            sendAnswer(chatId,error);
            return true;
        }else if(!BASIC_STATE.equals(appUser.getState())){
            var error = "некоректный ввод, используйте команду /cancel для отправки файлов";
            sendAnswer(chatId,error);
            return true;
        }
        return false;

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowedToSendContent(chatId,appUser)){
            return;
        }
        //todo: добавить сохранение фото
        var answer = "Фото успешно загружено : ссылка для скачивания https://test.ru/get-photo/12^$##%*$&532#$%^&";
        sendAnswer(chatId,answer);
    }

    private void sendAnswer(Long chatId, String output) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }


    private String processServiceCommand(AppUser appUser, String text) {
        if(REGISTRATION.equals(text)){
            return "Извините контент временно недоступен";

        }else if(START.equals(text)){
            return "Привествую, список команд можно посмотреть набрав /help";
        }else if(HELP.equals(text)){
            return help();

        }else {
            return "неизвестная команда попробуйте набрать /help";

        }
    }

    private String help() {
        return "Список доступных команд : \n" +
                "/help : выводит список доступных команд\n" +
                "/cancel : сбрасывает работу с ботом до исходного стостояния \n" +
                "/registration : запускает процесс регистрации пользователя \n";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "команда отменена";

    }

    private Long saveRawData(Update update) {
        RawData rawData = RawData.builder()
                                .event(update)
                                .build();
        return repository.save(rawData).getId();

    }
    private AppUser findOrSaveAppUser(Update update){
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
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
                    .state(BASIC_STATE)
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser;

    }
}
