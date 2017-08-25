package BotEx.tlgrm;


import org.apache.http.client.methods.HttpGet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class JsonRecoursiveParser {
    private JsonRecoursiveParser(){}
    private static JsonRecoursiveParser recParser;
    private JSONParser parser = new JSONParser();

    public static JsonRecoursiveParser getParser(){
        return recParser==null?new JsonRecoursiveParser():recParser;
    }

    public synchronized String JsonFindByKey (String key, InputStream is){
        String result = null;
        JSONObject jsonObj = null;
        try {
            jsonObj = (JSONObject) parser.parse(new InputStreamReader(is));
            if (!jsonObj.isEmpty()){
                result= JsonRecoursiveFind(jsonObj , key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    private synchronized String JsonRecoursiveFind(JSONObject jsonObject, String key){
        String result = null;
        Set<Object> keys = jsonObject.keySet();
        if (!keys.contains(key)){
            for (Object jsonKey: keys){
                Object value = jsonObject.get(jsonKey);
                if (jsonObject.getClass().isInstance(value)){
                    result = JsonRecoursiveFind((JSONObject) value, key);
                    if (result!=null) break;
                }
                if (JSONArray.class.isInstance(value)){
                    result = JsonArrayChecker((JSONArray) value, key);
                    if (result!=null) break;
                }
            }
        }else result =  jsonObject.get(key).toString();
        return result;
    }
    private synchronized String JsonArrayChecker(JSONArray arr, String key){
        String result = null;
        for (Object obj:arr){
            if (JSONObject.class.isInstance(obj)){
                result = JsonRecoursiveFind((JSONObject) obj, key);
                if (result!=null) break;
            }
            if (JSONArray.class.isInstance(obj)){
                result = JsonArrayChecker((JSONArray) obj, key);
                if (result!=null) break;
            }
        }
        return result;
    }
}
