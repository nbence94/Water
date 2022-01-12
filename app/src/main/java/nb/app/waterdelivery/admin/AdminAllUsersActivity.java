package nb.app.waterdelivery.admin;

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
import nb.app.waterdelivery.adapters.AllUsersAdapter;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Users;

public class AdminAllUsersActivity extends AppCompatActivity {

    DatabaseHelper dh;
    private final String LOG_TITLE = "AdminAllUsersActivity";

    Toolbar toolbar;
    FloatingActionButton new_user_button;

    AllUsersAdapter adapter;
    RecyclerView recycler;
    ArrayList<Users> users_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        dh = new DatabaseHelper(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_users_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Új felhasználó hozzáadás
        new_user_button = findViewById(R.id.admin_users_add_button_gui);

        new_user_button.setOnClickListener(v -> {
            finish();
            Intent new_user = new Intent(AdminAllUsersActivity.this, AdminNewUserActivity.class);
            startActivity(new_user);
        });

        //Felhasználók megjelenítése
        recycler = findViewById(R.id.admin_users_recycler_gui);
        users_list = new ArrayList<>();
        getDatas("SELECT * FROM " + dh.USERS + ";");
        adapter = new AllUsersAdapter(this, this, users_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    private void getDatas(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, email, password, created, phone;
                int id, role_id, status, numofjobs;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.USERS_ID_INDEX));
                    created = rs.getString(dh.USERS_CREATED_INDEX);
                    name = rs.getString(dh.USERS_NAME_INDEX);
                    email = rs.getString(dh.USERS_EMAIL_INDEX);
                    phone = rs.getString(dh.USERS_PHONE_INDEX);
                    password = rs.getString(dh.USERS_PASSWORD_INDEX);
                    numofjobs = Integer.parseInt(rs.getString(dh.USERS_JOBS_INDEX));
                    role_id = Integer.parseInt(rs.getString(dh.USERS_ROLEID_INDEX));
                    status = Integer.parseInt(rs.getString(dh.USERS_STATUS_INDEX));

                    users_list.add(new Users(id, created, name, email, password, phone, numofjobs, role_id, status));
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