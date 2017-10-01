package BotEx.tlgrm.priceChecker;

import BotEx.tlgrm.HttpExecuter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;


public class PriceChecker extends TimerTask{
//    private HttpExecuter httpExecuter= HttpExecuter.getHttpExecuter();
    private String link;
    Document doc;
    private long nextLaunchTime;

    @Override
    public void run() {
        try {
            setNextLaunchTime();
            doc = Jsoup.connect(link).get();
            Element result = doc.getElementById("stockTag");
            String code = result.toString();
            System.out.println(code);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PriceChecker nextChecker = new PriceChecker();
            nextChecker.setLink(link);
            CheckerBot.State.timer.schedule(nextChecker, 5000);
        }

    }
    void setNextLaunchTime(){
        this.nextLaunchTime = new Date().getTime()+5000;
    }

    void setLink(String str){
        this.link = str;
    }
}
