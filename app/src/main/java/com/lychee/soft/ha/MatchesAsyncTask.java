package com.lychee.soft.ha;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import items.Match;
import items.Team;
import tools.HaDbHelper;

/**
 * Created by Eli on 26/02/2017.
 */

public class MatchesAsyncTask extends AsyncTask<String,Void,Boolean> {
    private HaDbHelper dbHelper;
    private List<Team> teams;

    public MatchesAsyncTask(Context context){
       super();
       dbHelper = new HaDbHelper(context);
       teams = dbHelper.getTeams();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        syncResults(params[0]);

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    private void syncResults(String teamEngName){
        dbHelper.openDb(false);
        Date lastUpdate = dbHelper.getMatchesLastUpdate();
        Date lastResult = dbHelper.getLastResultDate();

        boolean lastResultUpdated = dbHelper.checkLastResultUpdated();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null; //new Date()
        try {
            todayDate = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Match> results = null;
        if (!lastResultUpdated)
            results = fillNewResults("http://www.livefootball.com/football/israel/winner-league/results/all/", teamEngName, lastUpdate);
        List<Match> fixtures = null;
        if (lastUpdate==null || !lastUpdate.equals(todayDate))
             fixtures = fillNewFixtures("http://www.livefootball.com/football/israel/winner-league/fixtures/all/",teamEngName);

        dbHelper.beginTransaction();
        try {
            Match.insertResultsToDB(results,lastResult,dbHelper);
            Match.insertFixturesToDB(fixtures,dbHelper);
            dbHelper.setMatchesUpdate(lastUpdate,todayDate);
            dbHelper.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelper.endTransaction();
        }

        dbHelper.closeDb();
    }

    private int getDateIndex(StringBuffer response,int indexTeam,int curDateIndex){
        if (indexTeam > curDateIndex){
            return curDateIndex;
        }
        else{
            return getDateIndex(response,indexTeam,curDateIndex+7);
        }

    }

    private List<Match> fillNewResults(String sUrl,String teamEngName,Date dLastUpdate){
        try {
            List<Match> results = new ArrayList<Match>();
            URL url = new URL(sUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            String dateString = "",homeTeam="",awayTeam="",matchResult;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            int indexTeam = response.indexOf(teamEngName);
            int curMatchIndex,awayStartIndex,homeStartIndex,matchResultIndex=0,nextDateIndex = 0;
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy",Locale.US);
            Date matchDate = null;

            while (indexTeam > 0)  {
                curMatchIndex = response.lastIndexOf("mElDate",indexTeam);
                dateString = response.substring(response.indexOf(",",curMatchIndex+9)+1,response.indexOf("<",curMatchIndex+9));

                try {
                    matchDate = fmt.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if ((dLastUpdate != null) && (matchDate.before(dLastUpdate)))
                    break;
                else{
                    if (response.substring(indexTeam-7,indexTeam-2).equals("mElO1")){
                        homeTeam = response.substring(indexTeam,response.indexOf("<",indexTeam));
                        awayStartIndex = response.indexOf("mElO2",indexTeam)+7;
                        awayTeam = response.substring(awayStartIndex,response.indexOf("<",awayStartIndex));
                        matchResultIndex = response.indexOf(" - ",indexTeam);
                    }
                    else if (response.substring(indexTeam-7,indexTeam-2).equals("mElO2")){
                        awayTeam = response.substring(indexTeam,response.indexOf("<",indexTeam));
                        homeStartIndex = response.lastIndexOf("mElO1",indexTeam)+7;
                        homeTeam = response.substring(homeStartIndex,response.indexOf("<",homeStartIndex));
                        matchResultIndex = response.lastIndexOf(" - ",indexTeam);
                    }
                    matchResult =
                            response.substring(response.lastIndexOf(">",matchResultIndex)+1,response.indexOf("<",matchResultIndex));

                    Match match = new Match();
                    match.setDate(matchDate);
                    match.setResult(matchResult);
                    for (int i=0; i<teams.size();i++)
                    {
                        if (teams.get(i).getEng_Full().equals(homeTeam)) {
                            match.setHomeTeam(teams.get(i));
                            if (match.getAwayTeam() != null)
                                break;
                        }
                        if (teams.get(i).getEng_Full().equals(awayTeam)) {
                            match.setAwayTeam(teams.get(i));
                            if (match.getHomeTeam() != null)
                                break;
                        }
                    }

                    results.add(match);

                }

                //jump to the next result
                indexTeam = response.indexOf(teamEngName,indexTeam+15);
            }
            in.close();

            return results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Match> fillNewFixtures(String sUrl,String teamEngName){
        try {
            List <Match> fixtures = new ArrayList<Match>();
            URL url = new URL(sUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            String dateString = "",homeTeam="",awayTeam="",matchHour;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            int indexTeam = response.indexOf(teamEngName);
            int curMatchIndex,awayStartIndex,homeStartIndex,gmtFactor=0,matchHourIndex=0,nextDateIndex = 0;
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy",Locale.US);
            Date dLastUpdate = null;
            Date matchDate = null;

            // find the difference between system GMT and url GMT in order to set the right match hour
            int tz = HaDbHelper.getCurrentTimezoneOffset();
            String urlGmt = response.substring(response.indexOf("(GMT")+4,response.indexOf("(GMT")+6);
            gmtFactor = tz-Integer.parseInt(urlGmt.charAt(0)=='+'?urlGmt.substring(1):urlGmt); // exclude '+' from GMT and leave '-' before parsing to int


            while (indexTeam > 0)  {
                curMatchIndex = response.lastIndexOf("mElDate",indexTeam);
                dateString = response.substring(response.indexOf(",",curMatchIndex+9)+1,response.indexOf("<",curMatchIndex+9));

                try {
                    matchDate = fmt.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                    if (response.substring(indexTeam-7,indexTeam-2).equals("mElO1")){
                        homeTeam = response.substring(indexTeam,response.indexOf("<",indexTeam));
                        awayStartIndex = response.indexOf("mElO2",indexTeam)+7;
                        awayTeam = response.substring(awayStartIndex,response.indexOf("<",awayStartIndex));
                    }
                    else if (response.substring(indexTeam-7,indexTeam-2).equals("mElO2")){
                        awayTeam = response.substring(indexTeam,response.indexOf("<",indexTeam));
                        homeStartIndex = response.lastIndexOf("mElO1",indexTeam)+7;
                        homeTeam = response.substring(homeStartIndex,response.indexOf("<",homeStartIndex));
                    }
                    matchHourIndex = response.lastIndexOf("mElStatus",indexTeam)+11;
                    matchHour = Match.getMatchHour(response.substring(matchHourIndex,matchHourIndex+5),gmtFactor);

                    Match match = new Match();
                    match.setDate(matchDate);
                    match.setHour(matchHour);
                    for (int i=0; i<teams.size();i++)
                    {
                        if (teams.get(i).getEng_Full().equals(homeTeam)) {
                            match.setHomeTeam(teams.get(i));
                            if (match.getAwayTeam() != null)
                                break;
                        }
                        if (teams.get(i).getEng_Full().equals(awayTeam)) {
                            match.setAwayTeam(teams.get(i));
                            if (match.getHomeTeam() != null)
                                break;
                        }
                    }

                    fixtures.add(match);



                //jump to the next result
                indexTeam = response.indexOf(teamEngName,indexTeam+15);
            }
            in.close();

            return fixtures;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
