package BotEx.tlgrm.priceChecker;

import BotEx.tlgrm.priceChecker.checker.GetAmount;
import BotEx.tlgrm.priceChecker.checker.GetAmountViaJS;
import BotEx.tlgrm.priceChecker.checker.GetAmountViaPhantom;
import lombok.extern.log4j.Log4j;

import java.util.TimerTask;

@Log4j
public class PriceChecker extends TimerTask{
//    private static String PJS_EXE_PATH = "C:\\Users\\aalbutov\\.m2\\repository\\webdriver\\phantomjs\\windows\\2.1.1\\phantomjs.exe";
    private static GetAmountViaJS jsHandler= new GetAmountViaJS();
    private static GetAmountViaPhantom phHandler= new GetAmountViaPhantom();


    public PriceChecker(CheckerBot bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    private CheckerBot bot;
    long chatId;
    private String link;

    @Override
    public void run() {
        GetAmount handler = getHandlerForLink(link);
        String result = handler.getAmount(link);
        log.info(link+" "+result);
        if (result.contains(GetAmount.SUCCESS))bot.sendStringMessage(chatId, result+" "+link);
        PriceChecker nextChecker = new PriceChecker(bot, chatId);
        nextChecker.setLink(link);
        CheckerBot.timer.schedule(nextChecker, 10000);
    }

    private GetAmount getHandlerForLink(String link) {
        // todo выбор обработчика
        return jsHandler;
    }

    void setLink(String str){
        this.link = str;
    }
}
