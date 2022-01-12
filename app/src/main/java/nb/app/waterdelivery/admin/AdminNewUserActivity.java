package nb.app.waterdelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.data.DataCoding;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Roles;
import nb.app.waterdelivery.data.Users;

public class AdminNewUserActivity extends AppCompatActivity implements OnDialogChoice {

    final private String LOG_TITLE = "AdminNewUserActivity";

    DatabaseHelper dh;
    DataCoding dc;
    MyAlertDialog mad;

    Toolbar toolbar;
    FloatingActionButton save_user_button;
    EditText name_field, email_field, phone_field, password_field, password_confirm_field;
    TextView role_field;

    String name_value, email_value, phone_value, password_value, password_confirm_value;

    ArrayList<Roles> role_list;
    String[] roles_array;
    int chosen_role = -1;
    int tmp_role;

    boolean[] chosen_role_item;
    boolean[] tmp_chosen_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_user);

        //Osztályok
        dh = new DatabaseHelper(this, this);
        dc = new DataCoding();
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.new_user_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name_field = findViewById(R.id.admin_user_fullname_gui);
        email_field = findViewById(R.id.admin_user_email_gui);
        phone_field = findViewById(R.id.admin_user_phone_gui);
        password_field = findViewById(R.id.admin_user_password_gui);
        password_confirm_field = findViewById(R.id.admin_user_password_confirm_gui);
        role_field = findViewById(R.id.admin_user_role_gui);

        //Szerepkörök betöltése
        role_list = new ArrayList<>();
        dh.getRolesData(role_list);

        //Szerepkörök áttétele tömbbe
        roles_array = new String[role_list.size()];
        chosen_role_item = new boolean[role_list.size()];
        tmp_chosen_item = new boolean[role_list.size()];

        for(int i = 0; i < role_list.size(); i++) {
            roles_array[i] = role_list.get(i).getName();
        }

        role_field.setOnClickListener(v -> {
            /*AlertDialog.Builder roles = new AlertDialog.Builder(this);
            roles.setTitle("Válassz szerepkört");
            tmp_role = chosen_role;

            roles.setSingleChoiceItems(roles_array, chosen_role, (dialog, which) -> {
                chosen_role = which;
            });

            roles.setPositiveButton("Rendben", (dialog, which) -> {
                role_field.setText(roles_array[chosen_role]);
                dialog.dismiss();
            });

            roles.setNegativeButton("Mégse", (dialog, which) -> {
                chosen_role = tmp_role;
                dialog.dismiss();
            });

            roles.show();*/
            mad.AlertSingleSelectDialog("Válassz szerepkört", roles_array, chosen_role_item, tmp_chosen_item, "Rendben", "Mégsem", null, 0,this);
        });


        //Felvitel mentése
        save_user_button = findViewById(R.id.new_user_save_gui);

        save_user_button.setOnClickListener(v -> {
            name_value = name_field.getText().toString();
            email_value = email_field.getText().toString();
            phone_value = phone_field.getText().toString();
            password_value = password_field.getText().toString();
            password_confirm_value = password_confirm_field.getText().toString();


            if(name_value.isEmpty() || email_value.isEmpty() || phone_value.isEmpty() || password_value.isEmpty() || password_confirm_value.isEmpty() || chosen_role == -1 ) {

                if(name_value.isEmpty()) {
                    name_field.setError("Kötelező mező!");
                    name_field.requestFocus();
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                    email_field.setError("Helytelen formátum!");
                    email_field.requestFocus();
                }

                if(phone_value.isEmpty()) {
                    phone_field.setError("Kötelező mező!");
                    phone_field.requestFocus();
                }

                if(password_value.isEmpty()) {
                    password_field.setError("Kötelező mező!");
                    password_field.requestFocus();
                }

                if(password_confirm_value.isEmpty()) {
                    password_confirm_field.setError("Kötelező mező!");
                    password_confirm_field.requestFocus();
                }

                if(chosen_role == -1) {
                    role_field.setError("Kötelező mező!");
                    role_field.requestFocus();
                }

                Log.e(LOG_TITLE, "Hiba felvitelkor.");
                return;
            }

            if(!password_value.equals(password_confirm_value)) {
                password_field.setError("A megadott jelszavak nem egyeznek!");
                password_field.requestFocus();
                password_confirm_field.setError("A megadott jelszavak nem egyeznek!");
                password_confirm_field.requestFocus();

                Log.e(LOG_TITLE, "Jelszavak nem egyeznek");
                return;
            }

            if(!dh.checkEmail(dh.USERS, email_value, "")) {
                email_field.setError("Az e-mail foglalt!");
                email_field.requestFocus();
                return;
            }

            //Mentés
            try {
                password_value = dc.encrypt(password_value);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TITLE, "Sikertelen jelszó titkosítás.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String aktualis_datum = sdf.format(new Date());

            String insert = "INSERT INTO " + dh.USERS + " (Created, Fullname, Email, Password, Phone, NumberOfJobs, RoleID, Status) VALUES " +
                    "('" + aktualis_datum + "', '" + name_value + "', '" + email_value + "', '" + password_value.trim() + "',"
                    + " '" + phone_value + "', 0, " + chosen_role + ", 1);";//TODO ITt úgy volt, hoyg (chosen_role +1), de most elvileg már nem kell

            if(!dh.sql(insert)) {
                Log.i(LOG_TITLE, insert);
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Mentés", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
            Intent users_screen = new Intent(AdminNewUserActivity.this, AdminAllUsersActivity.class);
            startActivity(users_screen);
        });
    }

    @Override
    public void OnPositiveClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_role_item.length; i++) {
            if(chosen_role_item[i]) {
                chosen_role = role_list.get(i).getId();
                role_field.setText(roles_array[i]);
                Log.i(LOG_TITLE, "A választott szerepkör ID: " + chosen_role + " - Name: " + role_list.get(i).getName());
                break;
            }
        }
    }

    @Override
    public void OnNegativeClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_role_item.length; i++) {
            chosen_role_item[i] = tmp_chosen_item[i];
        }
    }
}