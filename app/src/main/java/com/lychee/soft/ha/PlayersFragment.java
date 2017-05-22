package com.lychee.soft.ha;

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

import items.Player;
import tools.HaDbHelper;

/**
 * Created by Eli on 03/02/2017.
 */
public class PlayersFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Player>>{

    private PlayersListAdapter mPlayersAdapter;
    private Context mContext;
    private List<Player> playersList;
    private ListView playersListView;

// Continue from combining the pager+lists lychee and the PlayersFragment code
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.players_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d("PlayersFragment","onCreate Called");
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public Loader<List<Player>> onCreateLoader(int id, Bundle args) {
        Loader<List<Player>> playersLoader = new PlayersTaskLoader(getActivity());

        return playersLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Player>> loader, List<Player> data) {
        Log.d("PlayersFragment","onLoadFinished called");
        if (data != null){
            mPlayersAdapter.SetPlayersList(data);
            playersListView.setAdapter(mPlayersAdapter);
        }

        /*
        Button btnDefence = (Button)getActivity().findViewById(R.id.defence_button);
        btnDefence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playersListView.smoothScrollToPosition(3);
            }
        });*/
    }

    @Override
    public void onStart() {
        getLoaderManager().initLoader(0,null,this);
        getLoaderManager().getLoader(0).startLoading();
        super.onStart();
    }

    @Override
    public void onLoaderReset(Loader<List<Player>> loader) {
        Log.d("PlayersFragment","onLoaderReset called");
        mPlayersAdapter.SetPlayersList(new ArrayList<Player>());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("PlayersFragment","onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
        playersListView = (ListView)getActivity().findViewById(R.id.players_list);
        mPlayersAdapter = new PlayersListAdapter(getActivity().getApplicationContext());

     //   getLoaderManager().initLoader(0,null,this);
     //   getLoaderManager().getLoader(0).startLoading();
        // playersListView.setAdapter(mPlayersAdapter);
     //   playersListView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);

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
