package nb.app.waterdelivery.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Waters;

public class AdminNewCustomerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnDialogChoice {

    private final String LOG_TITLE = "AdminNewCustomerActivity";

    DatabaseHelper dh;
    MyAlertDialog mad;

    Toolbar toolbar;

    EditText name_field, city_field, address_field, email_field, phone_field, phoneplus_field;
    Spinner water_week_spinner;
    CheckBox bill_need_checkbox;
    FloatingActionButton save_button, add_water;
    TextView check_waters;

    String name_value, city_value, address_value, email_value, phone_value, phoneplus_value;
    int ww_value, bill_status;

    //Víz választás
    boolean water_status = false;
    ArrayList<Waters> water_list;
    String[] waters_name_to_show;
    public boolean[] chosen_waters;
    boolean[] tmp_chosen_waters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_customer);

        dh = new DatabaseHelper(this, this);
        mad = new MyAlertDialog(this, this);

        //Vissza gomb
        toolbar = findViewById(R.id.admin_add_customer_toolbar_gui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Elemek
        name_field = findViewById(R.id.add_customer_name_gui);
        city_field = findViewById(R.id.add_customer_city_gui);
        address_field = findViewById(R.id.add_customer_address_gui);
        email_field = findViewById(R.id.add_customer_email_gui);
        phone_field = findViewById(R.id.add_customer_telephone_one_gui);
        phoneplus_field = findViewById(R.id.add_customer_telephone_two_gui);
        water_week_spinner = findViewById(R.id.add_customer_water_spinner_gui);
        check_waters = findViewById(R.id.waters_selected_gui);
        add_water = findViewById(R.id.customers_add_waters_gui);
        bill_need_checkbox = findViewById(R.id.add_customer_bill_checkbox_gui);
        save_button = findViewById(R.id.admin_add_customer_save_button_gui);

        //Ez először legyen nem látható
        check_waters.setVisibility(View.GONE);

        //Spinner
        ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(this, R.array.weeks,android.R.layout.simple_spinner_item);
        AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        water_week_spinner.setAdapter(AA);
        water_week_spinner.setOnItemSelectedListener(this);

        //Víz választás
        water_list = new ArrayList<>();
        dh.getWatersData("SELECT * FROM " + dh.WATERS, water_list);
        waters_name_to_show = new String[water_list.size()];
        chosen_waters = new boolean[water_list.size()];
        tmp_chosen_waters = new boolean[water_list.size()];

        //Beletesszük a megjelenítendő neveket
        for(int i = 0; i < water_list.size(); i++) {
            waters_name_to_show[i] = water_list.get(i).getName();
        }

        add_water.setOnClickListener(v -> {
            mad.AlertMultiSelectDialog("Milyen vízre van szüksége?", waters_name_to_show, chosen_waters, tmp_chosen_waters, "Rendben", "Mégse", null, 0,this);
        });

        save_button.setOnClickListener(v ->{
            name_value = name_field.getText().toString().trim();
            city_value = city_field.getText().toString().trim();
            address_value = address_field.getText().toString().trim();
            email_value = email_field.getText().toString().trim();
            phone_value = phone_field.getText().toString().trim();
            phoneplus_value = phoneplus_field.getText().toString().trim();
            bill_status = (bill_need_checkbox.isChecked()) ? 1 : 0;

            if(name_value.isEmpty() || city_value.isEmpty() || address_value.isEmpty()
                    || email_value.isEmpty() || phone_value.isEmpty()) {

                if(name_value.isEmpty()) {
                    name_field.setError("Kötelező mező!");
                    name_field.requestFocus();
                }

                if(city_value.isEmpty()) {
                    city_field.setError("Kötelező mező!");
                    city_field.requestFocus();
                }

                if(address_value.isEmpty()) {
                    address_field.setError("Kötelező mező!");
                    address_field.requestFocus();
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                    email_field.setError("Helytelen formátum!");
                    email_field.requestFocus();
                }

                if(phone_value.isEmpty()) {
                    phone_field.setError("Kötelező mező!");
                    phone_field.requestFocus();
                }

                return;
            }

            if(!dh.checkEmail(dh.CUSTOMERS, email_value, "")) {
                email_field.setError("Az e-mail foglalt!");
                email_field.requestFocus();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String aktualis_datum = sdf.format(new Date());

            String insert_customer = "INSERT INTO " + dh.CUSTOMERS + " (Created, Fullname, City, Address, Email, Phone, PhonePlus, WaterWeeks, Bill) VALUES " +
                    "('" + aktualis_datum + "', '" + name_value + "', '" + city_value + "', '" + address_value + "', '" + email_value + "', "
                    + " '" + phone_value + "', '" + phoneplus_value + "', " + ww_value + ", " + bill_status + ");";



            if(!dh.sql(insert_customer)) {
                Log.i(LOG_TITLE, insert_customer);
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                return;
            }

            if(water_status) {
                int new_customer = dh.getNewID(dh.CUSTOMERS);
                int water_id;
                String insert_waters;
                for (int i = 0; i < chosen_waters.length; i++) {
                    if (chosen_waters[i]) {
                        water_id = water_list.get(i).getId();
                        /*insert_waters = "INSERT INTO " + dh.CAW + " (CustomerID, WaterID) VALUES (" + new_customer + ", " + water_id + ");";
                        if (!dh.sql(insert_waters)) {
                            return;
                        }*/
                        if (!dh.insert(new String[] {String.valueOf(new_customer), String.valueOf(water_id)}, new String[] {"CustomerID", "WaterID"}, dh.CAW)) {
                            return;
                        }
                    }
                }
            } else Log.i(LOG_TITLE, "Víz nem lett hozzáadva");

            Toast.makeText(this, "Mentés", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
            Intent custoemrs_screen = new Intent(AdminNewCustomerActivity.this, AdminAllCustomersActivity.class);
            startActivity(custoemrs_screen);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ww_value = Integer.parseInt(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        check_waters.setVisibility(View.GONE);
        water_status = false;
        for(boolean check : chosen_waters) {
            if(check) {
                check_waters.setVisibility(View.VISIBLE);
                water_status = true;
                break;
            }
        }
    }

    @Override
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        for(int i = 0; i < chosen_waters.length; i++) {
            chosen_waters[i] = tmp_chosen_waters[i];
        }
    }
}