package tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import items.Match;
import items.Player;
import items.PlayerTeams;
import items.PlayerViewType;
import items.Team;

import static tools.Consts.PLAYER_POSITION_CENTERBACK;
import static tools.Consts.PLAYER_POSITION_DEFENDER;

/**
 * Created by Eli on 26/01/2017.
 */
public class HaDbHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "";
    private static String DB_NAME = "HA";
    private static String TABLE_MATCHES = "Matches";
    private static String TABLE_PLAYERS = "Players";
    private static String TABLE_TEAMS = "Teams";
    private static String TABLE_PREVTEAMS = "PrevTeams";

    private final Context context;
    private SQLiteDatabase db;
    private String today;
    private SimpleDateFormat fmt;
    private DateFormat dateFormat;

    public HaDbHelper(Context context) {
        super(context, DB_NAME, null, 1);

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.context = context;
        try {
         // create(true); // needed for upgrading db

          create(false);
          fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
          dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          today = dateFormat.format(new java.util.Date());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDb(boolean readOnly){
        if (readOnly)
            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        else
            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void beginTransaction(){
        db.beginTransaction();
    }

    public void endTransaction(){
        db.endTransaction();
    }

    public void  setTransactionSuccessful(){
        db.setTransactionSuccessful();
    }

    public void closeDb(){
        db.close();
    }
    // Creates a empty database on the system and rewrites it with your own database.
    public void create(boolean bOverwriteDB) throws IOException {
        boolean dbExist = false;

        if (bOverwriteDB == false)
          dbExist = checkDataBase();

        if ((bOverwriteDB) || (!dbExist)) {
            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    // copy your assets db
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    // Check if the database exist to avoid re-copy the data
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database don't exist yet.
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Team> getTeams(){
        List<Team> teams = new ArrayList<Team>();
        Team team;
        String strTeams = "SELECT Id, Name, Imagename, eng_full, eng_short FROM Teams WHERE eng_short IS NOT NULL AND eng_short != \"\"";
        boolean closeDb = false;
        if (db==null || !db.isOpen()) {
            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            closeDb = true;
        }
        Cursor cursor = db.rawQuery(strTeams, null);
        while (cursor.moveToNext()){
            team = new Team(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            teams.add(team);
        }
        if (closeDb)
            db.close();

        return teams;
    }

    public Date getMatchesLastUpdate(){
        boolean closeDb = false;
        try {
            Date date = null;
            String strDateQuery = "SELECT Matches_Update FROM System";
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                closeDb = true;
            }

            Cursor cursor = db.rawQuery(strDateQuery, null);
            while (cursor.moveToNext()){
                date = dateFormat.parse(cursor.getString(0));
            }

            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
        return null;
    }

    public Date getLastResultDate(){
        boolean closeDb = false;
        try {
            java.util.Date date = null;
            String strDateQuery = "SELECT MAX(Date) FROM Matches WHERE Result IS NOT NULL AND Result <> ''";
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                closeDb = true;
            }

            Cursor cursor = db.rawQuery(strDateQuery, null);

            while (cursor.moveToNext()){
                if (cursor.getString(0) != null)
                 date = dateFormat.parse(cursor.getString(0));
            }

            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
        return null;
    }


    public boolean checkLastResultUpdated(){
        boolean closeDb = false;
        try {
            boolean isUpdated = false;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDateQuery = "SELECT (CASE WHEN Result IS NULL THEN 0 ELSE 1 END) AS Resulted" +
                            " FROM Matches WHERE Date IN (SELECT MAX(Date) FROM Matches WHERE Date <= '" + today + "') LIMIT 1";
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                closeDb = true;
            }
            Cursor cursor = db.rawQuery(strDateQuery, null);
            while (cursor.moveToNext()){
                isUpdated = cursor.getInt(0)==1;
            }

            return isUpdated;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
        return false;
    }

    public void setMatchesUpdate(java.util.Date dLastUpdate,java.util.Date todayDate){

        if (dLastUpdate != null && dLastUpdate.equals(todayDate))
            return;

        boolean closeDb = false;
        try {
            String strDateQuery = "";


             if (dLastUpdate == null) {
                 strDateQuery = "INSERT INTO System (Matches_Update) VALUES ('" + today + "')";
             }
            else{
                 strDateQuery = "UPDATE System SET Matches_Update = '" + today + "'";
             }

            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                closeDb = true;
            }

            db.execSQL(strDateQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
    }

    public List<Player> getPlayers() {
        boolean closeDb = false;
        try {

     //       createDb();
            Player player,playerHeader;
            PlayerTeams team;
            List<Player> playersList = new ArrayList<Player>();
            List<PlayerTeams> playerTeamsList = new ArrayList<PlayerTeams>();
            String str = "SELECT Id,Name,Birthdate,ImageName,Position,Number FROM Players ORDER BY position";
            String strTeams = "SELECT t.Name,pv.StartYear,pv.EndYear FROM prevteams pv " +
                               "JOIN teams t on pv.teamsid = t.id " +
                               "WHERE pv.playersid = ? ORDER BY pv.StartYear DESC, pv.EndYear DESC";
            String name,image;
            int id,position,number;
            int prevPosition = 0;
            String birthdate;
         //   SQLiteDatabase db = null;
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                closeDb = true;
            }
            Cursor cursor = db.rawQuery(str, null);
            Cursor cursorTeams;
            while (cursor.moveToNext()) {
                position = cursor.getInt(4);
                // defender and centerback are in the same category - defence (so there is no need to enter a header between them)
                if ((position != prevPosition) &&
                        !(prevPosition == PLAYER_POSITION_CENTERBACK && position == PLAYER_POSITION_DEFENDER)){
                    player = new Player(PlayerViewType.PLAYER_VIEW_HEADER,Player.getPlayerCategoryName(position));
                    playersList.add(player);
                    prevPosition = position;
                }
                id = cursor.getInt(0);
                name = cursor.getString(1);
                birthdate = cursor.getString(2);
                image = cursor.getString(3);
                number = cursor.getInt(5);
                player = new Player(PlayerViewType.PLAYER_VIEW_PLAYER,name,image,position,dateFormat.parse(birthdate),number,null); //Date.valueOf(birthdate)
                playerTeamsList = new ArrayList<PlayerTeams>();
                cursorTeams = db.rawQuery(strTeams, new String[]{String.valueOf(id)}); // cursor of previous teams
                while (cursorTeams.moveToNext()){
                    team = new PlayerTeams(cursorTeams.getString(0),cursorTeams.getInt(1),cursorTeams.getInt(2));
                    playerTeamsList.add(team);
                }
                player.setPreviousTeams(playerTeamsList); //set list of previous teams of this player
                playersList.add(player);
            }
            return playersList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
        return null;
    }

    public List<Match> getMatches() {
        Match match;
        List<Match> matches = new ArrayList<Match>();
        String hour,result="";
        boolean isNexMatch;
        Team homeTeam,awayTeam;

        Date date;
        int homeTeamId,awayTeamId;

        List<Team> teams = getTeams();
        try {

            SQLiteDatabase dbMatches = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            String strQuery = "SELECT HomeTeamID, AwayTeamID, Date, Hour, Result" +
                    " FROM Matches ORDER BY Date";
            Cursor cursor = dbMatches.rawQuery(strQuery, null);

            while (cursor.moveToNext()) {
                homeTeam = awayTeam = null;
                homeTeamId = cursor.getInt(0);
                awayTeamId = cursor.getInt(1);
                date = dateFormat.parse(cursor.getString(2));
                hour = cursor.getString(3);
                isNexMatch = false;
                if (cursor.getString(4)==null || cursor.getString(4)=="") {
                    if (result!="")
                        isNexMatch = true;
                    result = "";
                }
                else
                    result = new StringBuffer(cursor.getString(4)).reverse().toString();

                for (int i = 0; i < teams.size(); i++) {
                    if (teams.get(i).getId() == homeTeamId) {
                        homeTeam = teams.get(i);
                        if (awayTeam != null)
                            break;
                    }
                    if (teams.get(i).getId() == awayTeamId) {
                        awayTeam = teams.get(i);
                        if (homeTeam != null)
                            break;
                    }
                }

                match = new Match(homeTeam, awayTeam, date, hour, result);
                if (isNexMatch)
                    match.setNextMatch(isNexMatch);
                matches.add(match);
            }
            dbMatches.close();

            return matches;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void createDb(){

        final String DB_DESTINATION = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME;

        // Check if the database exists before copying
        boolean initialiseDatabase = (new File(DB_DESTINATION)).exists();
        if (initialiseDatabase == false) {

            InputStream is = null;
            try {
                is = context.getAssets().open("HA");

                // Copy the database into the destination
                OutputStream os = new FileOutputStream(DB_DESTINATION);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0){
                    os.write(buffer, 0, length);
                }
                os.flush();

                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertResult(Match result){

        boolean closeDb = false;
        try {
            StringBuilder insertStr = new StringBuilder();
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                closeDb = true;
            }

            insertStr.append("INSERT INTO Matches (HomeTeamId,AwayTeamId,Date,Result) VALUES (");
            insertStr.append(String.valueOf(result.getHomeTeam().getId()));
            insertStr.append(",");
            insertStr.append(String.valueOf(result.getAwayTeam().getId()));
            insertStr.append(",");
            insertStr.append("'" + dateFormat.format(result.getDate()) + "'"); //result.getDate().toString()
            insertStr.append(",");
            insertStr.append("'" + result.getResult() + "'");
            insertStr.append(")");

            db.execSQL(insertStr.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
    }

    public void insertFixture(Match fixture){

        boolean closeDb = false;
        try {
            StringBuilder insertStr = new StringBuilder();
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                closeDb = true;
            }

            insertStr.append("INSERT INTO Matches (HomeTeamId,AwayTeamId,Date,Hour) VALUES (");
            insertStr.append(String.valueOf(fixture.getHomeTeam().getId()));
            insertStr.append(",");
            insertStr.append(String.valueOf(fixture.getAwayTeam().getId()));
            insertStr.append(",");
            insertStr.append("'" + dateFormat.format(fixture.getDate()) + "'");
            insertStr.append(",");
            insertStr.append("'" + fixture.getHour() + "'");
            insertStr.append(")");

            db.execSQL(insertStr.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }
    }

    public void deleteFixtures(){
        boolean closeDb = false;
        try {
            String queryStr = "DELETE FROM Matches WHERE Result IS NULL OR Result = ''";
            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                closeDb = true;
            }

            db.execSQL(queryStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }

    }

    public void deleteAll(){
        boolean closeDb = false;
        try {

            if (db==null || !db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                closeDb = true;
            }
        //    String queryStr = "DELETE FROM Matches";
        //    db.execSQL(queryStr);
         //   queryStr = "DELETE FROM System";
          //  db.execSQL(queryStr);
            db.delete("Matches","",null);
            db.delete("System","",null);
//----------------------------------------------------------------------------------------------------------
    /*        //--------    SELECT TO TEST IF ANYTHING LEFT --------------------------------------------------
            queryStr = "SELECT * FROM System";
            Cursor c = db.rawQuery(queryStr,null);
            if (c.getCount() > 0){
               while (c.moveToNext()){
                   Date d= dateFormat.parse(c.getString(0));
               }
            }

            queryStr = "SELECT * FROM Matches";
             c = db.rawQuery(queryStr,null);
            if (c.getCount() > 0){
                while (c.moveToNext()){
                    int d= c.getInt(0);
                }
            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (closeDb)
                db.close();
        }

    }

    public static int getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        return Integer.valueOf(offset.substring(1,3));
    }
    /*
    public List<Player> getPlayersTest(){
        List<Player> players = new ArrayList<Player>();

        // test permanent players
        Player player = new Player("דלה איינוגבה","ayenugba", 1, Date.valueOf("1983-04-20"), 1, Arrays.asList("בני יהודה תל אביב","קינגס גאנה"));
        players.add(player);
        players.add(player);
        player = new Player("חיים איצרין","izrin", 3, Date.valueOf("1983-04-20"), 22, Arrays.asList("בני יהודה תל אביב","קינגס גאנה"));
        players.add(player);
        player = new Player("טל מכלוף","ayenugba", 2, Date.valueOf("1983-04-20"), 15, Arrays.asList("בני יהודה תל אביב","קינגס גאנה"));
        players.add(player);
        player = new Player("איסו לינגאנה","ayenugba", 4, Date.valueOf("1983-04-20"), 7, Arrays.asList("בני יהודה תל אביב","קינגס גאנה"));
        players.add(player);
        player = new Player("","ayenugba", 4, Date.valueOf("1983-04-20"), 8, Arrays.asList("בני יהודה תל אביב","קינגס גאנה"));
        players.add(player);
        return players;
    }
    */

}
