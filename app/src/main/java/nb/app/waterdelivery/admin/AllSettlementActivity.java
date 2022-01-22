package nb.app.waterdelivery.admin;

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
import nb.app.waterdelivery.adapters.AllSettlementsMonthAdapter;
import nb.app.waterdelivery.adapters.MySettlementsAdapter;
import nb.app.waterdelivery.adapters.MySettlementsMonthAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;

public class AllSettlementActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AllSettlementsActivity";

    DatabaseHelper dh;
    SaveLocalDatas sld;
    MyAlertDialog mad;

    Toolbar toolbar;
    RecyclerView recycler;
    AllSettlementsMonthAdapter adapter;

    ArrayList<String> months_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_settlement);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.all_settlements_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        recycler = findViewById(R.id.all_settlements_recycler_gui);
        months_list = new ArrayList<>();

        showMonthsElements();
    }

    public void showMonthsElements() {
        months_list.clear();
        loadMonths();
        adapter = new AllSettlementsMonthAdapter(this,  this, months_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

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