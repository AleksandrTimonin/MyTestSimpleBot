package aa.timonin.controller;

import aa.timonin.service.UpdateProducer;
import aa.timonin.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static aa.timonin.RabbitQueue.*;

@Log4j
@Component
public class UpdateController {

    private DispatcherBot dispatcherBot;
    private MessageUtils messageUtils;

    private UpdateProducer updateProducer;
    public void registerBot(DispatcherBot dispatcherBot){
        log.debug( " метод РЕГИСТР БОТ в АПДЕЙТ КОНТРОЛЛЕР ");
        this.dispatcherBot = dispatcherBot;

    }

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }
        if(update.getMessage() != null){
            distributeMessageByType(update);

        }else {
            log.warn("Received not supported type message");
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if(message.getText() != null){
            processTextMessage(update);
        }else if(message.getPhoto() != null){
            processPhotoMessage(update);
        }else if(message.getDocument() != null){
            processDocMessage(update);

        }else {
            sendUnsupportedMessage(update);
        }

    }

    private void sendUnsupportedMessage(Update update) {
        SendMessage sendMessage = prepareSendMessage(update,"Формат сообщения не поддерживается");
        setView(sendMessage);
    }

    private SendMessage prepareSendMessage(Update update, String message) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,message);
        return sendMessage;
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_UPDATE_QUEUE,update);
        var sendMessage = prepareSendMessage(update, "Документ получен, обрабатывается...");
        setView(sendMessage);


    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_UPDATE_QUEUE,update);
        var sendMessage = prepareSendMessage(update, "Фото получено, обрабатывается...");
        setView(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_UPDATE_QUEUE,update);
        var sendMessage = prepareSendMessage(update, "Сообщение получено, обрабатывается...");
        setView(sendMessage);
    }
    private void setView(SendMessage sendMessage) {
        dispatcherBot.sendAnswerMessage(sendMessage);
    }
}
