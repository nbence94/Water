package nb.app.waterdelivery.jobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import nb.app.waterdelivery.adapters.ChosenCustomerWatersAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.alertdialog.OnDialogTextChange;
import nb.app.waterdelivery.data.CustomerAndWaters;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Draft;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.helper.NumberSplit;

public class CreateJobActivity extends AppCompatActivity implements OnDialogChoice, OnDialogTextChange {

    private final String LOG_TITLE = "CreateJobActivity";

    DatabaseHelper dh;
    MyAlertDialog mad;
    SaveLocalDatas sld;

    Toolbar toolbar;
    FloatingActionButton save_button, add_customer_button;
    RecyclerView recycler;
    TextView income_text;

    //Listák
    public ArrayList<Customers> all_customers_list;  //Ez, a felhasználó számára összes választható megrendelő lista
    public ArrayList<Customers> chosen_customers_list;//Lista a kiválasztott megrendelőkről
    public ArrayList<CustomerAndWaters> all_caw_list; //Ez default értéket ad arra, hogy milyen értékek kerüljenek a Draft-ba
    ArrayList<Waters> all_water_list; //Eltárol minden vizet. Kell az árak kiírásához, stb.
    ArrayList<Draft> draft_list; //Ez a biztonsági mentéshez kell. Ez alapján fog létrejobbni a Jobs tábla, stb.

    //Megrendelők kiválasztása és megjelenítése
    ChosenCustomersAdapter adapter;
    String[] customers_name_to_show;
    public boolean[] chosen_customers;
    boolean[] tmp_chosen_customers;

    int global_income;
    int weekend = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        dh = new DatabaseHelper(this, this);
        mad = new MyAlertDialog(this, this);
        sld = new SaveLocalDatas(this);

        //Vissza gomb
        toolbar = findViewById(R.id.create_job_toolbar_gui);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek
        save_button = findViewById(R.id.create_draft_save_gui);
        add_customer_button = findViewById(R.id.draft_add_customer_gui);
        income_text = findViewById(R.id.draft_expected_income_gui);
        recycler = findViewById(R.id.create_job_recycler_gui);

        //Listák inicializálása
        all_customers_list = new ArrayList<>(); //Ezekből lehet válogatni
        chosen_customers_list = new ArrayList<>(); //Ezek lettek kiválasztva a tervezethez
        all_caw_list = new ArrayList<>();
        all_water_list = new ArrayList<>();
        draft_list = new ArrayList<>();

        //Hétvége vagy hétköznap van
        getIntentData();

        //Listák feltöltése

        //Választható megrendelők
        String query = "";

        //A 8 mindenképp kell, mert vasárnaphoz mérten akarom számolni, akár szombaton, akár vasárnap tervez
        if(weekend == 1) {
            query = "SELECT * FROM Customers c " +
                    "WHERE ((DATEDIFF(CURDATE(), c.Created + (8 - DAYOFWEEK(c.Created)))) DIV 7 MOD c.WaterWeeks) = 0 " +
                    "AND c.UserID = " + sld.loadUserID() + "  " +
                    "AND c.ID NOT IN " +
                    "( SELECT c.ID " +
                    "FROM Customers c, customerinjob cij, jobs j " +
                    "WHERE c.ID = cij.CustomerID AND j.ID = cij.JobID " +
                    "AND j.Finish IS NULL )";
        }
        else if(weekend == 0) {
            query = "SELECT * FROM Customers c WHERE c.UserID = " + sld.loadUserID() + "  AND c.ID NOT IN " +
                    "( SELECT c.ID FROM Customers c, customerinjob cij, jobs j WHERE c.ID = cij.CustomerID AND j.ID = cij.JobID AND j.Finish IS NULL )";
        }


        dh.getCustomersData(query, all_customers_list);

        //Megrendelőkhöz tartozó vizek
        dh.getCAWData(all_caw_list, "");

        //Víz adatok
        dh.getWatersData("SELECT * FROM " + dh.WATERS, all_water_list);

        //AlertDialog beállítások
        initializeArrays(all_customers_list.size());
        setShowableNames(all_customers_list);

        //Ha volt már piszkozat, akkor töltsük be
        loadDraftElements();

        //Végösszeg kiszámítása
        calculateGlobalIncome();

        //Hozzáadás gomb (AlertDialog megnyitás)
        add_customer_button.setOnClickListener(v -> mad.AlertMultiSelectDialog("Válassz megrendelőket", customers_name_to_show, chosen_customers, tmp_chosen_customers, "Rendben", "Mégse", null, 0, this));

        //Mentés
        save_button.setOnClickListener(v -> {

        if(chosen_customers_list.size() == 0) {
            Toast.makeText(this, "Válassz ki legalább 1 megrendelőt!", Toast.LENGTH_SHORT).show();
            return;
        }
            mad.AlertInputDialog("Nevezd el a munkát", "", "Rendben", 0, null,0, this);
        });
    }

    public void loadDraftElements() {
        try {
            if (sld.loadDraftStatus()) {
            //TODO: Majd megcsinálni, hogy ha később a ChosenCustomersAdapter-ből meghívom a cuccot, akkor ne írja ki azt, hogy piszkozat betöltve
                //chosen customer list feltöltése
                dh.getCustomersData("SELECT DISTINCT c.ID, c.Created, c.Fullname," +
                        " c.City, c.Address, c.Email, c.Phone, c.PhonePlus, c.Comment, c.WaterWeeks, c.Bill, c.UserID " +
                        "FROM " + dh.CUSTOMERS + " c, " + dh.DRAFT + " d  " +
                        "WHERE c.ID = d.CustomerID AND d.UserID=" + sld.loadUserID() + ";", chosen_customers_list);
                if(chosen_customers_list.size() == 0) {
                    return;
                }
                showCustomersInRecyclerView();

                //Kipipálni azokat az elemeket, amik ki voltak jelölve
                for (int i = 0; i < all_customers_list.size(); i++) {
                    for (int j = 0; j < chosen_customers_list.size(); j++) {
                        if (all_customers_list.get(i).getId() == chosen_customers_list.get(j).getId()) {
                            chosen_customers[i] = true;
                        }
                    }
                }
                Log.i(LOG_TITLE, "Korábbi piszkozat betöltve.");
                Toast.makeText(this, "Korábbi adatok betöltése", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Piszkozat betöltése sikertelen", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TITLE, "Sikertelen piszkozat betöltés.");
            e.printStackTrace();
        }
    }

    public void ChosenCustomersListLoad() {
        chosen_customers_list.clear();
        String fullname, created, city, address, email, phone, phoneplus, comment;
        int customer_id, userid, water_week, bill;

        for(int i = 0; i < chosen_customers.length; i++) {
            if(chosen_customers[i]) {

                customer_id = all_customers_list.get(i).getId();
                fullname = all_customers_list.get(i).getFullname();
                created = all_customers_list.get(i).getCreated();
                city = all_customers_list.get(i).getCity();
                address = all_customers_list.get(i).getAddress();
                email = all_customers_list.get(i).getEmail();
                phone = all_customers_list.get(i).getPhone_one();
                phoneplus = all_customers_list.get(i).getPhone_two();
                userid = all_customers_list.get(i).getUserid();
                water_week = all_customers_list.get(i).getWater_weeks();
                bill = all_customers_list.get(i).getBill();
                comment = all_customers_list.get(i).getComment();

                chosen_customers_list.add(new Customers(customer_id, created, fullname, city, address, email, phone, phoneplus, water_week, bill, comment, userid));

                //Customerhez tartozó víz elmentve
                for(int j = 0; j < all_caw_list.size(); j++) {
                    if(all_caw_list.get(j).getCustomerid() == customer_id) {
                        if(!dh.sql("INSERT INTO " + dh.DRAFT + " (UserID, CustomerID, WaterID) VALUES (" + sld.loadUserID() + ",  " + customer_id + ", " + all_caw_list.get(j).getWaterid() + ");"))
                            return;
                    }
                }
            }
        }
    }

    @Override
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position) {


        //Eddigi ürítés
        sld.saveDraftStatus(false);
        if(!dh.sql("DELETE FROM " + dh.DRAFT + " WHERE UserID = " + sld.loadUserID())) return;

        //A chosen_customers listába bekerültek a választott megrendelők. Szóval azok alapján töltődik újra a draft
        ChosenCustomersListLoad();

        int draft_user, draft_customer, draft_water;
        for(int i = 0; i < draft_list.size(); i++) {
            if(draft_list.get(i).getWater_amount() > 1) {
                draft_user = sld.loadUserID();
                draft_customer = draft_list.get(i).getCustomerid();
                draft_water = draft_list.get(i).getWaterid();

                dh.sql("UPDATE " + dh.DRAFT + " SET WaterAmount = " + draft_list.get(i).getWater_amount()
                        + " WHERE UserID =" + draft_user + " AND CustomerID =" + draft_customer + " AND WaterID =" + draft_water);
            }
        }
        if(chosen_customers_list.size() > 0) sld.saveDraftStatus(true);

        showCustomersInRecyclerView();
        calculateGlobalIncome();
    }

    @Override
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_customers.length; i++) {
            chosen_customers[i] = tmp_chosen_customers[i];
        }
    }

    public void showCustomersInRecyclerView() {
        draft_list.clear();
        dh.getDraftData("SELECT * FROM " + dh.DRAFT + " WHERE UserID = " + sld.loadUserID() + ";", draft_list);
        adapter = new ChosenCustomersAdapter(this, this, chosen_customers_list, all_water_list, draft_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    public void calculateGlobalIncome() {
        String result;
        if(draft_list.size() > 0) {
            int total_cost = dh.getExactInt("SELECT SUM(w.Price * d.WaterAmount) FROM Waters w, Draft d WHERE w.ID = d.WaterID AND d.UserID = " + sld.loadUserID() + ";");
            result = NumberSplit.splitNum(total_cost) + " Ft";
        } else {
            result = "-";
        }
        income_text.setText(result);
    }

    public void initializeArrays(int size) {
        customers_name_to_show = new String[size];
        chosen_customers = new boolean[size];
        tmp_chosen_customers = new boolean[size];
    }

    public void setShowableNames(ArrayList<Customers> list) {
        for(int i = 0; i < list.size(); i++)
            customers_name_to_show[i] = list.get(i).getFullname();
    }

    @Override
    public void onAlertDialogTextChange(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(mad.result_text.equals("")) {
            Toast.makeText(this, "Adj nevet a munkának!", Toast.LENGTH_SHORT).show();
            mad.closeable = false;
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String created_date = sdf.format(new Date());


        if(!dh.sql("INSERT INTO " + dh.JOBS + " (Name, Created, Income, UserID) VALUES ('" + mad.result_text + "', '" + created_date + "'," + global_income + ", " + sld.loadUserID() + ");")) {
            return;
        }
        Log.i(LOG_TITLE, "Munka feltöltve");

        int new_job_id = dh.getExactInt("SELECT ID FROM ( SELECT UserID, MAX(ID) AS ID FROM Jobs Group By UserID ) AS GetID WHERE UserID = " + sld.loadUserID() + "");
        Log.i(LOG_TITLE, "Új azonosító lekérve");

        ArrayList<Integer> identifiers = new ArrayList<>();
        for(int i = 0; i < draft_list.size(); i++) {
            if(draft_list.get(i).getUserid() == sld.loadUserID() && !identifiers.contains(draft_list.get(i).getCustomerid())) {
                if(!dh.sql("INSERT INTO " + dh.CIJ + " (JobID, CustomerID) " +
                        "VALUES (" + new_job_id + ", " + draft_list.get(i).getCustomerid() + ");" )) {
                    if(!dh.sql("DELETE FROM " + dh.CIJ + " WHERE JobID = " + new_job_id + ";")) return;
                    if(!dh.sql("DELETE FROM " + dh.JOBS + " WHERE ID = " + new_job_id + ";")) return;
                    return;
                }
                identifiers.add(draft_list.get(i).getCustomerid());
            }
        }
        Log.i(LOG_TITLE, "A megrendelők feltöltve a Munkához, ");

        for(int i = 0; i < draft_list.size(); i++) {
            if (draft_list.get(i).getUserid() == sld.loadUserID() ) {
                if (!dh.sql("INSERT INTO " + dh.JAW + " (JobID, CustomerID, WaterID, WaterAmount) " +
                        "VALUES (" + new_job_id + ", " + draft_list.get(i).getCustomerid() + ", " + draft_list.get(i).getWaterid() + ", " + draft_list.get(i).getWater_amount() + ");")) {
                    if (!dh.sql("DELETE FROM " + dh.CIJ + " WHERE JobID = " + new_job_id + ";"))
                        return;
                    if (!dh.sql("DELETE FROM " + dh.JAW + " WHERE JobID = " + new_job_id + ";"))
                        return;
                    if (!dh.sql("DELETE FROM " + dh.JOBS + " WHERE ID = " + new_job_id + ";"))
                        return;
                    return;
                }
            }
        }

        if(!dh.sql("DELETE FROM " + dh.DRAFT + " WHERE UserID = " + sld.loadUserID() + ";")) {
            Log.e(LOG_TITLE, "Nem sikerült törölni a vázlatokat");
            return;
        }

        finish();
        setResult(1);
        Intent jobs = new Intent(this, MyJobsActivity.class);
        startActivity(jobs);
        Toast.makeText(this, "Munka létrehozva", Toast.LENGTH_SHORT).show();
        sld.saveDraftStatus(false);
        mad.closeable = true;
    }

    private void getIntentData() {
        if(getIntent().hasExtra("weekend")) {
            weekend = getIntent().getIntExtra("weekend", -1);
        } else {
            Log.e(LOG_TITLE, "Az értékek átemelése sikertelen.");
        }
    }
}