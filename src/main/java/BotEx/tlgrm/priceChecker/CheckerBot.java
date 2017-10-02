package BotEx.tlgrm.priceChecker;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CheckerBot extends TelegramLongPollingBot {
    static{PhantomJsDriverManager.getInstance().setup();}
    static Timer timer = new Timer();
    static List<PriceChecker> checkers = new ArrayList<>();

    public static final String TOKEN ="402286704:AAGYjEK4OOZynmmyc9fRXxaQNbuwmAQA22U";
    State state = State.WaitCommand;
    PriceChecker currentChecker;


    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            String text = message.getText();
            switch (state) {
                case WaitCommand: {
                    if (text.equalsIgnoreCase("new")) {
                        currentChecker = new PriceChecker(this, message.getChatId());
                        state = State.WaitLink;
                        break;
                    }
                }
                case WaitLink: {
                    currentChecker.setLink(text);
                    checkers.add(currentChecker);
                    timer.schedule(currentChecker, 500);
                    state = State.WaitCommand;
                    break;
                }
            }
            sendStringMessage(message.getChatId(), state+"  "+text);

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
        WaitCommand,WaitLink;

    }

}
