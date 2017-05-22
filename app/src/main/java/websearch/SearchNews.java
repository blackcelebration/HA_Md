package websearch;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import items.News;
import tools.WebRequests;

/**
 * Created by Eli on 29/12/2016.
 */
public class SearchNews {

    private String searchString1,searchString2,searchString3;
    private String apiKey;
    private static final Type REVIEW_TYPE = new TypeToken<List<NewsThread>>() {
    }.getType();

    public SearchNews(){
    }

    public SearchNews(String _apiKey,String _searchString1,String _searchString2,String _searchString3){
        searchString1 = _searchString1;
        searchString2 = _searchString2;
        searchString3 = _searchString3;
        apiKey = _apiKey;
    }

    public void SetApiKey(String apiKey){
        this.apiKey = apiKey;
    }

    public List<News> GetSearchResults()
    {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://webhose.io/search?format=json&q=%D7%94%D7%A4%D7%95%D7%A2%D7%9C%20%D7%90%D7%A9%D7%A7%D7%9C%D7%95%D7%9F&token=");
            stringBuilder.append(apiKey);
            //stringBuilder.append("&ts=1485589406205");// search 5 days ago (instead of default 3)
            String url = stringBuilder.toString();
            Log.d("GetSearchResults url",url);
            JsonElement newsJson = WebRequests.getJSONElementFromURL(url);
            if (newsJson == null)
                return new ArrayList<News>();

            JsonObject jObject = newsJson.getAsJsonObject();

            JsonArray arr = jObject.get("posts").getAsJsonArray();

            List<News> newsThreadList = new ArrayList<News>();
            News news = new News();
            JsonElement jImage;
            String sTitle,sText,sImage;
            int nLineIndex;

            for (int i=arr.size()-1; i>=0;i--){
                news = new News();
                sTitle = arr.get(i).getAsJsonObject().get("title").getAsString();
                sText = arr.get(i).getAsJsonObject().get("text").getAsString();

                if (sText.contains("{"))
                    continue;

                if (((sTitle.contains(searchString1)) && !searchString1.isEmpty())
                        || ((sTitle.contains(searchString2)) && !searchString2.isEmpty())
                        || ((sTitle.contains(searchString3)) && !searchString3.isEmpty()))
                {
                    news.setNewsTitle(sTitle);
                    news.setLink(arr.get(i).getAsJsonObject().get("url").getAsString());


                    nLineIndex = sText.indexOf("\n");
                    if (nLineIndex > 0)
                        news.setNewsText(sText.substring(nLineIndex+1));
                    else
                        news.setNewsText(sText);

                    jImage = arr.get(i).getAsJsonObject().get("thread").getAsJsonObject().get("main_image");
                    if (!jImage.isJsonNull()){
                        news.setNewsImage(jImage.getAsString());}
                    newsThreadList.add(news);
                }
            }


            return newsThreadList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<News>();
    }

    private void readApp(JsonReader jsonReader) throws IOException {
        JsonToken token=null;// = jsonReader.peek();
        News news = new News();
        String key = "";
        while (jsonReader.hasNext()) {
            token = jsonReader.peek();

            switch (token) {
                case BEGIN_OBJECT:
                    jsonReader.beginObject();
                  //  key = jsonReader.nextName();
                    readApp(jsonReader);
                    break;
                case BEGIN_ARRAY:
                    jsonReader.beginArray();
                    readApp(jsonReader);
                    break;
                case NAME:
                     key = jsonReader.nextName();
                    break;
                case STRING:
                    String n = jsonReader.nextString();
                    if (n.equals("posts")){
                        readApp(jsonReader);
                    }
                    if (n.equals("thread")){
                        readApp(jsonReader);
                    }
                    if (key.equals("uuid")) {
                        news = new News();
                    }
                    if (key.equals("url")) {
                        news.setLink(jsonReader.nextString());
                    }
                    if (key.equals("title")) {
                        news.setNewsTitle(jsonReader.nextString());
                    }
                    if (key.equals("text")) {
                        news.setNewsText(jsonReader.nextString());
                    }
                    if (key.equals("mainimage")) {
                        news.setNewsImage(jsonReader.nextString());
                    }
                    if (key.equals("crawled")) {
                        // ListNewsItems.Add(news)
                    }
                    break;
                case END_OBJECT:
                    jsonReader.endObject();
                    break;
                case END_ARRAY:
                    jsonReader.endArray();
                    break;
                case END_DOCUMENT:
                    break;
                default:
                    break;
            }
        }

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            System.out.println(name);
            if (name.contains("thread")) {
                jsonReader.beginObject();
               news = new News();
                while (jsonReader.hasNext()) {
                    String n = jsonReader.nextName();
                    if (n.equals("url")) {
                        news.setLink(jsonReader.nextString());
                    }
                    if (n.equals("title")) {
                        news.setNewsTitle(jsonReader.nextString());
                    }
                    if (n.equals("text")) {
                        news.setNewsText(jsonReader.nextString());
                    }
                    if (n.equals("mainimage")) {
                        news.setNewsImage(jsonReader.nextString());
                    }
                    if (n.equals("crawled")){
                        // ListNewsItems.Add(news)
                    }
                }
                jsonReader.endObject();
            }
        }
    }

    public static SearchNews getInstance(){
        SearchNews searchNews = new SearchNews();
        return searchNews;
    }
}
