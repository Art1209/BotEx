package BotEx.tlgrm;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;


@SpringBootApplication
public class MainBot{
    public static void main(String[] args) {
//        BufferedImage sourceImage=null;
//        File destImageFile = new File("C:\\Users\\aalbutov\\IdeaProjects\\BotEx\\src\\main\\resources\\result.jpg");
//
//        try {
//            sourceImage = ImageIO.read(new URL("https://api.telegram.org/file/bot449406097:AAFZ4ZN8LGsfdZSZ9SBNJLwYCsNKUVbq5Hs/photos/file_4.jpg"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            ImageIO.write(sourceImage, "png", destImageFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // Initialize Api Context
        ApiContextInitializer.init();


        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(new MyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}