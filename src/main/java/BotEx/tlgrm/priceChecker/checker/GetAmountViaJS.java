package BotEx.tlgrm.priceChecker.checker;

import BotEx.tlgrm.HttpExecuter;
import BotEx.tlgrm.JsonRecoursiveParser;

/**
 * Created by aalbutov on 03.10.2017.
 */
public class GetAmountViaJS implements GetAmount {

    public static final String SERVICE_LINK="https://dweb.jd.com/product/getSkuStock.html?callback=jQuery172010888864788319497_1507013493433&skuId=%s&venderId=217&destCountryId=2456&_=1507013498164";
    public static final String PRE_SKU_LINK = "https://www.joybuy.com/";
    public static final String POST_SKU_LINK = ".html";

    private HttpExecuter exec = HttpExecuter.getHttpExecuter();
    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();

    @Override
    public String getAmount(String url) {
        String skuId = url.trim().replace(PRE_SKU_LINK,"").replace(POST_SKU_LINK,"");
        String amount = parser.safeJsonFindByKey("amount",exec.requestForStream(String.format(SERVICE_LINK,skuId)));
        String result = Integer.parseInt(amount)!=0?SUCCESS:OUT_OF_STOCK;
        return String.format(RESULT_TEMPLATE,result, amount);
    }
}
