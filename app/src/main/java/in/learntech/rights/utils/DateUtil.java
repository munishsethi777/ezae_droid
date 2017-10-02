package in.learntech.rights.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baljeetgaheer on 16/09/17.
 */

public class DateUtil {

    public static Date stringToDate(String dateStr){
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dateStr);
            return date;
        }catch (ParseException e){
            //// TODO: Log Exception
        }
        return null;
    }

    public static String dateToString(Date date){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(date);
        return s;
    }
}
