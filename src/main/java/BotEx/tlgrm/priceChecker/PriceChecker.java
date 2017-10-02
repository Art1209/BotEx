package BotEx.tlgrm.priceChecker;

import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.TimerTask;

@Log4j
public class PriceChecker extends TimerTask{
//    private static String PJS_EXE_PATH = "C:\\Users\\aalbutov\\.m2\\repository\\webdriver\\phantomjs\\windows\\2.1.1\\phantomjs.exe";

    public PriceChecker(CheckerBot bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    private CheckerBot bot;
    long chatId;
    PhantomJSDriver driver;
    private String link;

    @Override
    public void run() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
//        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PJS_EXE_PATH);
//        driver = new HtmlUnitDriver(true);
        driver = new PhantomJSDriver(caps);
        driver.get(link);
        String result = driver.findElement(By.id("stockTag")).getText();
        log.info(link+" "+result);
        if (!result.contains("out"))bot.sendStringMessage(chatId, "in stock "+link);
        PriceChecker nextChecker = new PriceChecker(bot, chatId);
        nextChecker.setLink(link);
        CheckerBot.timer.schedule(nextChecker, 15000);
    }
    void setLink(String str){
        this.link = str;
    }
}
