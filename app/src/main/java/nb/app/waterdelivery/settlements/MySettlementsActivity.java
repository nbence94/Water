package nb.app.waterdelivery.settlements;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.MySettlementsAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;

public class MySettlementsActivity extends AppCompatActivity {

    private final String LOG_TITLE = "MySettlementsActivity";

    DatabaseHelper dh;
    SaveLocalDatas sld;
    MyAlertDialog mad;

    Toolbar toolbar;
    RecyclerView recycler;
    MySettlementsAdapter adapter;

    ArrayList<Settlement> settlement_list;
    ArrayList<String> months_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settlements);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.my_settlements_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        recycler = findViewById(R.id.my_settlements_recycler_gui);
        settlement_list = new ArrayList<>();
        months_list = new ArrayList<>();

        showMonthsElements();
    }

    public void showMonthsElements() {
        months_list.clear();
        loadMonths();
        adapter = new MySettlementsAdapter(this,  this, months_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    public void loadMonths() {

        Connection con = dh.connectionClass(this);
        //String select = "SELECT YEAR(Created) AS year, MONTH(Created) As month FROM " + dh.SETTLEMENT + " WHERE UserID = " + sld.loadUserID() + " GROUP BY year, month ORDER BY year DESC;";
        String select = "call getMySettlements(" + sld.loadUserID() + ")";

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String value;

                while(rs.next()) {
                    value = rs.getString(1) + ".";
                    value += rs.getString(2) + ". - Leadott munkák";
                    months_list.add(value);
                }
            }
            Log.i(LOG_TITLE, "SIKERES lekérdezés (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "SIKERTELEN lekérdezé (" + select + ")");
        }
    }
}