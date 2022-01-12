package nb.app.waterdelivery.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class LoginSettingsActivity extends AppCompatActivity {

    private final String LOG_TITLE = "LoginSettingsActivity";

    EditText ip_field, port_field, user_field, pass_field;
    FloatingActionButton save_button;
    SaveLocalDatas sld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_settings);

        sld = new SaveLocalDatas(this);

        ip_field = findViewById(R.id.login_settings_ip_gui);
        port_field = findViewById(R.id.login_settings_port_gui);
        user_field = findViewById(R.id.login_settings_user_gui);
        pass_field = findViewById(R.id.login_settings_password_gui);
        save_button = findViewById(R.id.login_settings_save_gui);

        ip_field.setText(sld.loadIP());
        port_field.setText(String.valueOf(sld.loadPort()));
        user_field.setText(sld.loadUsername());
        pass_field.setText(sld.loadPassword());

        save_button.setOnClickListener(v -> {
            String ip_value = ip_field.getText().toString();
            String user_value = user_field.getText().toString();
            String password_value = pass_field.getText().toString();
            int port_value = Integer.parseInt(port_field.getText().toString());

            if(ip_value.isEmpty()) ip_value = "-";
            if(port_field.getText().toString().isEmpty()) port_value = -1;
            if(user_value.isEmpty()) user_value = "-";
            if(password_value.isEmpty()) password_value = "-";

            sld.saveDatabaseValues(ip_value, port_value, "", user_value, password_value);
            Log.i(LOG_TITLE, "IP: " + ip_value + " - PORT: " + port_value);
            finish();
            Intent login = new Intent(this, LoginScreenActivity.class);
            startActivity(login);
        });

    }

}