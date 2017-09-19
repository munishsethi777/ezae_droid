package in.learntech.rights.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baljeetgaheer on 16/09/17.
 */

public class DateUtil {

    public static Date stringToDate(String dateStr){
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date date = sdf.parse(dateStr);
            return date;
        }catch (ParseException e){
            //// TODO: Log Exception
        }
        return null;
    }
}
