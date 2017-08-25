package BotEx.tlgrm;

import BotEx.statPicture.Drawer;
import org.json.simple.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ChatThread implements Runnable{
    private Update update;
    private String mode = "sign";
    private String lang = "rus";
    File outputFile;
    long chat_id;
    private TelegramLongPollingBot bot;
    private HttpExecuter httpExecuter= HttpExecuter.getHttpExecuter();
    private JsonRecoursiveParser parser= JsonRecoursiveParser.getParser();
    String hostingImg;

    public static ChatThread getChatThread(Update update, TelegramLongPollingBot bot) {
        return new ChatThread(update, bot);
    }


    @Override
    public void run() {
        List<PhotoSize> LPhS= update.getMessage().getPhoto();
        String file_Id = LPhS.get(LPhS.size()-1).getFileId();
        String file_path =null;
        String file_link = null;
        String getFilePath = String.format(MyBot.API_GET_FILE_PATH_LINK,MyBot.TOKEN,file_Id);
        file_path =parser.JsonFindByKey(MyBot.API_FILE_PATH, httpExecuter.requestForStream(getFilePath));
        file_link = String.format(MyBot.API_GET_FILE_LINK,MyBot.TOKEN,file_path);

        // Uploading resized to 400 pixel width photo and getting it's link from json response
        InputStream jsonStreamFromImgHosting= httpExecuter.requestForStream(String.format(MyBot.API_DOWNLOAD_IMG_LINK, file_link));
        hostingImg = parser.JsonFindByKey(MyBot.API_IMG_PATH, jsonStreamFromImgHosting);

        doSomeWork(hostingImg);
        doInTheEnd();

    }
// todo make mode enum
    private void doSomeWork(String link) {
        System.out.println(mode+link);
        if (mode =="parse"){
            System.out.println("Это точно парс");
            doParse(link);
        }
        if (mode =="sign"){
            System.out.println("Это точно сайн");
            doSign(link);
        }
    }

    private void doSign(String link) {
        System.out.println("signing");
        InputStream jsonStreamFromParserAPI = httpExecuter.requestForStream(String.format(MyBot.API_OCR_PARSE_OVERLAY, link,getLang()));
        int x, y, width, height;
        try {
            JSONObject obj = parser.JsonFindByValue(MyBot.MATCH_TEMPLATE, jsonStreamFromParserAPI);
            x = (((HashMap<String, Double>)obj).get("Left")).intValue();
            y = (((HashMap<String, Double>)obj).get("Top")).intValue();
            width = (((HashMap<String, Double>)obj).get("Width")).intValue();
            height = (((HashMap<String, Double>)obj).get("Height")).intValue();
        } catch (NullPointerException e){
            e.printStackTrace();
            failTask();
            return;
        }

        outputFile = new File(MyBot.STANDART_FILE_NAME+chat_id);
        Drawer drawer = new Drawer(link, WaterMarkService.getRandomWatermark());
        drawer.addImageWatermark((x+width+height),y-(height/2), height);
        drawer.getResultToFile(outputFile);
        SendPhoto photoMessage = new SendPhoto() // Create a message object object
                .setChatId(chat_id)
                .setNewPhoto(outputFile);
        try {
            bot.sendPhoto(photoMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void doParse(String link) {
        System.out.println("parsing");
        InputStream jsonStreamFromParserAPI = httpExecuter.requestForStream(String.format(MyBot.API_OCR_PARSE, link,getLang()));
        String result =parser.JsonFindByKey(MyBot.API_OCR_PATH, jsonStreamFromParserAPI);
        System.out.println(result.substring(0,20));
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(result);
        try {
            bot.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void failTask(){
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(MyBot.ON_FAIL_MESSAGE);
        try {
            bot.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void doInTheEnd() {
        System.out.println("end");
        ((MyBot)bot).deleteFromChatThreads(chat_id);
        if (outputFile!=null)outputFile.delete();
    }

    private ChatThread(Update update, TelegramLongPollingBot bot) {
        this.update = update;
        this.bot = bot;
        this.chat_id = update.getMessage().getChatId();
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

    public ChatThread setMode(String mode) {
        this.mode = mode;
        return this;
    }




}
