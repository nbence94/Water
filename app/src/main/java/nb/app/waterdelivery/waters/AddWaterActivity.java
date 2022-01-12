package nb.app.waterdelivery.waters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;

public class AddWaterActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AddWaterActivity";

    DatabaseHelper dh;

    Toolbar toolbar;
    FloatingActionButton save_water_button;
    EditText name_field, price_field;

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

        //elemek
        name_field = findViewById(R.id.new_water_name_gui);
        price_field = findViewById(R.id.new_water_price_gui);
        
        //Felvitel mentése
        save_water_button = findViewById(R.id.new_water_save_gui);
        
        save_water_button.setOnClickListener(v -> {
            String name_value = name_field.getText().toString();
            String price_value = price_field.getText().toString();

            if(name_value.isEmpty() || price_value.isEmpty()) {

                if (name_value.isEmpty()) {
                    name_field.setError("Kötelező mező!");
                    name_field.requestFocus();
                }

                if (price_value.isEmpty()) {
                    price_field.setError("Kötelező mező!");
                    price_field.requestFocus();
                }

                return;
            }

            if(!dh.sql("INSERT INTO " + dh.WATERS + " (Name, Price) VALUES ('" + name_value + "', " + price_value + ");")) {
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
}