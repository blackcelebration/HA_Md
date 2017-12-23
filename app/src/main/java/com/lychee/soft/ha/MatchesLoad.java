package com.lychee.soft.ha;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import items.Match;
import items.Team;
import tools.HaDbHelper;

/**
 * Created by Eli on 08/03/2017.
 */

public class MatchesLoad {
    private HaDbHelper dbHelper;
    private List<Team> teams;
    private List<Match> results;
    private List<Match> resultsRelegation;
    private Context mContext;

    public MatchesLoad(Context context){
        super();
        mContext = context;
        dbHelper = new HaDbHelper(context);
        teams = dbHelper.getTeams();
    }

    public void syncResults(String teamEngName){
        dbHelper.openDb(false);
        boolean bOld = dbHelper.deleteOldResults();
        Date lastUpdate = dbHelper.getMatchesLastUpdate();
        Date lastResult = dbHelper.getLastResultDate();
        Log.d("SyncResults","SyncResults2");
        boolean lastResultUpdated = dbHelper.checkLastResultUpdated();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null; //new Date()
        try {
            todayDate = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        results = null;
        resultsRelegation = null;
        if (!lastResultUpdated) {
            String resultsSource1 = mContext.getResources().getString(R.string.results_source1); //regular season
            String resultsSource2 = mContext.getResources().getString(R.string.results_source2); // lower playoff
            results = fillNewResults(resultsSource1, teamEngName, lastUpdate);
            if (resultsSource2!=null && resultsSource2!="") {
                resultsRelegation = fillNewResults(resultsSource2, teamEngName, lastUpdate);
                gatherResults();
            }
        }
        List<Match> fixtures = null;
        if (lastUpdate==null || !lastUpdate.equals(todayDate)){
        String fixturesSource = mContext.getResources().getString(R.string.fixtures_source);
        fixtures = fillNewFixtures(fixturesSource,teamEngName);
        }

   //     if (fixtures == null || fixtures.size() == 0)
    //        fixtures = AddMissingFixtures();

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
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
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

    public Team getTeamByName(String name){
        for (int i=0;i<teams.size();i++){
            if (teams.get(i).getName().equals(name.trim()))
                return teams.get(i);
        }
        return null;
    }

    public List<Match> AddMissingFixtures(){
       List<Match> fixtures = new ArrayList<Match>();

        Date dLastMatchAdded;
        if (results != null && results.size()>0)
            dLastMatchAdded = results.get(0).getDate(); // get(0) is always the latest result
        else
            dLastMatchAdded = dbHelper.getLastResultDate();

        Date endSeason=null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Match> missingFixtures = null;
        try {
            missingFixtures = Arrays.asList(
                    new Match(getTeamByName("הפועל חיפה"),getTeamByName("הפועל אשקלון"),dateFormat.parse("2017-03-18"),"18:00",""),
                    new Match(getTeamByName("הפועל אשקלון"),getTeamByName("מ.ס. אשדוד"),dateFormat.parse("2017-04-01"),"19:00",""),
                    new Match(getTeamByName("הפועל רעננה"),getTeamByName("הפועל אשקלון"),dateFormat.parse("2017-04-08"),"19:00",""),
                    new Match(getTeamByName("הפועל אשקלון"),getTeamByName("בני יהודה ת\"א"),dateFormat.parse("2017-04-22"),"19:00",""),
                    new Match(getTeamByName("הפועל כפר סבא"),getTeamByName("הפועל אשקלון"),dateFormat.parse("2017-04-29"),"19:00",""),
                    new Match(getTeamByName("הפועל תל אביב"),getTeamByName("הפועל אשקלון"),dateFormat.parse("2017-05-06"),"19:00",""),
                    new Match(getTeamByName("הפועל אשקלון"),getTeamByName("עירוני קרית שמונה"),dateFormat.parse("2017-05-13"),"19:00","")
            );

            endSeason = dateFormat.parse("2017-05-16");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date fixDate;
        for(int i = 0; i< missingFixtures.size(); i++) {
            fixDate = missingFixtures.get(i).getDate();
            if (fixDate.after(dLastMatchAdded) && fixDate.before(endSeason))
                fixtures.add(missingFixtures.get(i));
        }

        return fixtures;
    }

    private void gatherResults(){
        if (resultsRelegation==null || resultsRelegation.size()==0)
            return;

        if (results == null || results.size()==0)
            results = new ArrayList<Match>();

        for (int i=0;i<resultsRelegation.size();i++)
            results.add(resultsRelegation.get(i));
    }

    private void deleteOldResults(){

    }
}
