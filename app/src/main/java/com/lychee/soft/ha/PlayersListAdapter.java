package com.lychee.soft.ha;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import items.Player;
import items.PlayerViewType;
import tools.FontChangeCrawler;

import static java.lang.Math.min;

/**
 * Created by Eli on 02/02/2017.
 */
public class PlayersListAdapter extends BaseAdapter {

    private final Context mContext;
    private int mPlayerPosition;
    private List<Player> mPlayers = new ArrayList<Player>();

    public PlayersListAdapter(Context context) {
        mContext = context;
    }

    public int getCount(){
        return mPlayers.size();
    }

    public void SetPlayersList(List<Player> players){
        mPlayers = players;
    }

    @Override
    public Object getItem(int position) {
        return mPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // continue to fill the getView method, filtering the players list,
    // creating the fragment and viewpager and connecting between them and to layouts

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Player player = (Player) mPlayers.get(position);
        CardView itemLayout = null;

        if (player.getViewType() == PlayerViewType.PLAYER_VIEW_HEADER){
          try {
              ViewHolder viewHolder = new ViewHolder();
              if ((convertView == null) || (convertView.getTag() != "header"))  {
                  LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  itemLayout = (CardView) inflater.inflate(R.layout.playerviewheader, parent, false);
              } else {
                  itemLayout = (CardView) convertView;
              }

              viewHolder.playerName = (TextView) itemLayout.findViewById(R.id.playerheadertext);
              viewHolder.playerName.setText(player.getName());
              viewHolder.playerName.setTypeface(FontChangeCrawler.boldFont);

              itemLayout.setTag("header");

              return itemLayout;
          }
          catch (Exception e){
              e.printStackTrace();
          }
        }
        else {
            ViewHolder viewholder = new ViewHolder();

            // TODO - Inflate the View for this ToDoItem
            // from todo_item.xml

            if ((convertView == null) || (convertView.getTag() != "player")) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemLayout = (CardView) inflater.inflate(R.layout.playerview, parent, false);
            } else {
                itemLayout = (CardView) convertView;
            }


            try {
                viewholder.playerImage = (ImageView) itemLayout.findViewById(R.id.playerimage);
                String uri = "@drawable/" + player.getImageName();  // where myresource (without the extension) is the file

                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    res = mContext.getResources().getDrawable(imageResource, null);
                } else {
                    res = ContextCompat.getDrawable(mContext, imageResource);
                }

                viewholder.playerImage.setImageDrawable(res);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ImageNews Load", e.getMessage() == null ? "" : e.getMessage());
            }

            viewholder.playerName = (TextView) itemLayout.findViewById(R.id.playername);
            viewholder.playerName.setText(player.getName());
            viewholder.playerName.setTypeface(FontChangeCrawler.boldFont);

            viewholder.playerPosition = (TextView) itemLayout.findViewById(R.id.playerposition);
            viewholder.playerPosition.setText(player.getPositionName().trim());
            viewholder.playerPosition.setTypeface(FontChangeCrawler.boldFont);

            viewholder.playerNumber = (TextView) itemLayout.findViewById(R.id.shapenumber);
            viewholder.playerNumber.setText(String.valueOf(player.getNumber()));
            viewholder.playerNumber.setTypeface(FontChangeCrawler.boldFont);

            viewholder.playerAge = (TextView) itemLayout.findViewById(R.id.playerage);
            viewholder.playerAge.setText(player.getAge());
            viewholder.playerAge.setTypeface(FontChangeCrawler.boldFont);

            viewholder.teamsButton = (Button)itemLayout.findViewById(R.id.button_tooltip_right_side);

            viewholder.teamsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupWindow popupWindow = new PopupWindow(mContext);
                        popupWindow.setFocusable(true);

                        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                        float popUpTextviewSize = mContext.getResources().getDimension(R.dimen.player_teams_layout_height);
                        popupWindow.setHeight((int)(popUpTextviewSize*min(player.getPreviousTeams().size(),4)*1.1));

                        ListView listView = new ListView(mContext);
                        listView.setAdapter(new PlayerTeamsAdapter(mContext, player.getPreviousTeams()));
                        popupWindow.setContentView(listView);
                       // popupWindow.setHeight(500);
                       // popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL ,0,0);
                        popupWindow.showAsDropDown(view, -200, -10);
                    }
                });

         /*   ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) itemLayout.findViewById(R.id.playerview_tooltip);

            ToolTip toolTip = new ToolTip()
                    .withText("A beautiful View")
                    .withColor(Color.RED)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);
            ToolTipView myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, viewholder.teamsButton);
            myToolTipView.setOnToolTipViewClickedListener(new ToolTipView.OnToolTipViewClickedListener() {
                @Override
                public void onToolTipViewClicked(ToolTipView toolTipView) {
                    // TODO Auto-generated method stub
                    toolTipView = null;
                }
            });*/


           // itemLayout.setTag(viewholder);
            itemLayout.setTag("player");

            // Return the View you just created
            return itemLayout;
        }
        return itemLayout;
    }

    static class ViewHolder {
        ImageView playerImage;
        TextView playerName;
        TextView playerNumber;
        TextView playerAge;
        TextView playerPosition;
        TextView playerTeams;
        Button teamsButton;
    }

    static class ViewHolderHeader{
        TextView name;
    }
}
