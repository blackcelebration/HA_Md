package com.lychee.soft.ha;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import items.Match;
import tools.HaDbHelper;

/**
 * Created by Eli on 09/03/2017.
 */

public class MatchesTaskLoader extends AsyncTaskLoader<List<Match>>
{

        public MatchesTaskLoader(Context context){
        super(context);
        onForceLoad();
    }

        @Override
        public List<Match> loadInBackground() {

        Resources res = getContext().getResources();
        HaDbHelper dbHelper = new HaDbHelper(getContext());

        List<Match> matches = dbHelper.getMatches();
        //  deliverResult(newses);
//            Log.d("Matches Async list size",String.valueOf(matches.size()));
        return matches;

    }

}
