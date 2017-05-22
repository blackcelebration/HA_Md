package tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eli on 09/03/2017.
 */

public class Helpers {

    public static String getDayChar(Date date){

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int iDayOfWeek = c.get(Calendar.DAY_OF_WEEK);

      //  DateFormat dateFormat = new SimpleDateFormat("E");
      //  String dayOfWeek = dateFormat.format(date);
        String result = "";

     //   return dayOfWeek;


        switch (iDayOfWeek){
            case 1:
                result = "א'";
                 break;
            case 2:
                result = "ב'";
                break;
            case 3:
                result = "ג'";
                break;
            case 4:
                result = "ד'";
                break;
            case 5:
                result = "ה'";
                break;
            case 6:
                result = "ו'";
                break;
            case 7:
                result = "ש'";
                break;
            default:
                break;
        }

        return result;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
