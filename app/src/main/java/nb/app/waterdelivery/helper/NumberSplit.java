package nb.app.waterdelivery.helper;

import android.annotation.SuppressLint;

public class NumberSplit {
    @SuppressLint("DefaultLocale")
    public static String splitNum(int number) {
        return String.format("%,d", number).replace(",", " ");
    }
}
