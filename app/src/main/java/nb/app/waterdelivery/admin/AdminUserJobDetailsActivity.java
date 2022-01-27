package nb.app.waterdelivery.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.MyJobsAdapter;
import nb.app.waterdelivery.adapters.UserJobsDetailsAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class AdminUserJobDetailsActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AdminUserJobDetailsAdapter";

    Toolbar toolbar;
    RecyclerView recycler;

    UserJobsDetailsAdapter adapter;
    DatabaseHelper dh;
    SaveLocalDatas sld;

    ArrayList<Jobs> job_list;
    int settlement_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_job_details);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_user_jobs_details_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler = findViewById(R.id.admin_user_jobs_details_recycler_gui);
        job_list = new ArrayList<>();

        getIntentData();
        showElements();
    }

    public void showElements() {
        job_list.clear();
        dh.getJobsData("SELECT * FROM " + dh.JOBS + " WHERE ID IN ( SELECT JobID FROM " + dh.JIS + " WHERE settlementid = " + settlement_id + ");", job_list);
        adapter = new UserJobsDetailsAdapter(this,  this, job_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    private void getIntentData() {
        if(getIntent().hasExtra("settlementid")) {
            settlement_id = getIntent().getIntExtra("settlementid", -1);
            Log.i(LOG_TITLE, "Értékek átemelve. (SettlementID: " + settlement_id );
        } else {
            Log.e(LOG_TITLE, "Az értékek átemelése sikertelen.");
        }
    }
}