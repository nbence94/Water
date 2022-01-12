package nb.app.waterdelivery.waters;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.AllWatersAdapter;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Users;
import nb.app.waterdelivery.data.Waters;

public class WatersActivity extends AppCompatActivity {

    private final String LOG_TITLE = "WatersActivity";

    DatabaseHelper dh;
    SaveLocalDatas sld;

    Toolbar toolbar;
    FloatingActionButton new_water_button;
    RecyclerView recycler;
    ArrayList<Waters> waters_list;
    AllWatersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waters);

        //Osztályok
        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //Vissza gomb
        toolbar = findViewById(R.id.waters_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek

        //Recycler
        recycler = findViewById(R.id.waters_recycler_gui);
        waters_list = new ArrayList<>();
        getDatas("SELECT * FROM " + dh.WATERS + ";");
        adapter = new AllWatersAdapter(this, this, waters_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);


        //Új víz felvitele
        new_water_button = findViewById(R.id.water_add_new_water_gui);

        if(sld.loadUserRoleID() != dh.ADMIN_ROLE) {
            new_water_button.setVisibility(View.GONE);
        }


        new_water_button.setOnClickListener(v -> {
            finish();
            Intent new_water = new Intent(WatersActivity.this, AddWaterActivity.class);
            startActivity(new_water);
        });
    }

    private void getDatas(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name;
                int id, cost;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.WATERS_ID_INDEX));
                    name = rs.getString(dh.WATERS_NAME_INDEX);
                    cost = Integer.parseInt(rs.getString(dh.WATERS_COST_INDEX));

                    waters_list.add(new Waters(id, name, cost));
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
}