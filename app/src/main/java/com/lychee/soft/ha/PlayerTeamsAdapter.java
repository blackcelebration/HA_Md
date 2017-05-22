package com.lychee.soft.ha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import items.PlayerTeams;

/**
 * Created by Eli on 23/02/2017.
 */

public class PlayerTeamsAdapter extends ArrayAdapter<PlayerTeams> {
    public PlayerTeamsAdapter(Context context, List<PlayerTeams> playerTeams) {
        super(context, 0, playerTeams);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PlayerTeams playerTeams = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_teams, parent, false);
        }
        // Lookup view for data population
        TextView prevTeam = (TextView) convertView.findViewById(R.id.player_prev_team);
        TextView years = (TextView) convertView.findViewById(R.id.player_prev_team_years);
        // Populate the data into the template view using the data object
        prevTeam.setText(playerTeams.getName());
        years.setText(playerTeams.getYearsRange());
        // Return the completed view to render on screen
        return convertView;
    }
}
