package in.learntech.rights.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by baljeetgaheer on 16/09/17.
 */

public class DateUtil {
    public static String format = "MMM d, y hh:mm a";
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

    public static String dateToFromat(Date date){
        Format formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String s = formatter.format(date);
        return s;
    }

    public static String dateToFormat(Date date,String format){
        Format formatter = new SimpleDateFormat(format);
        String s = formatter.format(date);
        return s;
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate)
    {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate))
        {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }
}
