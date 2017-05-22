package com.lychee.soft.ha;


import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

import items.News;
import websearch.SearchNews;

/**
 * Created by Eli on 02/01/2017.
 */
public class NewsTaskLoader extends AsyncTaskLoader<List<News>> {

    public NewsTaskLoader(Context context){
        super(context);
        onForceLoad();
    }

    @Override
    public List<News> loadInBackground() {
       // if (Helpers.isOnline(getContext())) {
            Resources res = getContext().getResources();
            SearchNews searchNews = new SearchNews(
                    res.getString(R.string.apikey),
                    res.getString(R.string.search1),
                    res.getString(R.string.search2),
                    res.getString(R.string.search3));
            List<News> news = searchNews.GetSearchResults();
            //  deliverResult(newses);
            if (news != null)
                Log.d("NewsLoadInBackground", String.valueOf(news.size()));

            return news;
       // }
      //  return null;

    }

}
