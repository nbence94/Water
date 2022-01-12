package nb.app.waterdelivery.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Users;

public class MyProfileActivity extends AppCompatActivity {

    private final String LOG_TITLE = "MyProfileActivity";
    Toolbar toolbar;
    SaveLocalDatas sld;
    DatabaseHelper dh;

    TextView name_text, email_text, phone_text, role_text, jobnumber_text;
    ImageView settings_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        sld = new SaveLocalDatas(this);
        dh = new DatabaseHelper(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.my_profile_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek inicializálása
        name_text = findViewById(R.id.profile_name_gui);
        email_text = findViewById(R.id.profile_email_gui);
        role_text = findViewById(R.id.profile_role_gui);
        phone_text = findViewById(R.id.profile_phone_gui);
        jobnumber_text = findViewById(R.id.profile_jobs_gui);
        settings_button = findViewById(R.id.my_profile_settings_gui);

        //Adatok megjelenítése
        name_text.setText(sld.loadUserName());
        email_text.setText(sld.loadUserEmail());
        role_text.setText(sld.loadUserRole());
        phone_text.setText(sld.loadUserPhonenumber());
        String jobs_ = "Elvégzett munkák: " + sld.loadUserJobs();
        jobnumber_text.setText(jobs_);

        //Beállítások
        settings_button.setOnClickListener(v -> {
            Intent setting = new Intent(this, MyProfileEditActivity.class);
            startActivity(setting);
        });
    }

}