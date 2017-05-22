package com.lychee.soft.ha;

/**
 * Created by Eli on 09/03/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import items.Match;
import tools.HaDbHelper;

import static items.Match.getLastResultPosition;


/**
 * Created by Eli on 03/02/2017.
 */
public class MatchesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Match>>{

    private MatchesListAdapter mMatchesAdapter;
    private Context mContext;
    private List<Match> matchesList;
    private ListView matchesListView;
    private static int MATCHES_LOADER_KEY=7;
    // Continue from combining the pager+lists lychee and the MatchesFragment code
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.matches_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d("MatchesFragment","onCreate Called");
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public Loader<List<Match>> onCreateLoader(int id, Bundle args) {
        Loader<List<Match>> matchesLoader = null;
        try {
            matchesLoader = new MatchesTaskLoader(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchesLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Match>> loader, List<Match> data) {
        Log.d("MatchesFragment","onLoadFinished called");
        try {
            if (data != null){
                mMatchesAdapter.SetMatchesList(data,Match.getLastResultPosition(data));
                matchesListView.setAdapter(mMatchesAdapter);
                matchesListView.setSelection(getLastResultPosition(data)-3);
                Log.d("MatchesList size : ",String.valueOf(data.size()));
            //    matchesListView.setSelection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        Button btnDefence = (Button)getActivity().findViewById(R.id.defence_button);
        btnDefence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchesListView.smoothScrollToPosition(3);
            }
        });*/
    }

    @Override
    public void onLoaderReset(Loader<List<Match>> loader) {
        Log.d("MatchesFragment","onLoaderReset called");
        mMatchesAdapter.SetMatchesList(new ArrayList<Match>(),0);
    }

    @Override
    public void onStart() {
          getLoaderManager().initLoader(1,null,this);
          getLoaderManager().getLoader(1).startLoading();
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("MatchesFragment","onActivityCreated called");
        try {
            super.onActivityCreated(savedInstanceState);
            matchesListView = (ListView)getActivity().findViewById(R.id.matches_list);
            mMatchesAdapter = new MatchesListAdapter(getActivity().getApplicationContext());

          //  getLoaderManager().initLoader(1,null,this);
          //  getLoaderManager().getLoader(1).startLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // matchesListView.setAdapter(mMatchesAdapter);
        //   matchesListView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);

    }



    private void TestDB(){
        HaDbHelper dbHelper = new HaDbHelper(getActivity().getApplicationContext());
        try {
            dbHelper.createDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //      dbHelper.TestSelect();

    }

}
