package BotEx.tlgrm.priceChecker.checker;

import org.json.simple.parser.ParseException;

public interface GetAmount {
    String RESULT_TEMPLATE = "%s : Current amount : %s";
    String SUCCESS = "Success";
    String OUT_OF_STOCK = "Out of stock";
    String getAmount(String url) throws ParseException;
}
