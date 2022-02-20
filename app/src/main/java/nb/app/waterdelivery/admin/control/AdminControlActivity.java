package nb.app.waterdelivery.admin.control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import java.util.Objects;
import nb.app.waterdelivery.R;

public class AdminControlActivity extends AppCompatActivity {

    Toolbar toolbar;
    CardView user_jobs_button, month_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_control_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_jobs_button = findViewById(R.id.control_user_jobs_gui);
        month_button = findViewById(R.id.control_month_settlement_gui);

        //Leadott Munkák
        user_jobs_button.setOnClickListener(v -> {
            Intent jobs = new Intent(this, AllSettlementActivity.class);
            startActivity(jobs);
        });

        //Havi elszámolás
        month_button.setOnClickListener(v -> {
            Intent settlements = new Intent(this, AdminMonthSettlementActivity.class);
            startActivity(settlements);
        });
    }
}