package nb.app.waterdelivery.helper;

public class DateTrim {

    public static String trim(String date) {
        String[] array = date.split("\\.");
        return array[0];
    }
}
