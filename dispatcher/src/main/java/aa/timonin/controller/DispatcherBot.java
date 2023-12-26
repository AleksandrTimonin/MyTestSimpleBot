package aa.timonin.controller;



import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j
@Component
public class DispatcherBot extends TelegramLongPollingBot {



    @Value("${bot.token}")
    private String token;
    @Value("${bot.name}")
    private String name;



    @Override
    public void onUpdateReceived(Update update) {
        var originalMessage = update.getMessage();
        log.debug(originalMessage.getText());
        var response = new SendMessage();
        response.setText(originalMessage.getChatId().toString());

        response.setChatId(originalMessage.getChatId());
        sendMessage(response);



    }


    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
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
}
