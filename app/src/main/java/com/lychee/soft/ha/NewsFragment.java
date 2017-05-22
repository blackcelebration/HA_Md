package com.lychee.soft.ha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import items.News;

/**
 * Created by Eli on 11/12/2016.
 */
// implements LoaderManager.LoaderCallbacks<List<News>>
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>{

    private NewsAdapter mNewsAdapter;
    private RequestQueue requestQueue;
    private ListView newsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.news_list_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d("NewsFragment","onCreate Called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Loader<List<News>> newsLoader = new NewsTaskLoader(getActivity());

        return newsLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        if (data==null)
            Log.d("NewsFragment list size ","Null");
        else
            Log.d("NewsFragment list size ",String.valueOf(data.size()));

        mNewsAdapter.setNewsList(data);
      //  if (newsListView!=null)
        newsListView.setAdapter(mNewsAdapter);
        ProgressBar pb = ((ProgressBar)getActivity().findViewById(R.id.progressBar_news));
        if (pb!=null)
            pb.setVisibility(View.GONE);

        if (data == null || data.size()==0)
            ((TextView)getActivity().findViewById(R.id.newsnotfound)).setVisibility(View.VISIBLE);
        else
            ((TextView)getActivity().findViewById(R.id.newsnotfound)).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.d("NewsFragment","onLoaderReset called");
        mNewsAdapter.setNewsList(new ArrayList<News>());
    }

    @Override
    public void onStart() {
        getLoaderManager().initLoader(2,null,this);
        getLoaderManager().getLoader(2).startLoading();
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("NewsFragment","onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
        newsListView = (ListView)getActivity().findViewById(R.id.news_list);
        mNewsAdapter = new NewsAdapter(getActivity().getApplicationContext());
        newsListView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String url = ((News)mNewsAdapter.getItem(position)).getLink();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    //See if the window we are requesting has already been created
                    NewsWebFragment newWindow = (NewsWebFragment)fm.findFragmentByTag(url);

                    if (newWindow == null) {
                        newWindow = NewsWebFragment.getInstance(url);
                    }

                    ft.replace(R.id.root_news_frame,newWindow,url);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
