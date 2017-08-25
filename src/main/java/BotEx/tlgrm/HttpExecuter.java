package BotEx.tlgrm;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HttpExecuter {
    private JSONParser parser = new JSONParser();
    private CloseableHttpClient httpclient = HttpClientBuilder.create()
            .setSSLHostnameVerifier(new NoopHostnameVerifier())
            .setConnectionTimeToLive(70L, TimeUnit.SECONDS)
            .setMaxConnTotal(100).build();
    private CloseableHttpResponse response;
    private static HttpExecuter exc;
    private HttpExecuter(){}
    public static HttpExecuter getHttpExecuter(){
        if (exc ==null)exc = new HttpExecuter();
        return exc;
    }
    public synchronized InputStream makeRequestGetJson (String request){
        try {
            response = httpclient.execute(new HttpGet(request));
            return response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    public synchronized String JsonRecoursiveFind(JSONObject jsonObject, String key){
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
    public synchronized String JsonArrayChecker(JSONArray arr, String key){
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
