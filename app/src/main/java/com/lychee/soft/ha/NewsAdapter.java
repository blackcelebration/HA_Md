package com.lychee.soft.ha;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import items.News;
import tools.FontChangeCrawler;


/**
 * Created by Eli on 08/12/2016.
 */
public class NewsAdapter extends BaseAdapter {

    private List<News> mNews = new ArrayList<News>();
    private final Context mContext;

    public NewsAdapter(Context context) {
        mContext = context;
      //  PopulateNews();

        Picasso picasso = new Picasso.Builder(mContext)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d("Picasso", exception.getMessage());
                    }
                }).build();
    }

    public void add(News item) {
        mNews.add(item);
        notifyDataSetChanged();
    }

    public void setNewsList(List<News> listNews){
        mNews = listNews;
    }

    @Override
    public int getCount() {
        return mNews==null?0:mNews.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mNews.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final News news = (News) mNews.get(position);
        ViewHolder viewholder = new ViewHolder();

        // TODO - Inflate the View for this ToDoItem
        // from todo_item.xml
        CardView itemLayout;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemLayout = (CardView) inflater.inflate(R.layout.newsfeed, parent, false);
        } else {
            itemLayout = (CardView) convertView;
        }

/*
        try {
            viewholder.newsImage = (ImageView) itemLayout.findViewById(R.id.newsimage);
            String sNewsImage = news.getNewsImage();
            URL url;
            if ((sNewsImage != null) && (sNewsImage.trim()!="")) {
                url = new URL(sNewsImage);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                viewholder.newsImage.setImageBitmap(bmp);
            }
            else {
                viewholder.newsImage.setImageResource(R.drawable.ashkelon);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ImageNews Load", e.getMessage() == null ? "" : e.getMessage());
        }
*/

        viewholder.newsImage = (ImageView) itemLayout.findViewById(R.id.newsimage);
        String sNewsImage = news.getNewsImage();
        if ((sNewsImage != null) && (sNewsImage.trim()!="")){
                    Picasso.with(mContext).load(news.getNewsImage()).error(R.drawable.crowd1).into(viewholder.newsImage);
        }
        else
            viewholder.newsImage.setImageResource(R.drawable.crowd1);

        viewholder.newsTitle = (TextView) itemLayout.findViewById(R.id.newstitle);
        viewholder.newsTitle.setText(news.getNewsTitle());
        viewholder.newsTitle.setTypeface(FontChangeCrawler.boldFont);

        viewholder.newsText = (TextView) itemLayout.findViewById(R.id.newstext);
        viewholder.newsText.setText(news.getNewsText());
        viewholder.newsText.setTypeface(FontChangeCrawler.regularFont);

        if (news.getNewsTitle().length() <= 33)
            viewholder.newsText.setMaxLines(4);
        else
            viewholder.newsText.setMaxLines(3);
        // Set clickable link to title and text
        Pattern pattern = Pattern.compile(news.getLink());
        Linkify.addLinks(viewholder.newsTitle, pattern, "http://");
        Linkify.addLinks(viewholder.newsText, pattern, "http://");


        itemLayout.setTag(viewholder);
        // Return the View you just created
        return itemLayout;



    }

    static class ViewHolder {
        TextView newsTitle;
        TextView newsText;
        ImageView newsImage;
    }
}
