package items;

/**
 * Created by Eli on 14/02/2017.
 */
public class PlayerTeams {
    String name;
    int startYear,endYear;

    public PlayerTeams(String _name, int _startYear, int _endYear){
        name = _name;
        startYear = _startYear;
        endYear = _endYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    // getYearsRange return a range string such as "(2003) - (2007)"
    public String getYearsRange(){
        if (endYear == 0){
            if (startYear == 0)
                return "";
            else
                return "("+String.valueOf(startYear)+")";
        }
        else{
            StringBuilder sb = new StringBuilder();
            sb.append("("+String.valueOf(startYear)+")");
            sb.append(" - ");
            sb.append("("+String.valueOf(endYear)+")");
            return sb.toString();
        }
    }

}
