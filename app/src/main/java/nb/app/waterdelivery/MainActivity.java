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

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

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
        } else menu.findItem(R.id.nav_customers).setVisible(false);

        job_list = new ArrayList<>();
        recycler = findViewById(R.id.main_activity_recycler_gui);
        showElements();
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
        dh.getJobsData("SELECT * FROM " + dh.JOBS + " WHERE UserID = " + sld.loadUserID() + ";", job_list);
        adapter = new CurrentJobAdapter(this,  this, job_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }
}