package BotEx.tlgrm.priceChecker.checker;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by aalbutov on 03.10.2017.
 */
public class GetAmountViaPhantom implements GetAmount {

    static{PhantomJsDriverManager.getInstance().setup();}

    private PhantomJSDriver driver;

    @Override
    public synchronized String getAmount(String url) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
//        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PJS_EXE_PATH);
//        driver = new HtmlUnitDriver(true);
        driver = new PhantomJSDriver(caps);
        driver.get(url);
        String stockTag = driver.findElement(By.id("stockTag")).getText();
        // todo переделать условие
        String result = stockTag.contains("out")?SUCCESS:OUT_OF_STOCK;
        return String.format(RESULT_TEMPLATE,result, "undefined");
    }
}
