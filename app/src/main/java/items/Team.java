package items;

import android.support.annotation.Nullable;

/**
 * Created by Eli on 02/02/2017.
 */
public class Team {
    public String getImageName() {
        return imageName;
    }

    public String getEng_Full() {
        return eng_Full;
    }

    public String getEng_Short() {
        return eng_Short;
    }

    private int id;
    private String name;
    private String imageName;
    private String eng_Full;
    private String eng_Short;

    public Team(int _id,@Nullable String _name, @Nullable String _imageName,@Nullable String _eng_Full,@Nullable String _eng_Short){
        id = _id;
        name = _name;
        imageName = _imageName;
        eng_Full = _eng_Full;
        eng_Short = _eng_Short;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
