package sample.test1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author huydn on 12/4/24 18:46
 */
public class Test1 {
    public static void main(String[] args) {
        Date expdate= new Date();
        expdate.setTime (expdate.getTime() + (3600 * 1000));
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        String cookieExpire = "expires=" + df.format(expdate);
        System.out.println(cookieExpire);
    }
}
