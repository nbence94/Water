package nb.app.waterdelivery.admin;

import static java.sql.Types.NULL;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.AllCustomersAdapter;
import nb.app.waterdelivery.adapters.AllUsersAdapter;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Users;

public class AdminAllCustomersActivity extends AppCompatActivity {

    DatabaseHelper dh;

    Toolbar toolbar;
    FloatingActionButton new_customer_button;
    RecyclerView recycler;

    ArrayList<Customers> data_list;
    AllCustomersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_customers);

        dh = new DatabaseHelper(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_customers_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Felhasználók megjelenítése
        recycler = findViewById(R.id.admin_customers_recycler_gui);

        data_list = new ArrayList<>();
        dh.getCustomersData("SELECT * FROM " + dh.CUSTOMERS + ";", data_list);
        adapter = new AllCustomersAdapter(this, this, data_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        //Új megrendelő felvitele
        new_customer_button = findViewById(R.id.admin_customers_add_button_gui);

        new_customer_button.setOnClickListener(v -> {
            finish();
            Intent new_customer = new Intent(AdminAllCustomersActivity.this, AdminNewCustomerActivity.class);
            startActivity(new_customer);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    @Override
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
    }
}