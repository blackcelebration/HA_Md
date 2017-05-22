package com.lychee.soft.ha;

import android.app.ListActivity;
import android.view.Menu;

/**
 * Created by Eli on 30/11/2016.
 */
public class BaseListActivity extends ListActivity {

    private static final int MENU_NEWS = Menu.FIRST;
    private static final int MENU_SQUAD = Menu.FIRST + 1;
    private static final int MENU_MATCHES = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

      //  menu.add(Menu.NONE,MENU_NEWS,Menu.NONE,getResources().getString(R.string.news_menu));
     //   menu.add(Menu.NONE,MENU_NEWS,Menu.NONE,getResources().getString(R.string.squad_menu));
     //   menu.add(Menu.NONE,MENU_NEWS,Menu.NONE,getResources().getString(R.string.matches_menu));

        return true;
    }
}
