package items;

/**
 * Created by Eli on 08/12/2016.
 */
public class News {

    private String newsImage;
    private String newsTitle;
    private String newsText;
    private String newsLink;

    public News(){};

    public News(String image, String title, String text, String link)
    {
        newsImage = image;
        newsTitle = title;
        newsText = text;
        newsLink = link;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsText() {
        return newsText.replace("\n","");
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public String getLink() {
        return newsLink;
    }

    public void setLink(String link) {
        this.newsLink = link;
    }
}
