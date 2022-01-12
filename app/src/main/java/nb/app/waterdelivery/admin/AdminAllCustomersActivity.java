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
    private final String LOG_TITLE = "AdminAllCustomersActivity";

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
        getDatas("SELECT * FROM " + dh.CUSTOMERS + ";");
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

    private void getDatas(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, created, city, address, email, phone, phoneplus;
                int id, ww_number, bill, userid;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.CUSTOMERS_ID_INDEX));
                    created = rs.getString(dh.CUSTOMERS_CREATED_INDEX);
                    name = rs.getString(dh.CUSTOMERS_NAME_INDEX);
                    city = rs.getString(dh.CUSTOMERS_CITY_INDEX);
                    address = rs.getString(dh.CUSTOMERS_ADDRESS_INDEX);
                    email = rs.getString(dh.CUSTOMERS_EMAIL_INDEX);
                    phone = rs.getString(dh.CUSTOMERS_PHONE_INDEX);
                    phoneplus = rs.getString(dh.CUSTOMERS_PHONEPLUS_INDEX);
                    ww_number = Integer.parseInt(rs.getString(dh.CUSTOMERS_WATERWEEK_INDEX));
                    bill = Integer.parseInt(rs.getString(dh.CUSTOMERS_BILL_INDEX));
                    userid = (rs.getString(dh.CUSTOMERS_USERID_INDEX) == null) ? -1 : Integer.parseInt(rs.getString(dh.CUSTOMERS_USERID_INDEX));

                    data_list.add(new Customers(id, created, name, city, address, email, phone, phoneplus, ww_number, bill, userid));
                }
            }

            Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres. (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
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