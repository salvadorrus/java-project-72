package hexlet.code.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FormatedData {

    public static String formatedData(Timestamp date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
    }

//    public static LocalDateTime formatedTime(Timestamp time) {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        return timestamp.toLocalDateTime();
//    }
}
