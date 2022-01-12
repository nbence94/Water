package nb.app.waterdelivery.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Roles;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Users;

public class AdminCheckUserActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AdminCheckUserActivity";
    DatabaseHelper dh;
    SaveLocalDatas sld;
    MyAlertDialog mad;

    Toolbar toolbar;
    TextView name_text, phone_text, more_details_btn;
    CardView jobs_button, customers_button;
    ImageView settings_button;

    int user_id;
    ArrayList<Users> user_data_list;
    ArrayList<Roles> roles_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_user);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_user_detail_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek
        name_text = findViewById(R.id.user_name_gui);
        phone_text = findViewById(R.id.user_phone_number_gui);
        more_details_btn = findViewById(R.id.user_more_details_gui);
        jobs_button = findViewById(R.id.user_jobs_button_gui);
        customers_button = findViewById(R.id.user_customers_button_gui);
        settings_button = findViewById(R.id.admin_user_details_settings);

        //Adatok megjelenítése
        user_data_list = new ArrayList<>();
        roles_list = new ArrayList<>();
        getIntentData();
        dh.getRolesData(roles_list);
        getData("SELECT * FROM " + dh.USERS + " WHERE ID = " + user_id + ";");
        name_text.setText(user_data_list.get(0).getName());
        phone_text.setText(user_data_list.get(0).getPhone());

        more_details_btn.setOnClickListener(v -> {
            StringBuilder data = new StringBuilder();
            data.append("Név: ").append(user_data_list.get(0).getName()).append("\n")
                    .append("E-mail: ").append(user_data_list.get(0).getEmail()).append("\n")
                    .append("Telefonszám: ").append(user_data_list.get(0).getPhone()).append("\n")
                    .append("Státusz: ").append((user_data_list.get(0).getStatus() == 1) ? "Aktív" : "Inaktív").append("\n")
                    .append("Szerepkör: ").append(roles_list.get(user_data_list.get(0).getRoleid()).getName()).append("\n")
                    .append("\n")
                    .append("Szállításai: ").append(user_data_list.get(0).getJobnumber()).append(" db").append("\n");
            //AlertDialogHelper.setMessage(this, "Adatok", data.toString(), "Rendben");
            mad.AlertInfoDialog("Adatok", data.toString(), "Rendben");
        });

        jobs_button.setOnClickListener(v -> {
            Intent jobs = new Intent(AdminCheckUserActivity.this, AdminUserJobsActivity.class);
            jobs.putExtra("user_id", user_id);
            sld.saveCurrentUser(user_id);
            startActivity(jobs);
        });

        customers_button.setOnClickListener(v -> {
            Intent jobs = new Intent(AdminCheckUserActivity.this, AdminUserCustomersActivity.class);
            jobs.putExtra("user_id", user_id);
            sld.saveCurrentUser(user_id);
            startActivity(jobs);
        });

        settings_button.setOnClickListener(v -> {
            Intent update_user = new Intent(AdminCheckUserActivity.this, AdminUserEditActivity.class);
            update_user.putExtra("user_id", user_id);
            update_user.putExtra("name", user_data_list.get(0).getName());
            update_user.putExtra("email", user_data_list.get(0).getEmail());
            update_user.putExtra("pnumber", user_data_list.get(0).getPhone());
            update_user.putExtra("roleid", user_data_list.get(0).getRoleid());
            sld.saveCurrentUser(user_id);
            startActivity(update_user);
        });

    }

    private void getData(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, email, password, created, phone;
                int id, role_id, status, numofjobs;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.USERS_ID_INDEX));
                    created = rs.getString(dh.USERS_CREATED_INDEX);
                    name = rs.getString(dh.USERS_NAME_INDEX);
                    email = rs.getString(dh.USERS_EMAIL_INDEX);
                    phone = rs.getString(dh.USERS_PHONE_INDEX);
                    password = rs.getString(dh.USERS_PASSWORD_INDEX);
                    numofjobs = Integer.parseInt(rs.getString(dh.USERS_JOBS_INDEX));
                    role_id = Integer.parseInt(rs.getString(dh.USERS_ROLEID_INDEX)) - 1;
                    status = Integer.parseInt(rs.getString(dh.USERS_STATUS_INDEX));

                    user_data_list.add(new Users(id, created, name, email, password, phone, numofjobs, role_id, status));
                }
            }

            Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
    }

    private void getIntentData() {
        if(getIntent().hasExtra("user_id")) {
            user_id = getIntent().getIntExtra("user_id", 0);
        } else {
            user_id = sld.loadCurrentUserID();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }
}