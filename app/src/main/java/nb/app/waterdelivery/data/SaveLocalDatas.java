package nb.app.waterdelivery.data;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SaveLocalDatas {

    private final String LOG_TITLE = "SaveLocalDatas";
    Activity activity;

    public SaveLocalDatas(Activity activity) {
        this.activity = activity;
    }

    public void saveUserDatas(int id, String name, String email, String phonenumber, String role_name, int role_id, short status, int jobs) {
        Log.i(LOG_TITLE, "Felhasználó adatok eltárolva");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("user_id", id);
        edit.putString("user_name", name);
        edit.putString("user_email", email);
        edit.putString("user_phone", phonenumber);
        edit.putString("user_role", role_name);
        edit.putInt("user_role_id", role_id);
        edit.putInt("user_status", status);
        edit.putInt("user_jobs", jobs);
        edit.apply();
    }

    //Kell, hogy mindig gyorsan el lehessen érni adminként a kívánt felhasználót
    public void saveCurrentUser(int id) {
        Log.i(LOG_TITLE, "Aktuális felhasználó azonosítójának mentése. (" + id + ")");
        SharedPreferences sp = activity.getSharedPreferences("current_user_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("user_id", id);
        edit.apply();
    }

    public int loadCurrentUserID() {
        Log.i(LOG_TITLE, "Aktuális felhasználó azonosítójának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("current_user_data", MODE_PRIVATE);
        return sp.getInt("user_id", -1);
    }


    //Ez a tervezet piszkozatának státuszát menti el. Szükség van rá, hogy tudni lehessen, mit töltsön be
    public void saveDraftStatus(boolean status) {
        SharedPreferences sp = activity.getSharedPreferences("save_draft_elements", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("draft_status", status);
        Log.i(LOG_TITLE, "A tervezet állapota '" + status + "' lett");
        edit.apply();
    }

    public boolean loadDraftStatus() {
        SharedPreferences sp = activity.getSharedPreferences("save_draft_elements", MODE_PRIVATE);
        Log.i(LOG_TITLE, "Tervezet állapota betöltve (" + sp.getBoolean("draft_status", false) + ")");
        return sp.getBoolean("draft_status", false);
    }


    //Saját adatainknak a megjelenítése
    public int loadUserID() {
        Log.i(LOG_TITLE, " Felhasználó azonosítójának betöltése ");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_id", -1);
    }

    public String loadUserName() {
        Log.i(LOG_TITLE, "Felhasználó nevének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_name", null);
    }

    public int loadUserRoleID() {
        Log.i(LOG_TITLE, "Felhasználó szerepkör azonosítójának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_role_id", -1);
    }

    public String loadUserRole() {
        Log.i(LOG_TITLE, "Felhasználó szerepkörének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_role", null);
    }

    public String loadUserEmail(){
        Log.i(LOG_TITLE, "Felhasználó e-mail címének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_email", null);
    }

    public String loadUserPhonenumber(){
        Log.i(LOG_TITLE, "Felhasználó telefonszámának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_phone", null);
    }

    public short loadUserStatus(){
        Log.i(LOG_TITLE, "Felhasználó állapotának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return Short.parseShort(String.valueOf(sp.getInt("user_status", -1)));
    }

    public int loadUserJobs(){
        Log.i(LOG_TITLE, "Felhasználó munkái darabszámának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_jobs", -1);
    }

    public boolean checkStayLoggedStatus() {
        SharedPreferences sp = activity.getSharedPreferences("open_check", MODE_PRIVATE);
        Log.i(LOG_TITLE, "Automatikus bejelentkeztetés állapota: " + sp.getBoolean("key", false));
        return sp.getBoolean("key", false);
    }

    public void saveStayLoggedStatus(boolean stay_logged_in) {
        Log.i(LOG_TITLE, "Automata bejelentkeztetés adatok elmentve. (" + stay_logged_in + ")");
        SharedPreferences sp = activity.getSharedPreferences("open_check", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("key", stay_logged_in);
        edit.apply();
    }

    //Database
    public void saveDatabaseValues(String ip, int port, String database, String username, String password) {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("db_ip", ip);
        edit.putInt("db_port", port);
        edit.putString("db_name", database);
        edit.putString("db_username", username);
        edit.putString("db_password", password);
        Log.i(LOG_TITLE, "Adatbázis adatok eltárolása. (" + ip + ", " + port + ")");
        edit.apply();
    }

    public String loadIP() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        Log.i(LOG_TITLE, "loadIP(" + sp.getString("db_ip", null)+ ")");
        return sp.getString("db_ip", null);
    }

    public int loadPort() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        Log.i(LOG_TITLE, "loadPort(" + sp.getInt("db_port", -1) + ")");
        return sp.getInt("db_port", -1);
    }

    public String loadDB() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_name", null);
    }

    public String loadUsername() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_username", null);
    }

    public String loadPassword() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_password", null);
    }

}
