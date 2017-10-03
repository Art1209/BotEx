package BotEx.tlgrm.priceChecker.checker;

/**
 * Created by aalbutov on 03.10.2017.
 */
public interface GetAmount {
    static final String RESULT_TEMPLATE = "%s : Current amount : %s";
    static final String SUCCESS = "Success";
    static final String OUT_OF_STOCK = "Out of stock";
    String getAmount(String url);
}
