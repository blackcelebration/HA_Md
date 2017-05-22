package tools;

import  com.google.gson.stream.JsonReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Eli on 09/01/2017.
 */
public class WebRequests {

    public static JsonReader getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        try {
            HttpsURLConnection urlConnection = null;

            URL url = new URL(urlString);

            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(150000 /* milliseconds */);

            TLSSocketFactory socketFactory = new TLSSocketFactory();
            urlConnection.setSSLSocketFactory(socketFactory);
            urlConnection.setDoOutput(true);

            urlConnection.connect();
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(),"UTF-8");

            return new JsonReader(inputStreamReader);

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }


       return null;
    }

    public static JsonElement getJSONElementFromURL(String urlString) throws IOException, JSONException {
        try {
            HttpsURLConnection urlConnection = null;

            URL url = new URL(urlString);

            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(100000 /* milliseconds */);
            urlConnection.setConnectTimeout(150000 /* milliseconds */);

            TLSSocketFactory socketFactory = new TLSSocketFactory();
            urlConnection.setSSLSocketFactory(socketFactory);
            urlConnection.setDoOutput(true);

            urlConnection.connect();
        //    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(),"UTF-8");

            // TEMP TRIAL !!!
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            // END TEMP TRIAL !!!
            return root;



        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }


}
