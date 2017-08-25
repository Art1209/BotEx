package BotEx.tlgrm;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBot extends TelegramLongPollingBot {
    public static final String WATERMARK_LINK = "http:\\/\\/sm.uploads.im\\/ep2Xi.png";
    public static final String API_DOWNLOAD_IMG_LINK ="http://uploads.im/api?upload=%s";
    public static final String API_IMG_PATH ="img_url";
    public static final String API_OCR_PATH ="ParsedText";
    public static final String API_OCR_GET_LINK ="https://api.ocr.space/parse/imageurl?apikey=c9f49f68ca88957&url=%s&language=%s";
    public static final String LANG_CHANGE_SUCCESS = "язык изменен на %s";
    public static final String API_FILE_PATH = "file_path";
    public static final String API_GET_FILE_PATH_LINK = "https://api.telegram.org/bot%s/getFile?file_id=%s";
    public static final String API_GET_FILE_LINK = "https://api.telegram.org/file/bot%s/%s";
    public static final String TOKEN ="449406097:AAFZ4ZN8LGsfdZSZ9SBNJLwYCsNKUVbq5Hs";
    public static final String TARGET_FILE ="C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\result.jpg";
    public static final String[] LANGS = {"rus", "eng"};
    private Map<Long,ChatThread> chatThreads = new HashMap<>();
    ExecutorService exec = Executors.newFixedThreadPool(10);



    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has TEXT
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String message_text = update.getMessage().getText();
            if (needNewChatThread(message_text, chat_id)){
                chatThreads.put(chat_id,ChatThread.getChatThread(update,this).setLang(message_text.toLowerCase()));
                message_text = String.format(LANG_CHANGE_SUCCESS, message_text);
            }
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text);
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        // We check if the update has a message and the message is PHOTO
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            long chat_id = update.getMessage().getChatId();
            if (!chatThreads.containsKey(chat_id)){
                chatThreads.put(chat_id,ChatThread.getChatThread(update,this));
            }
            exec.execute(chatThreads.get(chat_id).setUpdate(update));
        }
    }

    private boolean needNewChatThread(String msg, long id) {
        if (containsIgnoreCase(LANGS, msg)&&!chatThreads.containsKey(id))return true;
        return false;
    }

    public boolean containsIgnoreCase(String[] list, String key){
        for (String str:list) {
            if (str.equalsIgnoreCase(key)) return true;
        } return false;
    }

    public boolean deleteFromChatThreads(long id){
        if (chatThreads.containsKey(id)){
            chatThreads.remove(id);
            return true;
        }return false;
    }

    public String getBotUsername() {
        return "EdRoSignerBot";
    }

    @Override
    public String getBotToken() {
        return "449406097:AAFZ4ZN8LGsfdZSZ9SBNJLwYCsNKUVbq5Hs";
    }
}
