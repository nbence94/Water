package nb.app.waterdelivery.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.MyJobsAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.myWarningDialogChoice;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class MyJobsActivity extends AppCompatActivity implements myWarningDialogChoice {

    Toolbar toolbar;
    FloatingActionButton new_draft_button;
    RecyclerView recycler;

    MyJobsAdapter adapter;
    DatabaseHelper dh;
    SaveLocalDatas sld;
    MyAlertDialog mad;

    ArrayList<Jobs> job_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.my_jobs_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Tervezet készítés (Munka létrehozás)
        new_draft_button = findViewById(R.id.my_jobs_add_button_gui);

        new_draft_button.setOnClickListener(v -> {
            if(dh.SZALLITO_ROLE != sld.loadUserRoleID()) {
                mad.myWarningDialog("Válassz az alábbiak közül", "Milyen tervezetet készítenél?", "Heti", "Egyéb", null, 0, this);
            } else {
                openCreate();
            }
        });

        recycler = findViewById(R.id.my_jobs_recycler_gui);
        job_list = new ArrayList<>();
        showElements();

        clearTable();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    public void showElements() {
        job_list.clear();
        dh.getJobsData("SELECT * FROM " + dh.JOBS + " WHERE UserID = " + sld.loadUserID() + " AND ID NOT IN ( SELECT JobID FROM " + dh.JIS + ");", job_list);
        adapter = new MyJobsAdapter(this,  this, job_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    @Override
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        openCreate();
    }

    @Override
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (0 == dh.getExactInt("SELECT COUNT(*) FROM Customers c WHERE c.UserID = " + sld.loadUserID() + "  AND c.ID NOT IN " +
                "( SELECT c.ID FROM Customers c, customerinjob cij, jobs j WHERE c.ID = cij.CustomerID AND j.ID = cij.JobID AND j.Finish IS NULL )")) {
            Toast.makeText(this, "Minden megrendelő szállításhoz van már rendelve!", Toast.LENGTH_LONG).show();
            return;
        }

        finish();
        Intent new_draft = new Intent(MyJobsActivity.this, CreateJobActivity.class);
        startActivity(new_draft);
    }

    private void openCreate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day == 1 || day == 7) {
            if (0 == dh.getExactInt("SELECT COUNT(*) FROM Customers c WHERE ((DATEDIFF(CURDATE(), c.Created + (8 - DAYOFWEEK(c.Created)))) DIV 7 MOD c.WaterWeeks) = 0 AND c.UserID = "  + sld.loadUserID()+ "  AND c.ID NOT IN " +
                    "( SELECT c.ID FROM Customers c, customerinjob cij, jobs j WHERE c.ID = cij.CustomerID AND j.ID = cij.JobID AND j.Finish IS NULL )")) {
                Toast.makeText(this, "Minden megrendelő szállításhoz van már rendelve!", Toast.LENGTH_LONG).show();
                return;
            }

            finish();
            Intent new_draft = new Intent(MyJobsActivity.this, CreateJobActivity.class);
            new_draft.putExtra("weekend", 1);
            startActivity(new_draft);
        } else {
            Toast.makeText(this, "Nincs hétvége!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearTable() {
        if(sld.loadCurrentJobID() > 0) {
            if(!dh.delete(dh.EDITDRAFT, "JobID = " + sld.loadCurrentJobID())) {
                Log.e("MyJobsActivity","Az vázlat adatok törlése sikertelen. (" + dh.EDITDRAFT + ")");
            }
        }
    }
}