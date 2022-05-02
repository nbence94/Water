package nb.app.waterdelivery.jobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Objects;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.CurrentChosenJobAdapter;
import nb.app.waterdelivery.adapters.MyJobsAdapter;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.CustomersInJob;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JobAndWaters;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.helper.NumberSplit;

public class JobVisitActivity extends AppCompatActivity {

    private final String LOG_TITLE = "JobVisitActivity";

    DatabaseHelper dh;
    SaveLocalDatas sld;

    RecyclerView recycler;
    Toolbar toolbar;
    CurrentChosenJobAdapter adapter;

    public ArrayList<CustomersInJob> customers_in_jobs_list;
    ArrayList<JobAndWaters> waters_in_jobs_list;
    ArrayList<Waters> water_details_list;
    ArrayList<Customers> customers_detail_list;
    ArrayList<String> customer_income;

    //TODO Ez
    public ArrayList<Boolean> expanded_list;

    int job_id;
    String job_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_visit);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        recycler = findViewById(R.id.current_job_recycler_gui);
        toolbar = findViewById(R.id.current_job_toolbar_gui);

        //Vissza gomb
        toolbar = findViewById(R.id.current_job_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Listák inicializálása
        customers_in_jobs_list = new ArrayList<>();
        waters_in_jobs_list = new ArrayList<>();
        water_details_list = new ArrayList<>();
        customers_detail_list = new ArrayList<>();
        customer_income = new ArrayList<>();

        getIntentData();//Job_ID

        //Listák feltöltése
        dh.getCIJData("SELECT * FROM " + dh.CIJ + " WHERE JobID=" + job_id + ";", customers_in_jobs_list);
        dh.getJAWData("SELECT * FROM " + dh.JAW + " WHERE JobID=" + job_id + ";", waters_in_jobs_list);
        dh.getWatersData("SELECT * FROM " + dh.WATERS, water_details_list);
        dh.getCustomersData("SELECT * FROM " + dh.CUSTOMERS + " WHERE UserID=" + sld.loadUserID() + ";", customers_detail_list);
        loadIncomes();

        //TODO Ez
        this.expanded_list = new ArrayList<>();
        for(int i = 0; i < customers_in_jobs_list.size(); i++) {
            expanded_list.add(false);
        }


        showElements();
    }

    public void showElements() {
        adapter = new CurrentChosenJobAdapter(this,  this, customers_in_jobs_list, waters_in_jobs_list, water_details_list, customers_detail_list, customer_income, expanded_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    private void getIntentData() {
        if(getIntent().hasExtra("job_id")) {
            job_id = getIntent().getIntExtra("job_id", -1);
            job_name = getIntent().getStringExtra("job_name");
            Log.i(LOG_TITLE, "Értékek átemelve. (JobID: " + job_id + ", JobName: "  + job_name + ")");
        } else {
            Log.e(LOG_TITLE, "Az értékek átemelése sikertelen.");
        }
    }

    public void loadIncomes() {
        int id, customer, income;
        customer_income.clear();
        for(int i = 0; i < customers_in_jobs_list.size(); i++) {
            id = customers_in_jobs_list.get(i).getJobid();
            customer = customers_in_jobs_list.get(i).getCustomerid();
            income = dh.getExactInt("SELECT SUM(w.Price * jaw.WaterAmount) FROM " + dh.WATERS + " w, " + dh.JAW + " jaw WHERE w.ID = jaw.WaterID AND jaw.JobID = " + id + " AND CustomerID=" + customer + ";");
            customer_income.add(String.valueOf(income));
        }
    }

    public void reload() {
        adapter.reload();
    }
}