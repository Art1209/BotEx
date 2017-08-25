package BotEx.tlgrm;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
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

    public synchronized JSONObject JsonFindByValue (String value, InputStream is){
        JSONObject result = null;
        JSONObject jsonObj = null;
        try {
            jsonObj = (JSONObject) parser.parse(new InputStreamReader(is));
            if (!jsonObj.isEmpty()){
                result= JsonRecoursiveFindByValue(jsonObj , value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    private synchronized JSONObject JsonRecoursiveFindByValue(JSONObject jsonObject, String value){
        JSONObject result = null;
        Collection<Object> values = jsonObject.values();
        Set<Object> keys = jsonObject.keySet();
        if (!values.contains(value)){
            for (Object key: keys){
                Object jsonValue = jsonObject.get(key);
                if (jsonObject.getClass().isInstance(jsonValue)){
                    result = JsonRecoursiveFindByValue((JSONObject) jsonValue, value);
                    if (result!=null) break;
                }
                if (JSONArray.class.isInstance(jsonValue)){
                    result = JsonArrayCheckerByValue((JSONArray) jsonValue, value);
                    if (result!=null) break;
                }
            }
        }else result = jsonObject;
        return result;
    }
    private synchronized JSONObject JsonArrayCheckerByValue(JSONArray arr, String value){
        JSONObject result = null;
        for (Object obj:arr){
            if (JSONObject.class.isInstance(obj)){
                result = JsonRecoursiveFindByValue((JSONObject) obj, value);
                if (result!=null) break;
            }
            if (JSONArray.class.isInstance(obj)){
                result = JsonArrayCheckerByValue((JSONArray) obj, value);
                if (result!=null) break;
            }
        }
        return result;
    }
}
