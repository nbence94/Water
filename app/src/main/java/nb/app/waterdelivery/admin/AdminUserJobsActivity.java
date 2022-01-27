package nb.app.waterdelivery.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.AllSettlementsAdapter;
import nb.app.waterdelivery.adapters.AllUsersAdapter;
import nb.app.waterdelivery.adapters.UserJobsAdapter;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Users;

public class AdminUserJobsActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AdminUserJobsActivity";

    DatabaseHelper dh;
    RecyclerView recycler;
    Toolbar toolbar;
    UserJobsAdapter adapter;
    SaveLocalDatas sld;
    ArrayList<Jobs> jobs_list;
    ArrayList<String> months_list;

    public int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_jobs);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_user_jobs_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Adatok átvétele
        getIntentData();

        //Felhasználók megjelenítése
        recycler = findViewById(R.id.admin_user_jobs_recycler_gui);
        //jobs_list = new ArrayList<>();
        months_list = new ArrayList<>();
        //getDatas("SELECT * FROM " + dh.JOBS + " WHERE UserID = " + user_id + ";");
        showMonthsElements();
        /*adapter = new UserJobsAdapter(this, this,months_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);*/

    }

    public void showMonthsElements() {
        months_list.clear();
        loadMonths();
        adapter = new UserJobsAdapter(this,  this, months_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    private void getIntentData() {
        if(getIntent().hasExtra("user_id")) {
            user_id = getIntent().getIntExtra("user_id", 0);
        } else {
            user_id = sld.loadUserID();
        }
    }

    private void getDatas(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, created, finish;
                int id, income, userid;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.JOBS_ID_INDEX));
                    created = rs.getString(dh.JOBS_CREATED_INDEX);
                    finish = rs.getString(dh.JOBS_FINISH_INDEX);
                    name = rs.getString(dh.JOBS_NAME_INDEX);
                    income = Integer.parseInt(rs.getString(dh.JOBS_INCOME_INDEX));
                    userid = Integer.parseInt(rs.getString(dh.JOBS_USERID_INDEX));

                    jobs_list.add(new Jobs(id, created, finish, name, income, userid));
                }
            }
            Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres. (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }*/

    public void loadMonths() {

        Connection con = dh.connectionClass(this);
        String select = "SELECT YEAR(Created) AS year, MONTH(Created) As month FROM " + dh.SETTLEMENT + " GROUP BY year, month ORDER BY year DESC;";

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String value;

                while(rs.next()) {
                    value = rs.getString(1) + ".";
                    value += getMonthsName(rs.getString(2));
                    months_list.add(value);
                }
            }
            Log.i(LOG_TITLE, "SIKERES lekérdezés (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "SIKERTELEN lekérdezé (" + select + ")");
        }
    }

    public String getMonthsName(String num_of_month) {
        switch (num_of_month) {
            case "1": return "Január";
            case "2": return "Február";
            case "3": return "Március";
            case "4": return "Április";
            case "5": return "Május";
            case "6": return "Június";
            case "7": return "Július";
            case "8": return "Augusztus";
            case "9": return "Szeptember";
            case "10": return "Október";
            case "11": return "November";
            case "12": return "December";
        }
        return "0";
    }
}