package BotEx.tlgrm;


import org.json.simple.JSONObject;

public class ParsedWord {
    private static int counter;
    private int position;
    private JSONObject obj;
    private String word;

    public ParsedWord(JSONObject obj, String word) {
        counter++;
        position = counter;
        this.obj = obj;
        this.word = word;
    }

    @Override
    public String toString() {
        return getWord()+" "+getPosition();
    }

    public int getPosition() {
        return position;
    }

    public JSONObject getObj() {
        return obj;
    }

    public String getWord() {
        return word;
    }
}
