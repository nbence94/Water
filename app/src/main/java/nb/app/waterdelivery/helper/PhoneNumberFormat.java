package nb.app.waterdelivery.helper;

public class PhoneNumberFormat {

    final static int mobile_length = 11;
    final int phone_length = 10;
    //06303503697

    public static String mobileFormat(String number) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < number.length(); i++) {
            result.append(number.charAt(i));
            if((i + 1) == 2) result.append(" ");
            if((i + 1) == 4) result.append(" ");
            if((i + 1) == 7) result.append("-");

        }

        return result.toString();
    }

    public static String phoneFormat(String number) {
        int korzet_szam, dash;
        if(number.length() == 10) {
            korzet_szam = 4;
            dash = 7;
        }
        else {
            korzet_szam = 3;
            dash = 6;
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < number.length(); i++) {
            result.append(number.charAt(i));
            if((i + 1) == 2) result.append(" ");
            if((i + 1) == korzet_szam) result.append(" ");
            if((i + 1) == dash) result.append("-");

        }

        return result.toString();
    }

    public boolean checkMobile(String number) {
        return mobile_length == number.length();
    }

    public boolean checkPhone(String number) {
        return number.length() == 10 || number.length() == 9;
    }


}
