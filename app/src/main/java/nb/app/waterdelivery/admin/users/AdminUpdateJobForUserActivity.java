package nb.app.waterdelivery.admin.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.EditJobChosenCustomerAdapter;
import nb.app.waterdelivery.adapters.admin_users.UpdateChosenCustomersListForJobAdapter;
import nb.app.waterdelivery.alertdialog.EditJobOnDialogChoice;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogTextChange;
import nb.app.waterdelivery.data.CustomerAndWaters;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JawDraft;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.jobs.MyJobsActivity;

public class AdminUpdateJobForUserActivity extends AppCompatActivity implements EditJobOnDialogChoice, OnDialogTextChange {

    private final String LOG_TITLE = "AdminUpdateJobForUserActivity";

    DatabaseHelper dh;
    MyAlertDialog mad;
    SaveLocalDatas sld;

    Toolbar toolbar;
    FloatingActionButton save_button, add_customer_button;
    RecyclerView recycler;
    TextView income_text;

    public int job_id;
    String job_name;

    //Listák
    public ArrayList<Customers> all_customers_list;  //Ez, a felhasználó számára összes választható megrendelő lista
    public ArrayList<Customers> chosen_customers_list;//Lista a kiválasztott megrendelőkről
    public ArrayList<CustomerAndWaters> all_caw_list; //Ez default értéket ad arra, hogy milyen értékek kerüljenek a Draft-ba
    ArrayList<Waters> all_water_list; //Eltárol minden vizet. Kell az árak kiírásához, stb.
    ArrayList<JawDraft> draft_list; //Ez a biztonsági mentéshez kell. Ez alapján fog létrejobbni a Jobs tábla, stb.

    //Megrendelők kiválasztása és megjelenítése
    UpdateChosenCustomersListForJobAdapter adapter;
    String[] customers_name_to_show;
    public boolean[] chosen_customers;
    boolean[] tmp_chosen_customers;

    int global_income;

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

        getIntentData();
        sld.saveCurrentJobID(job_id);

        //Listák feltöltése

        //Választható megrendelők
        dh.getCustomersData(
                "SELECT * " +
                        "FROM Customers c " +
                        "WHERE ((DATEDIFF(CURDATE(), c.Created + (8 - DAYOFWEEK(c.Created)))) DIV 7 MOD c.WaterWeeks) = 0 " +
                        "AND c.UserID = " + sld.loadCurrentUserID() +" AND c.ID NOT IN ( SELECT c.ID " +
                        " FROM Customers c, customerinjob cij, jobs j " +
                        " WHERE c.ID = cij.CustomerID AND j.ID = cij.JobID AND j.Finish IS NULL " +
                        ")" +
                        " UNION " +
                        "SELECT * FROM Customers C WHERE c.ID IN (" +
                        " SELECT CustomerID" +
                        " FROM " + dh.CIJ +
                        " WHERE JobID = " + job_id +
                        " );", all_customers_list);

        //Megrendelőkhöz tartozó vizek
        dh.getCAWData(all_caw_list, "");

        //Víz adatok
        dh.getWatersData("SELECT * FROM " + dh.WATERS, all_water_list);

        //AlertDialog beállítások
        initializeArrays(all_customers_list.size());
        setShowableNames(all_customers_list);

        copyJobWatersToDraft();
        //Ha volt már piszkozat, akkor töltsük be
        loadDraftElements();

        for (int i = 0; i < chosen_customers.length; i++) {
            for(int j = 0; j < chosen_customers_list.size(); j++) {
                if (all_customers_list.get(i).getId() == chosen_customers_list.get(j).getId()) {
                    chosen_customers[i] = true;
                }
            }
        }

        //Végösszeg kiszámítása
        calculateGlobalIncome();

        //Hozzáadás gomb (AlertDialog megnyitás)
        add_customer_button.setOnClickListener(v -> mad.editJobMultiSelect("Válassz megrendelőket", customers_name_to_show, chosen_customers, tmp_chosen_customers, "Rendben", "Mégse", null, 0, this));

        //Mentés
        save_button.setOnClickListener(v -> {

            if(chosen_customers_list.size() == 0) {
                Toast.makeText(this, "Válassz ki legalább 1 megrendelőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            mad.AlertInputDialog("Nevezd el a munkát", job_name, "Rendben", 0, null,0, this);
        });
    }

    void copyJobWatersToDraft() {
        if(!dh.sql("DELETE FROM " + dh.EDITDRAFT + " WHERE JobID =" + job_id)) {
            Log.e(LOG_TITLE, "Nem sikerült törölni az adatokat");
            return;
        }
        if(!dh.sql("INSERT INTO " + dh.EDITDRAFT + " SELECT * FROM " + dh.JAW + " WHERE jobID = " + job_id)) {
            Log.e(LOG_TITLE, "Nem sikerült az adatokat átmásolni a JAW-DRAFT-ba");
            return;
        }
    }

    public void loadDraftElements() {
        try {

            //TODO: Majd megcsinálni, hogy ha később a ChosenCustomersAdapter-ből meghívom a cuccot, akkor ne írja ki azt, hogy piszkozat betöltve
            //chosen customer list feltöltése
            dh.getCustomersData("SELECT DISTINCT c.ID, c.Created, c.Fullname," +
                    " c.City, c.Address, c.Email, c.Phone, c.PhonePlus, c.WaterWeeks, c.Bill, c.UserID " +
                    "FROM " + dh.CUSTOMERS + " c, " + dh.EDITDRAFT + " d  " +
                    "WHERE c.ID = d.CustomerID AND d.JobID=" + job_id + ";", chosen_customers_list);

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnPositiveClick(@NonNull EditJobChosenCustomerAdapter.ViewHolder holder, int position) {
        String fullname, created, city, address, email, phone, phoneplus;
        int customer_id, userid, water_week, bill;

        //Eddigi ürítés
        if(!dh.sql("DELETE FROM " + dh.EDITDRAFT + " WHERE JobID = " + job_id + ";")) return;
        chosen_customers_list.clear();
        //A chosen_customers listába bekerültek a választott megrendelők. Szóval azok alapján töltődik újra a draft
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

                chosen_customers_list.add(new Customers(customer_id, created, fullname, city, address, email, phone, phoneplus, water_week, bill, userid));

                //Customerhez tartozó víz elmentve
                for(int j = 0; j < all_caw_list.size(); j++) {
                    if(all_caw_list.get(j).getCustomerid() == customer_id) {
                        if(!dh.sql("INSERT INTO " + dh.EDITDRAFT + " (JobID, CustomerID, WaterID) VALUES (" + job_id + ",  " + customer_id + ", " + all_caw_list.get(j).getWaterid() + ");"))
                            return;
                    }
                }
            }
        }

        int draft_customer, draft_water;
        for(int i = 0; i < draft_list.size(); i++) {
            if(draft_list.get(i).getWater_amount() > 1) {
                draft_customer = draft_list.get(i).getCustomerid();
                draft_water = draft_list.get(i).getWaterid();

                dh.sql("UPDATE " + dh.EDITDRAFT + " SET WaterAmount = " + draft_list.get(i).getWater_amount()
                        + " WHERE JobID =" + job_id + " AND CustomerID =" + draft_customer + " AND WaterID =" + draft_water);
            }
        }

        showCustomersInRecyclerView();
        calculateGlobalIncome();
    }

    @Override
    public void OnNegativeClick(@NonNull EditJobChosenCustomerAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_customers.length; i++) {
            chosen_customers[i] = tmp_chosen_customers[i];
        }
    }

    public void showCustomersInRecyclerView() {
        draft_list.clear();
        dh.getJawDraftData("SELECT * FROM " + dh.EDITDRAFT + " WHERE JobID = " + job_id + ";", draft_list);
        adapter = new UpdateChosenCustomersListForJobAdapter(this, this, chosen_customers_list, all_water_list, draft_list, job_id);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    public void calculateGlobalIncome() {
        String result;
        if(draft_list.size() > 0) {
            int total_cost;
            total_cost = dh.getExactInt("SELECT SUM(w.Price * d.WaterAmount) FROM Waters w, JawDraft d WHERE w.ID = d.WaterID AND d.JobID = " + job_id + ";");
            result = String.format("%,d", total_cost).replace(",", " ");
            global_income = total_cost;
            result += " Ft";
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

        if(!dh.sql("UPDATE " + dh.JOBS + " SET Name='" + mad.result_text + "', Income=" + global_income + " WHERE ID=" + job_id + ";")) {
            Log.e(LOG_TITLE, "A módosítás sikertelen. (Első szakas - Job tábla módosítás)");
            return;
        }

        if(!dh.sql("DELETE FROM " + dh.CIJ + " WHERE JobID = " + job_id + ";")) return;
        ArrayList<Integer> identifiers = new ArrayList<>();
        for(int i = 0; i < draft_list.size(); i++) {
            if(draft_list.get(i).getJobid() == job_id && !identifiers.contains(draft_list.get(i).getCustomerid())) {
                if(!dh.sql("INSERT INTO " + dh.CIJ + " (JobID, CustomerID) " +
                        "VALUES (" + job_id + ", " + draft_list.get(i).getCustomerid() + ");" )) {
                    Log.e(LOG_TITLE, "A módosítás sikertelen. (Második szakas - CustomersInJob tábla módosítás)");
                    return;
                }
                identifiers.add(draft_list.get(i).getCustomerid());
            }
        }

        if (!dh.sql("DELETE FROM " + dh.JAW + " WHERE JobID = " + job_id + ";"))
            return;
        for(int i = 0; i < draft_list.size(); i++) {
            if (draft_list.get(i).getJobid() == job_id ) {
                if (!dh.sql("INSERT INTO " + dh.JAW + " (JobID, CustomerID, WaterID, WaterAmount) " +
                        "VALUES (" + job_id + ", " + draft_list.get(i).getCustomerid() + ", " + draft_list.get(i).getWaterid() + ", " + draft_list.get(i).getWater_amount() + ");")) {
                    return;
                }
            }
        }

        if(!dh.sql("DELETE FROM " + dh.EDITDRAFT + " WHERE JobID = " + job_id + ";")) {
            Log.e(LOG_TITLE, "Nem sikerült törölni a vázlatokat");
            return;
        }

        finish();
        setResult(1);
        Intent jobs = new Intent(this, AdminCheckUserJobDraftsActivity.class);
        startActivity(jobs);
        Toast.makeText(this, "Adatok módosítva", Toast.LENGTH_SHORT).show();
        mad.closeable = true;
        sld.saveCurrentJobID(-1);
    }

    private void getIntentData() {
        if(getIntent().hasExtra("job_id")) {
            job_id = getIntent().getIntExtra("job_id", -1);
            job_name = getIntent().getStringExtra("job_name");
            Log.i(LOG_TITLE, "Értékek átemelve. (JobID: " + job_id + ", JobName: " + job_name);
        } else {
            Log.e(LOG_TITLE, "Az értékek átemelése sikertelen.");
        }
    }
}