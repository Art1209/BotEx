package BotEx.tlgrm;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import BotEx.statPicture.Drawer;
import java.util.List;

public class ChatThread implements Runnable{
    private Update update;
    private String lang = "rus";
    private TelegramLongPollingBot bot;
    private HttpExecuter httpExecuter= HttpExecuter.getHttpExecuter();

    public static ChatThread getChatThread(Update update, TelegramLongPollingBot bot) {
        ChatThread chat = new ChatThread(update, bot);
        return chat;
    }


    @Override
    public void run() {
        List<PhotoSize> LPhS= update.getMessage().getPhoto();
        String file_Id = LPhS.get(LPhS.size()-1).getFileId();
        String file_path =null;
        String file_link = null;
        String getFilePath = String.format(MyBot.API_GET_FILE_PATH_LINK,MyBot.TOKEN,file_Id);
        file_path =httpExecuter.JsonFindByKey(MyBot.API_FILE_PATH,httpExecuter.makeRequestGetJson(getFilePath));
        file_link = String.format(MyBot.API_GET_FILE_LINK,MyBot.TOKEN,file_path);

        Drawer drawer = new Drawer(file_link);
        java.io.File targetFile = new java.io.File(MyBot.TARGET_FILE);
        drawer.setDestImageFile(targetFile);
        drawer.addImageWatermark(0.3);
        long chat_id = update.getMessage().getChatId();

        SendPhoto photoMessage = new SendPhoto() // Create a message object object
                .setChatId(chat_id)
                .setNewPhoto(targetFile);
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(textParser(file_link));
        try {
            bot.sendPhoto(photoMessage);
            bot.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        ((MyBot)bot).deleteFromChatThreads(chat_id);
    }

    private ChatThread(Update update, TelegramLongPollingBot bot) {
        this.update = update;
        this.bot = bot;
    }

    public String textParser(String inputImg){
        String hostingImg = httpExecuter.JsonFindByKey(MyBot.API_IMG_PATH,httpExecuter.makeRequestGetJson(String.format(MyBot.API_DOWNLOAD_IMG_LINK, inputImg)));
        String result =httpExecuter.JsonFindByKey(MyBot.API_OCR_PATH,httpExecuter.makeRequestGetJson(String.format(MyBot.API_OCR_GET_LINK, hostingImg,getLang())));
        return result;
    }

    public ChatThread setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public ChatThread setUpdate(Update update) {
        this.update = update;
        return this;
    }


}
