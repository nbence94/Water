package nb.app.waterdelivery.admin;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.CustomerAndWaters;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Waters;

public class AdminCustomerEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnDialogChoice {


    private final String LOG_TITLE = "AdminCustomerEditActivity";
    DatabaseHelper dh;
    MyAlertDialog mad;

    Toolbar toolbar;

    EditText name_field, city_field, address_field, email_field, phone_field, phoneplus_field;
    Spinner water_week_spinner;
    CheckBox bill_need_checkbox;
    FloatingActionButton save_button, add_water;
    TextView check_waters;

    int customer_id;
    String name_value, city_value, address_value, email_value, phone_value, phoneplus_value;
    int ww_value, bill_status;

    ArrayList<Waters> water_list;
    ArrayList<CustomerAndWaters> caw_list;
    String[] waters_name_to_show;
    public boolean[] chosen_waters;
    boolean[] tmp_chosen_waters;
    boolean water_status = false;

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
        bill_need_checkbox = findViewById(R.id.add_customer_bill_checkbox_gui);
        check_waters = findViewById(R.id.waters_selected_gui);
        add_water = findViewById(R.id.customers_add_waters_gui);
        save_button = findViewById(R.id.admin_add_customer_save_button_gui);

        //Spinner
        ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(this, R.array.weeks, android.R.layout.simple_spinner_item);
        AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        water_week_spinner.setAdapter(AA);
        water_week_spinner.setOnItemSelectedListener(this);

        //Adatok lekérése
        getIntentData();
        name_field.setText(name_value);
        city_field.setText(city_value);
        address_field.setText(address_value);
        email_field.setText(email_value);
        phone_field.setText(phone_value);
        phoneplus_field.setText(phoneplus_value);
        water_week_spinner.setSelection((ww_value - 1));
        bill_need_checkbox.setChecked(bill_status == 1);


        //Víz szekció
        check_waters.setVisibility(View.GONE);//Csak akkor jelenjen meg,ha van hozzá téve víz
        water_list = new ArrayList<>();
        caw_list = new ArrayList<>();

        dh.getWatersData("SELECT * FROM " + dh.WATERS, water_list);//Mindent meg kell jeleníteni
        waters_name_to_show = new String[water_list.size()];
        chosen_waters = new boolean[water_list.size()];
        tmp_chosen_waters = new boolean[water_list.size()];

        //Beletesszük a megjelenítendő neveket
        for (int i = 0; i < water_list.size(); i++) {
            waters_name_to_show[i] = water_list.get(i).getName();
        }

        //Kellenek azok a waterID-k, amiket ki kell jelölni
        dh.getCAWData(caw_list, "WHERE CustomerID = " + customer_id);
        if (caw_list.size() > 0) {
            check_waters.setVisibility(View.VISIBLE);
            for (int i = 0; i < caw_list.size(); i++) {
                chosen_waters[caw_list.get(i).getWaterid() - 1] = true;
            }
        }

        add_water.setOnClickListener(v -> {
            mad.AlertMultiSelectDialog("Milyen vízre van szüksége?", waters_name_to_show, chosen_waters, tmp_chosen_waters, "Rendben", "Mégse", null, 0, this);
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

            if(!dh.checkEmail(dh.CUSTOMERS, email_value, "WHERE ID != " + customer_id)) {
                email_field.setError("Az e-mail foglalt!");
                email_field.requestFocus();
                return;
            }

            String update = "UPDATE " + dh.CUSTOMERS + " SET Fullname = '" + name_value + "', City = '" + city_value + "', Address = '" + address_value + "', "
                            + " Email = '" + email_value + "', Phone='" + phone_value + "', PhonePlus = '" + phoneplus_value + "', WaterWeeks = " + ww_value + ", Bill = " + bill_status
                            + " WHERE ID = " + customer_id + ";";

            if(!dh.sql(update)) {
                Log.e(LOG_TITLE, update);
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                return;
            }

            if(water_status) {
                int water_id;
                String insert_waters;
                if(!dh.sql("DELETE FROM " + dh.CAW + " WHERE CustomerID = " + customer_id + ";")) {
                    Toast.makeText(this, "A vizek mentése sikertelen", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < chosen_waters.length; i++) {
                    if (chosen_waters[i]) {
                        water_id = water_list.get(i).getId();
                        insert_waters = "INSERT INTO " + dh.CAW + " (CustomerID, WaterID) VALUES (" + customer_id + ", " + water_id + ");";
                        if (!dh.sql(insert_waters)) {
                            return;
                        }
                        Log.i(LOG_TITLE, insert_waters);
                    }
                }
            } else Log.i(LOG_TITLE, "Víz nem lett hozzáadva");

            Log.i(LOG_TITLE, update);
            Toast.makeText(this, "Mentés", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
            Intent custoemrs_screen = new Intent(AdminCustomerEditActivity.this, AdminAllCustomersActivity.class);
            startActivity(custoemrs_screen);
        });
    }

    private void getIntentData() {
        if(getIntent().hasExtra("customer_id")) {
            customer_id = getIntent().getIntExtra("customer_id", -1);
            name_value =  getIntent().getStringExtra("customer_name");
            city_value  =  getIntent().getStringExtra("customer_city");
            address_value  =  getIntent().getStringExtra("customer_address");
            email_value  =  getIntent().getStringExtra("customer_email");
            phone_value  =  getIntent().getStringExtra("customer_phone");
            phoneplus_value  =  getIntent().getStringExtra("customer_phoneplus");

            ww_value  =  getIntent().getIntExtra("customer_ww", -1);
            bill_status  =  getIntent().getIntExtra("customer_bill", -1);
        }
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
