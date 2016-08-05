package utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Vaibhav on 3/16/16.
 */
public class HttpUtils {

    public static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static byte[] getPostDataBytes(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        return postDataBytes;
    }

    public static String createPositionString(double latitude, double longitude) {
        return "{\"latitude\":" + latitude + ",\"longitude\":" + longitude + "}";
    }

    public static String HttpGetWithEntity(String urlAddress) {
        Log.v("api call", urlAddress);
        //HttpParams params = new BasicHttpParams();

        //HttpClient client = new DefaultHttpClient(params);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(urlAddress);

        try {

            HttpResponse responsePOST = client.execute(get);
            HttpEntity resEntity = responsePOST.getEntity();

            if (resEntity != null)
            {
                String responseString = EntityUtils.toString(resEntity);
                Log.v("api call", "return - " + responseString);

                return responseString;
            }

            return null;

        }catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
