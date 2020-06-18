package kavaliou.ivan.net.moneymanagermobile.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static final String LDT_INPUT_FORMAT_WITHOUT_T = "yyyy-MM-dd HH:mm";
    public static String parseDate(Date date){
        DateFormat df = new SimpleDateFormat(LDT_INPUT_FORMAT_WITHOUT_T);
        return df.format(date);
    }
}
