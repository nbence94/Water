package nb.app.waterdelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.UserCustomersAdapter;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;

public class AdminUserCustomersActivity extends AppCompatActivity implements OnDialogChoice {

    private final String LOG_TITLE = "AdminUserCustomersActivity";

    DatabaseHelper dh;
    MyAlertDialog mad;

    RecyclerView recycler;
    Toolbar toolbar;
    UserCustomersAdapter adapter;
    ArrayList<Customers> user_customers_list;
    FloatingActionButton add_customer;

    int user_id;

    ArrayList<Customers> choseable_customers_list;
    String[] customers;
    boolean[] chosen_customers;
    boolean[] tmp_chosen_customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_customers);

        dh = new DatabaseHelper(this, this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_user_customers_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Adatok átvétele
        getIntentData();

        //Felhasználók megjelenítése
        recycler = findViewById(R.id.admin_user_customers_recycler_gui);
        user_customers_list = new ArrayList<>();
        dh.getCustomersData("SELECT * FROM " + dh.CUSTOMERS + " WHERE UserID = " + user_id + ";", user_customers_list);
        showElements();

        //Megrendelő választása
        add_customer = findViewById(R.id.admin_user_customers_add_gui);
        choseable_customers_list = new ArrayList<>();
        getDatas("SELECT * FROM " + dh.CUSTOMERS + " WHERE UserID < 1 OR UserID IS NULL OR UserID = " + user_id + ";", choseable_customers_list);

        customers = new String[choseable_customers_list.size()];
        chosen_customers = new boolean[choseable_customers_list.size()];
        tmp_chosen_customers = new boolean[choseable_customers_list.size()];

        for(int i = 0; i < choseable_customers_list.size(); i++) {
            customers[i] = choseable_customers_list.get(i).getFullname();
            chosen_customers[i] = choseable_customers_list.get(i).getUserid() > 0;
        }

        add_customer.setOnClickListener(v -> {
            mad.AlertMultiSelectDialog("Válassz megrendelőket", customers, chosen_customers, tmp_chosen_customers, "Rendben", "Mégse", null, 0, this);

        });

    }

    private void showElements() {
        adapter = new UserCustomersAdapter(this, user_customers_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void getIntentData() {
        if(getIntent().hasExtra("user_id")) {
            user_id = getIntent().getIntExtra("user_id", 0);
        }
    }

    private void getDatas(String select, ArrayList<Customers> list) {
        //Connection con = dh.connectionClass(this);
        Connection con = null;
        try {
            con = dh.connectionClass(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String fullname, created, city, address, email, phone, phoneplus;
                int id, userid, water_week, bill;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.CUSTOMERS_ID_INDEX));
                    created = rs.getString(dh.CUSTOMERS_CREATED_INDEX);
                    fullname = rs.getString(dh.CUSTOMERS_NAME_INDEX);
                    city = rs.getString(dh.CUSTOMERS_CITY_INDEX);
                    address = rs.getString(dh.CUSTOMERS_ADDRESS_INDEX);
                    email = rs.getString(dh.CUSTOMERS_EMAIL_INDEX);
                    phone = rs.getString(dh.CUSTOMERS_PHONE_INDEX);
                    phoneplus = rs.getString(dh.CUSTOMERS_PHONEPLUS_INDEX);

                    water_week = Integer.parseInt(rs.getString(dh.CUSTOMERS_WATERWEEK_INDEX));
                    bill = Integer.parseInt(rs.getString(dh.CUSTOMERS_BILL_INDEX));
                    userid = (rs.getString(dh.CUSTOMERS_USERID_INDEX) != null) ? Integer.parseInt(rs.getString(dh.CUSTOMERS_USERID_INDEX)) : -1;

                    list.add(new Customers(id, created, fullname, city, address, email, phone, phoneplus, water_week, bill, userid));
                }
            }
            Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres. (" + select + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
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

    @Override
    public void OnPositiveClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position) {
        String update_sql;
        for(int i = 0; i < chosen_customers.length; i++) {
            if(chosen_customers[i]) {
                update_sql = "UPDATE Customers SET UserID = " + user_id + " WHERE ID = " + choseable_customers_list.get(i).getId();
            } else {
                update_sql = "UPDATE Customers SET UserID = CAST(NULL As int) WHERE ID = " + choseable_customers_list.get(i).getId();
            }

            if(!dh.sql(update_sql)) {
                Toast.makeText(this, "Megrendelő(k) csatolása sikertelen.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        recreate();
    }

    @Override
    public void OnNegativeClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_customers.length; i++) {
            chosen_customers[i] = tmp_chosen_customers[i];
        }
    }
}