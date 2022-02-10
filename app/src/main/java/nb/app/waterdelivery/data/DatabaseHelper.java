package nb.app.waterdelivery.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.AccessibleObject;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper {

    Context context;
    Activity activity;
    SaveLocalDatas sld;

    final private String LOG_TITLE = "DatabaseHelper";

    final private String IP = "192.168.0.17";
    final private String PORT = "3306";
    final private String USERNAME = "teszt_user";
    final private String PASSWORD = "jelszo123";
    final private String DATABASE = "WaterDelivery";

    //Szerepkörök
    final public int ADMIN_ROLE = 3;
    final public int ROGZITO_ROLE = 2;

    //Táblák
    final public String USERS = "Users";
    final public String ROLES = "Roles";
    final public String WATERS = "Waters";
    final public String CUSTOMERS = "Customers";
    final public String JOBS = "Jobs";
    final public String CIJ = "CustomerInJob";
    final public String JAW = "JobAndWater";
    final public String CAW = "CustomerAndWaters";
    final public String DRAFT = "Draft";
    final public String EDITDRAFT = "JawDraft";
    final public String SETTLEMENT = "Settlement";
    final public String JIS = "JobsInSettlement";

    //Felhasználók
    final public int USERS_ID_INDEX = 1;
    final public int USERS_CREATED_INDEX = 2;
    final public int USERS_NAME_INDEX = 3;
    final public int USERS_EMAIL_INDEX = 4;
    final public int USERS_PASSWORD_INDEX = 5;
    final public int USERS_PHONE_INDEX = 6;
    final public int USERS_JOBS_INDEX = 7;
    final public int USERS_ROLEID_INDEX = 8;
    final public int USERS_STATUS_INDEX = 9;

    //Munkák
    final public int JOBS_ID_INDEX = 1;
    final public int JOBS_CREATED_INDEX = 2;
    final public int JOBS_FINISH_INDEX = 3;
    final public int JOBS_NAME_INDEX = 4;
    final public int JOBS_INCOME_INDEX = 5;
    final public int JOBS_USERID_INDEX = 6;

    //Megrendelők
    final public int CUSTOMERS_ID_INDEX = 1;
    final public int CUSTOMERS_CREATED_INDEX = 2;
    final public int CUSTOMERS_NAME_INDEX = 3;
    final public int CUSTOMERS_CITY_INDEX = 4;
    final public int CUSTOMERS_ADDRESS_INDEX = 5;
    final public int CUSTOMERS_EMAIL_INDEX = 6;
    final public int CUSTOMERS_PHONE_INDEX = 7;
    final public int CUSTOMERS_PHONEPLUS_INDEX = 8;
    final public int CUSTOMERS_WATERWEEK_INDEX = 9;
    final public int CUSTOMERS_BILL_INDEX = 10;
    final public int CUSTOMERS_USERID_INDEX = 11;

    //Szerepkörök
    final public int ROLES_ID_INDEX = 1;
    final public int ROLES_NAME_INDEX = 2;

    //Vizek
    final public int WATERS_ID_INDEX = 1;
    final public int WATERS_NAME_INDEX = 2;
    final public int WATERS_COST_INDEX = 3;

    //CustomerAndWaters
    final public int CAW_CUSTOMERID_INDEX = 1;
    final public int CAW_WATERID_INDEX = 2;

    //CustomerInJob
    final public int CIJ_JOBID_INDEX = 1;
    final public int CIJ_CUSTOMERID_INDEX = 2;
    final public int CIJ_FINISH_INDEX = 3;

    //JobAndWater
    final public int JAW_JOBID_INDEX = 1;
    final public int JAW_CUSTOMERID_INDEX = 2;
    final public int JAW_WATERID_INDEX = 3;
    final public int JAW_WATERAMOUNT_INDEX = 4;

    //Draft
    final public int DRAFT_USERID_INDEX = 1;
    final public int DRAFT_CUSTOMERID_INDEX = 2;
    final public int DRAFT_WATERID_INDEX = 3;
    final public int DRAFT_AMOUNT_INDEX = 4;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    Connection con;

    public DatabaseHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.sld = new SaveLocalDatas(activity);

        try {
            con = this.connectionClass(context);
        } catch (Exception e) {
            Toast.makeText(context, "Sikertelen kapcsolódás", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(Context context) throws Exception {
        Connection con = null;
        this.sld = new SaveLocalDatas(activity);

        //IP: 192.168.0.17 - PORT: 3306 - teszt_user / jelszo123 - WaterDelivery

        String ip = sld.loadIP();
        int port = sld.loadPort();
        String database = "WaterDelivery";
        String username = sld.loadUsername();
        String password = sld.loadPassword();

        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        String conURL = "jdbc:mysql://" + ip + ":" + port + "/" + database;

        if (ip != null && !ip.isEmpty() && port != -1 &&
                !username.isEmpty() && !password.isEmpty()) {
            if (!ip.equals("-") && !username.equals("-") && !password.equals("-")) {

                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(conURL, username, password);


            } else {
                Toast.makeText(context, "Nincsenek kapcsolódási adatok", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Nincsenek kapcsolódási adatok", Toast.LENGTH_SHORT).show();
        }

        Log.i(LOG_TITLE, "Sikeres adatbázis kapcsolat. (" + ip + ", " + port + ", " + DATABASE + ")");
        return con;

    }

    private static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public boolean checkEmail(String table, String email, String condition) {
        String select = "SELECT Email FROM " + table + " " + condition + ";";

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while (rs.next()) {

                    if (rs.getString(1).equals(email)) {
                        return false;
                    }
                }
            }
            Log.i(LOG_TITLE, "Adatbáizs lekérdezés (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés (" + select + ")");
        }

        return true;
    }

    public boolean insert(String[] values, String[] columns, String table) {

        StringBuilder values_string = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (isNumeric(values[i])) values_string.append(values[i]);
            else values_string.append("'").append(values[i]).append("'");
            if (i < values.length - 1) values_string.append(",");
        }

        StringBuilder columns_string = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            columns_string.append(columns[i]);
            if (i < columns.length - 1) columns_string.append(",");
        }

        String insert = "INSERT INTO " + table + " (" + columns_string.toString() + ") VALUES (" + values_string.toString() + ");";
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Log.i(LOG_TITLE, insert);
                Statement st = con.createStatement();
                st.executeUpdate(insert);
            }

            Log.i(LOG_TITLE, "Adatbázis utasítás - SIKERES (" + insert + ")");
        } catch (SQLException throwables) {
            Log.e(LOG_TITLE, "Adatbázis utasítás - SIKERTELEN (" + insert + ")");
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete(String table, String condition) {
        String delete = "DELETE FROM " + table;
        if (!condition.equals("")) delete += " WHERE " + condition;
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Log.i(LOG_TITLE, delete);
                Statement st = con.createStatement();
                st.executeUpdate(delete);
            }

            Log.i(LOG_TITLE, "TÖRLÉS - SIKERES (" + delete + ")");
        } catch (SQLException throwables) {
            Log.e(LOG_TITLE, "TÖRLÉS - SIKERTELEN (" + delete + ")");
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean sql(String sql) {
        try {
            if (con != null) {
                Log.i(LOG_TITLE, sql);
                Statement st = con.createStatement();
                st.executeUpdate(sql);
            }

            Log.i(LOG_TITLE, "Adatbázis utasítás - SIKERES (" + sql + ")");
        } catch (SQLException throwables) {
            Log.e(LOG_TITLE, "Adatbázis utasítás - SIKERTELEN (" + sql + ")");
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public int getNewID(String table) {
        String s = "SELECT MAX(ID) FROM " + table + ";";
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(s);

                while (rs.next()) {
                    return Integer.parseInt(rs.getString(1));
                }
            }

            Log.i(LOG_TITLE, "Legfrissebb ID lekérdezve (" + s + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Nem sikerült lekérni az új ID-t!");
            return -1;
        }
        return -1;
    }

    public int getExactInt(String select) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while (rs.next()) {
                    return Integer.parseInt(rs.getString(1));
                }
            }

            Log.i(LOG_TITLE, "Legfrissebb ID lekérdezve (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikeretelen lekérdezés. (" + select + ")");
            return -1;
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public String getExactString(String select) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while (rs.next()) {
                    return rs.getString(1);
                }
            }

            Log.i(LOG_TITLE, "Sikeres lekérdezés (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikeretelen lekérdezés. (" + select + ")");
            return "-";
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return "-";
    }

    public void getSettlementData(String select, ArrayList<Settlement> settlement_list) {
        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int id, user_id, finisher_id;
                String name, created, finish;

                while (rs.next()) {
                    id = Integer.parseInt(rs.getString(1));
                    name = rs.getString(2);
                    created = rs.getString(3);
                    finish = rs.getString(4);
                    user_id = Integer.parseInt(rs.getString(5));
                    finisher_id = Integer.parseInt(rs.getString(6));

                    settlement_list.add(new Settlement(id, name, created, finish, user_id, finisher_id));
                }
            }
            Log.i(LOG_TITLE, "Elszámolások lekérdezése - SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Elszámolások lekérdezése - SIKERTELEN (" + select + ")");
        }
    }


    public void getDraftData(String select, ArrayList<Draft> draft_list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int user_id, customer_id, water_id, water_amount;

                while (rs.next()) {
                    user_id = Integer.parseInt(rs.getString(DRAFT_USERID_INDEX));
                    customer_id = Integer.parseInt(rs.getString(DRAFT_CUSTOMERID_INDEX));
                    water_id = Integer.parseInt(rs.getString(DRAFT_WATERID_INDEX));
                    water_amount = Integer.parseInt(rs.getString(DRAFT_AMOUNT_INDEX));
                    draft_list.add(new Draft(user_id, customer_id, water_id, water_amount));
                }
            }
            Log.i(LOG_TITLE, "Piszkozat lekérdezése - SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Piszkozat lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getJawDraftData(String select, ArrayList<JawDraft> draft_list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int user_id, customer_id, water_id, water_amount;

                while (rs.next()) {
                    user_id = Integer.parseInt(rs.getString(DRAFT_USERID_INDEX));
                    customer_id = Integer.parseInt(rs.getString(DRAFT_CUSTOMERID_INDEX));
                    water_id = Integer.parseInt(rs.getString(DRAFT_WATERID_INDEX));
                    water_amount = Integer.parseInt(rs.getString(DRAFT_AMOUNT_INDEX));
                    draft_list.add(new JawDraft(user_id, customer_id, water_id, water_amount));
                }
            }
            Log.i(LOG_TITLE, "Piszkozat lekérdezése - SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Piszkozat lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getCIJData(String select, ArrayList<CustomersInJob> cij_list) {
        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int job_id, customer_id;
                String finish;

                while (rs.next()) {
                    job_id = Integer.parseInt(rs.getString(CIJ_JOBID_INDEX));
                    customer_id = Integer.parseInt(rs.getString(CIJ_CUSTOMERID_INDEX));
                    finish = rs.getString(CIJ_FINISH_INDEX);
                    cij_list.add(new CustomersInJob(job_id, customer_id, finish));
                }
            }
            Log.i(LOG_TITLE, "Munkához tarotzó megrendelők lekérdezése - SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Munkához tarotzó megrendelők lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getJAWData(String select, ArrayList<JobAndWaters> jaw_list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int job_id, customer_id, water_id, amount;

                while (rs.next()) {
                    job_id = Integer.parseInt(rs.getString(JAW_JOBID_INDEX));
                    customer_id = Integer.parseInt(rs.getString(JAW_CUSTOMERID_INDEX));
                    water_id = Integer.parseInt(rs.getString(JAW_WATERID_INDEX));
                    amount = Integer.parseInt(rs.getString(JAW_WATERAMOUNT_INDEX));
                    jaw_list.add(new JobAndWaters(job_id, customer_id, water_id, amount));
                }
            }
            Log.i(LOG_TITLE, "Munkához tarotzó vizek lekérdezése - SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Munkához tarotzó vzek lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getRolesData(ArrayList<Roles> roles_list) {
        String select = "SELECT * FROM " + ROLES + ";";
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String role_name;
                int role_id;

                while (rs.next()) {
                    role_id = Integer.parseInt(rs.getString(ROLES_ID_INDEX));
                    role_name = rs.getString(ROLES_NAME_INDEX);
                    roles_list.add(new Roles(role_id, role_name));
                }
            }

            Log.i(LOG_TITLE, "Szerepkörök lekérdezése - SIKERES (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Szerepkörök lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getJobsData(String select, ArrayList<Jobs> list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, created, finish;
                int id, userid, income;

                while (rs.next()) {
                    id = Integer.parseInt(rs.getString(JOBS_ID_INDEX));
                    created = rs.getString(JOBS_CREATED_INDEX);
                    finish = rs.getString(JOBS_FINISH_INDEX);
                    name = rs.getString(JOBS_NAME_INDEX);
                    income = Integer.parseInt(rs.getString(JOBS_INCOME_INDEX));
                    userid = Integer.parseInt(rs.getString(CUSTOMERS_EMAIL_INDEX));

                    list.add(new Jobs(id, name, created, finish, income, userid));
                }

                Log.i(LOG_TITLE, "Munkák lekérdezése - SIKERES (" + select + ")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Munkák lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getCustomersData(String select, ArrayList<Customers> list) {
        //String s = "SELECT * FROM " + CUSTOMERS + " " +  condition + ";";
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String fullname, created, city, address, email, phone, phoneplus;
                int id, userid, water_week, bill;

                while (rs.next()) {
                    id = Integer.parseInt(rs.getString(CUSTOMERS_ID_INDEX));
                    created = rs.getString(CUSTOMERS_CREATED_INDEX);
                    fullname = rs.getString(CUSTOMERS_NAME_INDEX);
                    city = rs.getString(CUSTOMERS_CITY_INDEX);
                    address = rs.getString(CUSTOMERS_ADDRESS_INDEX);
                    email = rs.getString(CUSTOMERS_EMAIL_INDEX);
                    phone = rs.getString(CUSTOMERS_PHONE_INDEX);
                    phoneplus = rs.getString(CUSTOMERS_PHONEPLUS_INDEX);

                    water_week = Integer.parseInt(rs.getString(CUSTOMERS_WATERWEEK_INDEX));
                    bill = Integer.parseInt(rs.getString(CUSTOMERS_BILL_INDEX));
                    userid = (rs.getString(CUSTOMERS_USERID_INDEX) != null) ? Integer.parseInt(rs.getString(CUSTOMERS_USERID_INDEX)) : -1;

                    list.add(new Customers(id, created, fullname, city, address, email, phone, phoneplus, water_week, bill, userid));
                }
            }

            Log.i(LOG_TITLE, "Megrendelők lekérdezés - SIKERES (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Megrendelők lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getCAWData(ArrayList<CustomerAndWaters> caw_list, String condition) {
        String select = "SELECT * FROM " + CAW + " " + condition + ";";
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int customerid, waterid;

                while (rs.next()) {
                    customerid = Integer.parseInt(rs.getString(CAW_CUSTOMERID_INDEX));
                    waterid = Integer.parseInt(rs.getString(CAW_WATERID_INDEX));
                    caw_list.add(new CustomerAndWaters(customerid, waterid));
                }
            }

            Log.i(LOG_TITLE, "Megrendelők vizeinek lekérdezés - SIKERES (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Megrendelők vizeinek lekérdezés - SIKERTELEN (" + select + ")");
        }
    }

    public void getWatersData(String select, ArrayList<Waters> waters_list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name;
                int id, cost;

                while (rs.next()) {
                    id = Integer.parseInt(rs.getString(WATERS_ID_INDEX));
                    name = rs.getString(WATERS_NAME_INDEX);
                    cost = Integer.parseInt(rs.getString(WATERS_COST_INDEX));
                    waters_list.add(new Waters(id, name, cost));
                }
            }

            Log.i(LOG_TITLE, "Vizek lekérdezése - SIKERES (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Vizek lekérdezése - SIKERTELEN (" + select + ")");
        }
    }

    public void getCustomData(String select, ArrayList<CustomData> data_list) {
        //Connection con = this.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name;
                int id, number;

                while (rs.next()) {
                    id = Integer.parseInt(rs.getString(1));
                    name = rs.getString(2);
                    number = Integer.parseInt(rs.getString(3));
                    data_list.add(new CustomData(id, name, number));
                }
            }

            Log.i(LOG_TITLE, "SIKERES (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "SIKERTELEN (" + select + ")");
        }
    }

}
