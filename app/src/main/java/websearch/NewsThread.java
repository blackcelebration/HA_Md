package websearch;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eli on 11/01/2017.
 */
public class NewsThread implements Serializable {
   // private static final long serialVersionUID = -8125568453376399843L;

    @SerializedName("url")
    private String url;

    @SerializedName("title")
    private String title;

    @SerializedName("text")
    private String text;

    @SerializedName("main_image")
    private String image;


    public NewsThread() {
        super();
    }

}
