package com.lychee.soft.ha;

/**
 * Created by Eli on 09/03/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import items.Match;
import tools.FontChangeCrawler;
import tools.Helpers;

import static android.support.v4.content.ContextCompat.getColor;


public class MatchesListAdapter extends BaseAdapter {

    private final Context mContext;
    private Date nextMatchDate;
    private List<Match> mMatches = new ArrayList<Match>();
    DateFormat dateFormat;

    public MatchesListAdapter(Context context) {
        mContext = context;
        dateFormat = new SimpleDateFormat("dd/MM");
    }

    public int getCount(){
        return mMatches.size();
    }

    public void SetMatchesList(List<Match> matches,int nextMatch){
        mMatches = matches;
        if (mMatches !=null && mMatches.size()>0)
           nextMatchDate = mMatches.get(nextMatch).getDate();
    }

    @Override
    public Object getItem(int position) {
        return mMatches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // continue to fill the getView method, filtering the players list,
    // creating the fragment and viewpager and connecting between them and to layouts

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Match match;
        LinearLayout itemLayout = null;
        ViewHolder viewholder = null;
        try {
            match = (Match) mMatches.get(position);
            itemLayout = null;


            viewholder = new ViewHolder();

            // TODO - Inflate the View for this ToDoItem
            // from todo_item.xml

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemLayout = (LinearLayout) inflater.inflate(R.layout.matchview, parent, false);
            } else {
                itemLayout = (LinearLayout) convertView;
            }

                viewholder.homeTeamImg = (ImageView) itemLayout.findViewById(R.id.match_hometeam_img);
                String uri = "@drawable/" + match.getHomeTeam().getImageName();  // where myresource (without the extension) is the file

                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable res;
                res = mContext.getResources().getDrawable(imageResource);
                /*
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    res = mContext.getResources().getDrawable(imageResource,null);
                } else {
                    res = ContextCompat.getDrawable(mContext, imageResource);
                }
                */
                viewholder.homeTeamImg.setImageDrawable(res);

                viewholder.awayTeamImg = (ImageView) itemLayout.findViewById(R.id.match_awayteam_img);
                uri = "@drawable/" + match.getAwayTeam().getImageName();  // where myresource (without the extension) is the file

                imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());


                res = mContext.getResources().getDrawable(imageResource);
               /*
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    res = mContext.getResources().getDrawable(imageResource, null);
                } else {
                    res = ContextCompat.getDrawable(mContext, imageResource);
                }
*/
            viewholder.awayTeamImg.setImageDrawable(res);

            viewholder.homeTeamName = (TextView) itemLayout.findViewById(R.id.match_hometeam_text);
            viewholder.homeTeamName.setText(match.getHomeTeam().getName());
            viewholder.homeTeamName.setTypeface(FontChangeCrawler.boldFont);

            viewholder.awayTeamName = (TextView) itemLayout.findViewById(R.id.match_awayteam_text);
            viewholder.awayTeamName.setText(match.getAwayTeam().getName());
            viewholder.awayTeamName.setTypeface(FontChangeCrawler.boldFont);

            Date mDate = match.getDate();
            viewholder.date = (TextView) itemLayout.findViewById(R.id.match_date);
            viewholder.date.setText(dateFormat.format(mDate));
            viewholder.date.setTypeface(FontChangeCrawler.boldFont);

            viewholder.day = (TextView) itemLayout.findViewById(R.id.match_day);
            viewholder.day.setText(Helpers.getDayChar(mDate));
            viewholder.day.setTypeface(FontChangeCrawler.boldFont);

            String result = match.getResult();
            if (result==null || result.trim()=="") {
                viewholder.result = (TextView) itemLayout.findViewById(R.id.match_result);
                viewholder.result.setText(match.getResult());
             //   viewholder.result.setVisibility(View.GONE);

                viewholder.hour = (TextView) itemLayout.findViewById(R.id.match_hour);
                viewholder.hour.setText(match.getHour());
                viewholder.hour.setTypeface(FontChangeCrawler.boldFont);

            }
            else
            {
                viewholder.result = (TextView) itemLayout.findViewById(R.id.match_result);
                viewholder.result.setText(match.getResult());
                viewholder.result.setTypeface(FontChangeCrawler.boldFont);

                viewholder.hour = (TextView) itemLayout.findViewById(R.id.match_hour);
                viewholder.hour.setText(match.getHour());
               // viewholder.hour.setVisibility(View.GONE);
            }

            if (match.getNextMatch())
                itemLayout.setBackgroundColor(getColor(mContext,R.color.light_blue));
            else {
                if (position%2==0)
                  itemLayout.setBackgroundColor(getColor(mContext, R.color.very_light_gray));
                else
                  itemLayout.setBackgroundColor(getColor(mContext, R.color.offwhite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("MatchesGetView",viewholder.toString());
        return itemLayout;
    }

    static class ViewHolder {
        ImageView homeTeamImg;
        ImageView awayTeamImg;
        TextView homeTeamName;
        TextView awayTeamName;
        TextView result;
        TextView date;
        TextView hour;
        TextView day;
    }
}
