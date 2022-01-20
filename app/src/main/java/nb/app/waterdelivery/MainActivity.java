package nb.app.waterdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.adapters.CurrentJobAdapter;
import nb.app.waterdelivery.adapters.MyJobsAdapter;
import nb.app.waterdelivery.admin.AdminAllCustomersActivity;
import nb.app.waterdelivery.admin.AdminAllUsersActivity;
import nb.app.waterdelivery.customers.MyCustomersActivity;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.jobs.MyJobsActivity;
import nb.app.waterdelivery.login.LoginScreenActivity;
import nb.app.waterdelivery.users.MyProfileActivity;
import nb.app.waterdelivery.waters.WatersActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseHelper dh;
    SaveLocalDatas sld;
    Menu menu;

    //Navigációs menü-elemek
    DrawerLayout drawer_layout;
    NavigationView navigation_view;
    Toolbar toolbar;

    ArrayList<Jobs> job_list;
    CurrentJobAdapter adapter;
    RecyclerView recycler;

    FloatingActionButton finish_jobs_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Osztályok
        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //GUI elemek
        drawer_layout = findViewById(R.id.drawer_layout_gui);
        navigation_view = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.main_toolbar_gui);
        menu = navigation_view.getMenu();
        finish_jobs_button = findViewById(R.id.main_check_button_gui);

        //Saját Toolbar
        setSupportActionBar(toolbar);

        //Navigációs menü
        navigation_view.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        navigation_view.setNavigationItemSelectedListener(this);

        //Login utáni szerepkörök
        if(sld.loadUserRoleID() == dh.ADMIN_ROLE) {
            menu.findItem(R.id.nav_users).setVisible(true);
        } else menu.findItem(R.id.nav_users).setVisible(false);

        if(sld.loadUserRoleID() == dh.ROGZITO_ROLE || sld.loadUserRoleID() == dh.ADMIN_ROLE) {
            menu.findItem(R.id.nav_customers).setVisible(true);
        } else {
            menu.findItem(R.id.nav_customers).setVisible(false);
            menu.findItem(R.id.nav_settlements).setVisible(false);
        }

        job_list = new ArrayList<>();
        recycler = findViewById(R.id.main_activity_recycler_gui);
        showElements();

        finish_jobs_button.setOnClickListener(v -> {
            for(int i = 0; i < job_list.size(); i++) {
                if(job_list.get(i).getFinish() == null) {
                    Toast.makeText(this, "Nem végrehajtható, ameddig van lezáratlan munka!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String created = sdf.format(new Date());

            SimpleDateFormat sdf_ = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
            String name_date = sdf_.format(new Date());

            String settlement_name = name_date + "-" + sld.loadUserName();

            if(!dh.insert(new String[] {settlement_name, created, String.valueOf(sld.loadUserID())}, new String[] {"Name, Created, UserID"}, dh.SETTLEMENT)) {
                Toast.makeText(this, "Munkák leadása sikertelen", Toast.LENGTH_SHORT).show();
                return;
            }

            int new_settlement_id = dh.getNewID(dh.SETTLEMENT);

            for(int i = 0; i < job_list.size(); i++) {
                if(!dh.insert(new String[] {String.valueOf(new_settlement_id), String.valueOf(job_list.get(i).getId())}, new String[] {"SettlementID", "JobID"}, dh.JIS)) {
                    dh.delete(dh.SETTLEMENT, "SettlementID = " + new_settlement_id);
                    dh.delete(dh.SETTLEMENT, "ID = " + new_settlement_id);
                    break;
                }
            }

            job_list.clear();
            showElements();

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.nav_job:
                Intent my_jobs = new Intent(MainActivity.this, MyJobsActivity.class);
                startActivity(my_jobs);
                break;

            case R.id.nav_my_customers:
                Intent my_customers = new Intent(MainActivity.this, MyCustomersActivity.class);
                startActivity(my_customers);
                break;

            case R.id.nav_waters:
                Intent waters = new Intent(MainActivity.this, WatersActivity.class);
                startActivity(waters);
                break;

            case R.id.nav_profile:
                Intent profile = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.nav_logout:
                sld.saveStayLoggedStatus(false);
                finish();
                Intent logout = new Intent(MainActivity.this, LoginScreenActivity.class);
                startActivity(logout);
                break;

            case R.id.nav_my_settlements:
                Toast.makeText(this, "Átvisz majd a leadott elszámolásokhoz", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settlements:
                Toast.makeText(this, "Átvisz majd az elszámolásokhoz", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_users:
                Intent users = new Intent(MainActivity.this, AdminAllUsersActivity.class);
                startActivity(users);
                break;

            case R.id.nav_customers:
                Intent customers = new Intent(MainActivity.this, AdminAllCustomersActivity.class);
                startActivity(customers);
                break;
        }


        return true;
    }

    public void showElements() {
        job_list.clear();
        dh.getJobsData("SELECT * FROM " + dh.JOBS + " WHERE UserID = " + sld.loadUserID() + " AND ID NOT IN ( SELECT JobID FROM " + dh.JIS + ");", job_list);
        adapter = new CurrentJobAdapter(this,  this, job_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }
}