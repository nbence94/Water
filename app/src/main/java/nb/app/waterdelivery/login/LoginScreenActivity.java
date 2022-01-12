package nb.app.waterdelivery.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nb.app.waterdelivery.MainActivity;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DataCoding;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class LoginScreenActivity extends AppCompatActivity {

    private final String LOG_TITLE = "LoginScreen";

    DataCoding dc;
    DatabaseHelper dh;
    SaveLocalDatas sld;

    Button login_button;
    EditText email_field, password_field;
    CheckBox stay_logged_in;
    ImageView settings_button;

    String user_role, user_name, user_email, user_phone;
    int user_id = -1, user_roleid, user_jobs;
    short user_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        dc = new DataCoding();
        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        login_button = findViewById(R.id.login_button_gui);
        email_field = findViewById(R.id.login_email_gui);
        password_field = findViewById(R.id.login_password_gui);
        stay_logged_in = findViewById(R.id.login_stay_logged_gui);
        settings_button = findViewById(R.id.login_settings_gui);

        email_field.setText("admin@admin.com");
        //password_field.setText("admin");

        //Ha bejelentkezve marad
        if(sld.checkStayLoggedStatus()) {
            Connection conn = dh.connectionClass(this);

            if(conn != null) {

                finish();
                Intent main = new Intent(LoginScreenActivity.this, MainActivity.class);
                startActivity(main);
                return;

            } else {
                Toast.makeText(this, "Sikertelen belépés", Toast.LENGTH_SHORT).show();
            }
        }

        login_button.setOnClickListener(v -> {
            String email_value = email_field.getText().toString();
            String password_value = password_field.getText().toString();
            //Ellenőrzés
            if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches() || password_value.isEmpty()) {
                if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                    email_field.setError("A mező üres vagy helytelen formátum");
                    email_field.requestFocus();
                }

                if(password_value.isEmpty()) {
                    password_field.setError("Üresen hagyott mező!");
                    password_field.requestFocus();
                }

                Log.e(LOG_TITLE, "Bejelentkezés hiba. (" + email_value + ")");
                return;
            }
            if(!getLoginData(email_value, password_value)) {
                return;
            }

            finish();
            Intent main_screen = new Intent(LoginScreenActivity.this, MainActivity.class);
            startActivity(main_screen);

            //Ha a checkbox ki van pipálva, akkor adatok mentése
            sld.saveStayLoggedStatus(stay_logged_in.isChecked());
        });

        settings_button.setOnClickListener(v -> {
            Intent settings = new Intent(this, LoginSettingsActivity.class);
            startActivity(settings);
            finish();
        });
    }

    private boolean getLoginData(String email, String password) {
        String select = "";
        Connection con = dh.connectionClass(this);


        try {
            if(con != null) {
                password = dc.encrypt(password);
                select = "SELECT * FROM " + dh.USERS + " u, " + dh.ROLES + " r WHERE u.RoleID = r.ID AND u.Email = '" + email + "' AND u.Password='" + password.trim() + "';";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {
                    user_role = rs.getString(11);//Szerepkör neve
                    user_name = rs.getString(dh.USERS_NAME_INDEX);
                    user_email = rs.getString(dh.USERS_EMAIL_INDEX);
                    user_phone = rs.getString(dh.USERS_PHONE_INDEX);
                    user_jobs = Integer.parseInt(rs.getString(dh.USERS_JOBS_INDEX));
                    user_id = Integer.parseInt(rs.getString(dh.USERS_ID_INDEX));
                    user_roleid = Integer.parseInt(rs.getString(dh.USERS_ROLEID_INDEX));
                    user_status = Short.parseShort(rs.getString(dh.USERS_STATUS_INDEX));

                }

                sld.saveUserDatas(user_id, user_name, user_email, user_phone, user_role, user_roleid, user_status, user_jobs);
                Log.i(LOG_TITLE, select);
                Log.i(LOG_TITLE, user_id + ", " + user_name+ ", " + user_email+ ", " + user_phone+ ", " + user_role+ ", " + user_roleid+ ", " + user_status+ ", " + user_jobs);
            } else {
                Toast.makeText(this, "Nincs kapcsolat", Toast.LENGTH_SHORT).show();
            }

            
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Kódolás hiba");

        }

        if(user_id < 0) {
            Toast.makeText(this, "Helytelen e-mail vagy jelszó!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Nem jó ID");
            return false;

        } else if(user_status == 0) {

            Toast.makeText(this, "Ez a fiók inaktív!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "A felhasználó inaktív");
            return false;
        }

        return true;
    }
}