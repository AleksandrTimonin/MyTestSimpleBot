package aa.timonin.controller;



import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Log4j
@Component
public class DispatcherBot extends TelegramLongPollingBot {




    @Value("${bot.name}")
    private String name;


    private final UpdateController updateController;

    public DispatcherBot( @Value("${bot.token}") String botToken, UpdateController updateController) {

        super(botToken);

        this.updateController = updateController;
    }



    @PostConstruct
    private void init(){

        updateController.registerBot(this);
    }



    @Override
    public void onUpdateReceived(Update update) {
        log.debug("получено сообщение : "+ update.getMessage().getText() );
        updateController.processUpdate(update);




    }


    @Override
    public String getBotUsername() {
        return name;
    }


    private void sendMessage(SendMessage message){
        if(message!= null){
            try{
                execute(message);
            }catch (TelegramApiException e){
                log.error(e.getMessage());
            }
        }
    }

    public void sendAnswerMessage(SendMessage response) {

        sendMessage(response);
        log.debug(response.getText() + " : отправлено");

    }
}
