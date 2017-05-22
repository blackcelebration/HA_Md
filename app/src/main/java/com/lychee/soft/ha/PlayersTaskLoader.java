package com.lychee.soft.ha;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import items.Player;
import tools.HaDbHelper;

/**
 * Created by Eli on 02/02/2017.
 */
public class PlayersTaskLoader extends AsyncTaskLoader<List<Player>> {

    public PlayersTaskLoader(Context context){
        super(context);
        onForceLoad();
    }

    @Override
    public List<Player> loadInBackground() {

        Resources res = getContext().getResources();
        HaDbHelper dbHelper = new HaDbHelper(getContext());

        List<Player> players = dbHelper.getPlayers();
        //  deliverResult(newses);
        return players;

    }

}
