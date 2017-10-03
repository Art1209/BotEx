package BotEx.tlgrm.priceChecker;

import BotEx.tlgrm.priceChecker.checker.GetAmount;
import BotEx.tlgrm.priceChecker.checker.GetAmountViaJS;
import BotEx.tlgrm.priceChecker.checker.GetAmountViaPhantom;
import lombok.extern.log4j.Log4j;
import java.io.Serializable;
import java.util.TimerTask;

@Log4j
public class PriceChecker extends TimerTask implements Serializable{
    private static final long serialVersionUID = -6988425262228548200L;

    private static GetAmountViaJS jsHandler= new GetAmountViaJS();
    private static GetAmountViaPhantom phHandler= new GetAmountViaPhantom();

    private transient CheckerBot bot;

    private long chatId;
    private String link;
    private boolean silent = false;

    public PriceChecker(CheckerBot bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void run() {
        GetAmount handler = getHandlerForLink(link);
        String result = handler.getAmount(link);
        log.info(link+" "+result);
        if (result.contains(GetAmount.SUCCESS)) {
            if (!isSilent()) {
                bot.sendStringMessage(chatId, result + " " + link);
                setSilent(true);
            }
        } else setSilent(false);
        CheckerBot.timer.schedule(this);
    }

    private GetAmount getHandlerForLink(String link) {
        // todo выбор обработчика
        return jsHandler;
    }
    void setLink(String str){
        this.link = str;
    }
    public String getLink() {
        return link;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isSilent() {
        return silent;
    }
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
    public long getChatId() {
        return chatId;
    }
    public void setBot(CheckerBot bot) {
        this.bot = bot;
    }
}
