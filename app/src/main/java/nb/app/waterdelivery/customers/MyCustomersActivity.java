package nb.app.waterdelivery.customers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.AllCustomersAdapter;
import nb.app.waterdelivery.adapters.MyCustomersAdapter;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class MyCustomersActivity extends AppCompatActivity {

    DatabaseHelper dh;
    SaveLocalDatas sld;
    private final String LOG_TITLE = "MyCustomersActivity";

    Toolbar toolbar;
    RecyclerView recycler;
    TextView no_customers_text;

    ArrayList<Customers> data_list;
    MyCustomersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_customers);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //Vissza gomb
        toolbar = findViewById(R.id.my_customers_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Felhasználók megjelenítése
        recycler = findViewById(R.id.my_customers_recycler_gui);
        data_list = new ArrayList<>();
        getDatas("SELECT * FROM " + dh.CUSTOMERS + " WHERE UserID = " + sld.loadUserID());
        adapter = new MyCustomersAdapter(this,  this, data_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        //Üzenet
        no_customers_text = findViewById(R.id.my_customers_no_customers_gui);
        if(data_list.size() == 0) {
            no_customers_text.setVisibility(View.VISIBLE);
        }
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
}