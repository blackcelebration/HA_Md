package items;

import java.util.Date;
import java.util.List;

import tools.HaDbHelper;

/**
 * Created by Eli on 25/01/2017.
 */
public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private Date date;
    private String hour;
    private String result;
    private boolean isNextMatch;

    public Match(){}

    public Match(Team _homeTeam,Team _awayTeam,Date _date,String _hour, String _result){
        homeTeam = _homeTeam;
        awayTeam = _awayTeam;
        date = _date;
        hour = _hour;
        result = _result;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setNextMatch(boolean _isNextMatch){
        isNextMatch = _isNextMatch;
    }

    public boolean getNextMatch(){
        return isNextMatch;
    }

    public static void insertResultsToDB(List<Match> results, Date lastUpdate, HaDbHelper dbHelper){
        if (results == null || results.size() ==0)
            return;

        java.util.Date matchDate;
        for (int i = results.size()-1; i >= 0; i--){
            matchDate = results.get(i).getDate();
            if (lastUpdate!=null && matchDate.before(lastUpdate))
                return;

            dbHelper.insertResult(results.get(i));
        }
    }

    public static void insertFixturesToDB(List<Match> fixtures, HaDbHelper dbHelper){
        if (fixtures == null || fixtures.size() == 0){
            return;
        }

        Date matchDate;
        dbHelper.deleteFixtures();

        for (int i = 0; i < fixtures.size(); i++){
            dbHelper.insertFixture(fixtures.get(i));
        }
    }

    public static String getMatchHour(String matchHour,int factor){
        if (factor == 0)
            return matchHour;
        else{
            int hour = Integer.parseInt(matchHour.substring(0,2))+factor;
            return (hour < 10 ? "0" : "") + String.valueOf(hour) + matchHour.substring(2);
        }
    }

    public static int getLastResultPosition(List<Match> matches){
        for (int i = 0; i<matches.size();i++){
            if (matches.get(i).getResult()==null || matches.get(i).getResult()=="")
                return i;

        }
        return -1;
    }

}
