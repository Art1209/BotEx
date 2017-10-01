package BotEx.tlgrm.priceChecker;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class CheckerBot extends TelegramLongPollingBot {


    public static final String TOKEN ="402286704:AAGYjEK4OOZynmmyc9fRXxaQNbuwmAQA22U";
    State state = State.WaitCommand;

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            state = state.iterate(message.getText());
            sendStringMessage(message.getChatId(), state+"  "+message.getText());
        }
    }

    void sendStringMessage(long chatId, String textMessage){
        SendMessage sendMessage = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText(textMessage);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "Shopper1Bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
    enum State{
        WaitCommand {
            @Override
            State iterate(String str) {
                if (str.equalsIgnoreCase("new")){
                    currentChecker = new PriceChecker();
                    return State.WaitLink;
                } else return this;

            }
        },WaitLink {
            @Override
            State iterate(String str) {
                currentChecker.setLink(str);
                timer.schedule(currentChecker,500);
                return WaitCommand;
            }
        };
        static Timer timer = new Timer();
        static PriceChecker currentChecker;
        List<PriceChecker> checkers = new ArrayList<>();
        abstract State iterate(String str);
    }

}
