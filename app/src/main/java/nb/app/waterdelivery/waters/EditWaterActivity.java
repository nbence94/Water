package nb.app.waterdelivery.waters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;

public class EditWaterActivity extends AppCompatActivity {

    private final String LOG_TITLE = "EditWaterActivity";

    DatabaseHelper dh;

    Toolbar toolbar;
    FloatingActionButton save_water_button;
    EditText name_field, cost_field;

    int water_id, cost_value;
    String name_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);

        dh = new DatabaseHelper(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.new_water_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.edit_water_title);

        getIntentData();

        //Elemek
        name_field = findViewById(R.id.new_water_name_gui);
        cost_field = findViewById(R.id.new_water_price_gui);
        name_field.setText(name_value);
        cost_field.setText("" + cost_value);

        //Módosítás mentése
        save_water_button = findViewById(R.id.new_water_save_gui);

        save_water_button.setOnClickListener(v -> {
            String name_value = name_field.getText().toString();
            String price_value = cost_field.getText().toString();

            if(name_value.isEmpty() || price_value.isEmpty()) {

                if (name_value.isEmpty()) {
                    name_field.setError("Kötelező mező!");
                    name_field.requestFocus();
                }

                if (price_value.isEmpty()) {
                    cost_field.setError("Kötelező mező!");
                    cost_field.requestFocus();
                }

                return;
            }

            if(!dh.sql("UPDATE " + dh.WATERS + " SET Name = '" + name_value + "', Price = " + price_value + " WHERE ID = " + water_id + ";")) {
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                return;
            }

            setResult(1);
            finish();
            Intent back = new Intent(this, WatersActivity.class);
            startActivity(back);
            Toast.makeText(this, "Mentés", Toast.LENGTH_SHORT).show();
        });
    }

    private void getIntentData() {
        if(getIntent().hasExtra("water_id")) {
            water_id = getIntent().getIntExtra("water_id" , - 1);
            cost_value = getIntent().getIntExtra("water_cost" , - 1);
            name_value = getIntent().getStringExtra("water_name" );
            Log.i(LOG_TITLE, "ID: " + water_id + " Name: " + name_value + " Cost: " + cost_value);
        }
    }
}