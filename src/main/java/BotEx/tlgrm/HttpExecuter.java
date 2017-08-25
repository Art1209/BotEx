package BotEx.tlgrm;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HttpExecuter {

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

    public synchronized InputStream requestForStream (String request){
        try {
            response = httpclient.execute(new HttpGet(request));
            return response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getStreamForFileUrl(String url){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            return connection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

}
