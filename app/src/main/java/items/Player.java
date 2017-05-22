package items;

import android.support.annotation.Nullable;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import tools.Consts;

import static tools.Consts.PLAYER_POSITION_ATTACK_NAME;
import static tools.Consts.PLAYER_POSITION_DEFFENCE_NAME;
import static tools.Consts.PLAYER_POSITION_GOALKEEPER_NAME;
import static tools.Consts.PLAYER_POSITION_MIDFIELD_NAME;

/**
 * Created by Eli on 26/01/2017.
 */
public class Player {
    private String name;
    private String imageName;
    private int position;
    private Date birthDate;
    private int number;

    private PlayerViewType viewType;

    private List<PlayerTeams> previousTeams;

    public Player(PlayerViewType _viewType,@Nullable String _name, @Nullable String __imageName, @Nullable int _position, @Nullable Date _birthDate,
                  @Nullable int _number, @Nullable List<PlayerTeams> _previousTeams){
        name = _name;
        imageName = __imageName;
        position = _position;
        birthDate = _birthDate;
        number = _number;
        previousTeams = _previousTeams;
        viewType = _viewType;
    }

    public Player(PlayerViewType _viewType, String _name){
       viewType = _viewType;
       name = _name;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }

    public int getPosition() {
        return position;
    }

    public String getPositionName() {
        String positionName;
        switch (position) {
            case Consts.PLAYER_POSITION_GOALKEEPER:
                positionName = "שוער";
                break;
            case Consts.PLAYER_POSITION_CENTERBACK:
                positionName = "בלם";
                break;
            case Consts.PLAYER_POSITION_DEFENDER:
                positionName = "מגן";
                break;
            case Consts.PLAYER_POSITION_MIDFIELDER:
                positionName = "קשר";
                break;
            case Consts.PLAYER_POSITION_STRIKER:
                positionName = "חלוץ";
                break;
            default:
                positionName = "לא הוגדר";
        }

        return positionName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getAge(){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(birthDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);

        if ((startCalendar.get(Calendar.MONTH) > endCalendar.get(Calendar.MONTH)) ||
        (startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
                && startCalendar.get(Calendar.DATE) > endCalendar.get(Calendar.DATE)))
            diffYear--;
    HashMap hm;

        return String.valueOf(diffYear);
        //Period.between(birthDate, currentDate).getYears(
       // LocalDate now = new LocalDate();
       // Years age = Years.yearsBetween(birthdate, now);
    }

    public int getNumber() {
        return number;
    }

    public List<PlayerTeams> getPreviousTeams() {
        return previousTeams;
    }

    public int getPreviousTeamsNumber(){
        return previousTeams.size();
    }

    public String getPrevTeamsShortString() {
        if (this.previousTeams.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append("קבוצות קודמות: ");
        for (int i = this.previousTeams.size()-1; i>=0; i--){
            sb.append(previousTeams.get(i).getName());
            if (i > 0)
                sb.append(", ");
        }
            return sb.toString();
    }

    public String getPrevTeamsFullString() {
        if (this.previousTeams.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append("קבוצות קודמות: ");
        for (int i = this.previousTeams.size()-1; i>=0; i--){
            sb.append("\n");
            String team = previousTeams.get(i).getName();
            sb.append(team);
            int spaces = team.trim().length()<40?40-team.trim().length():1;
            sb.append(Strings.repeat(" ",spaces));
            sb.append(" (");
            sb.append(String.valueOf(previousTeams.get(i).getEndYear()).trim());
            sb.append(")");
            int startYear = previousTeams.get(i).getStartYear();
            if (startYear !=0){
                sb.append(" - (");
                sb.append(String.valueOf(startYear).trim());
                sb.append(")");
            }
        }
        return sb.toString();
    }

    public void setPreviousTeams(List<PlayerTeams> previousTeams) {
        this.previousTeams = previousTeams;
    }

    public PlayerViewType getViewType() {
        return viewType;
    }

    public static String getPlayerCategoryName(int position){
       String catName;
        switch (position) {
            case 1:
                catName = PLAYER_POSITION_GOALKEEPER_NAME;
                break;
            case 2:
            case 3:
                catName = PLAYER_POSITION_DEFFENCE_NAME;
                break;
            case 4:
                catName = PLAYER_POSITION_MIDFIELD_NAME;
                break;
            case 5:
                catName = PLAYER_POSITION_ATTACK_NAME;
                break;
            default:
                    catName = "";
        }
        return catName;
    }
}
