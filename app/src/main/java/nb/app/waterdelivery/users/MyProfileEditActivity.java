package nb.app.waterdelivery.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DataCoding;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class MyProfileEditActivity extends AppCompatActivity {

    private final String LOG_TITLE = "MyProfileEditActivity";

    DatabaseHelper dh;
    DataCoding dc;
    SaveLocalDatas sld;

    EditText phone_field, email_field, password_field, password_confirm_field;
    FloatingActionButton save_button;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_edit);

        dh = new DatabaseHelper(this, this);
        dc = new DataCoding();
        sld = new SaveLocalDatas(this);

        //Toolbar
        toolbar = findViewById(R.id.my_profile_edit_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek inicializálása
        phone_field = findViewById(R.id.edit_profile_phone_gui);
        email_field = findViewById(R.id.edit_profile_email_gui);
        password_field = findViewById(R.id.edit_profile_password_gui);
        password_confirm_field = findViewById(R.id.edit_profile_password_confirm_gui);
        save_button = findViewById(R.id.profile_edit_save_Button_gui);

        //Adatok megjelenítése
        phone_field.setText(sld.loadUserPhonenumber());
        email_field.setText(sld.loadUserEmail());

        //Mentés
        save_button.setOnClickListener(v -> {
            String email_value = email_field.getText().toString();
            String phone_value = phone_field.getText().toString();
            String password_value = password_field.getText().toString();
            String password_confirm_value = password_confirm_field.getText().toString();


            if(email_value.isEmpty() || phone_value.isEmpty()) {

                if(email_value.isEmpty()) {
                    email_field.setError("Helytelen formátum!");
                    email_field.requestFocus();
                }

                if(phone_value.isEmpty()) {
                    phone_field.setError("Kötelező mező!");
                    phone_field.requestFocus();
                }

                Log.e(LOG_TITLE, "Hiba felvitelkor.");
                return;
            }

            if(!dh.checkEmail(dh.USERS, email_value, "WHERE ID != " + sld.loadUserID())) {
                email_field.setError("Az e-mail foglalt!");
                email_field.requestFocus();
                return;
            }

            if((password_value.isEmpty() && !password_confirm_value.isEmpty()) || (!password_value.isEmpty() && password_confirm_value.isEmpty())) {
                password_confirm_field.setError("Mindkét jelszó megadása kötelező!");
                password_confirm_field.requestFocus();

                password_field.setError("Mindkét jelszó megadása kötelező!");
                password_field.requestFocus();
            }

            if(!password_value.isEmpty() && !password_confirm_value.isEmpty() && !password_value.equals(password_confirm_value)) {
                password_field.setError("A megadott jelszavak nem egyeznek!");
                password_field.requestFocus();
                password_confirm_field.setError("A megadott jelszavak nem egyeznek!");
                password_confirm_field.requestFocus();

                Log.e(LOG_TITLE, "Jelszavak nem egyeznek");
                return;
            }

            //Mentés
            String update;
            if(!password_value.isEmpty() && !password_confirm_value.isEmpty()) {
                try {
                    password_value = dc.encrypt(password_value);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TITLE, "Sikertelen jelszó titkosítás.");
                }

                update = "UPDATE " + dh.USERS + " SET Email = '" + email_value +
                        "', Phone = '" + phone_value +
                        "', Password = '" + password_value +
                        "' WHERE ID = " + sld.loadUserID() + ";";

            } else {

                update = "UPDATE " + dh.USERS + " SET Email = '" + email_value +
                        "', Phone = '" + phone_value + "' WHERE ID = " + sld.loadUserID() + ";";

            }

            if(!dh.sql(update)) {
                Log.i(LOG_TITLE, update);
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                return;
            }

            sld.saveUserDatas(sld.loadUserID(), sld.loadUserName(), email_value, phone_value, sld.loadUserRole(), sld.loadUserRoleID(), sld.loadUserStatus(), sld.loadUserJobs());
            Toast.makeText(this, "Mentés sikeres", Toast.LENGTH_SHORT).show();
            setResult(1);



        });
    }
}