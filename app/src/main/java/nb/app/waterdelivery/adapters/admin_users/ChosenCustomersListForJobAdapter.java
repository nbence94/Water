package nb.app.waterdelivery.adapters.admin_users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.users.AdminCreateJobForUserActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogChoice;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Draft;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.helper.NumberSplit;


public class ChosenCustomersListForJobAdapter extends RecyclerView.Adapter<ChosenCustomersListForJobAdapter.ViewHolder> implements OnDialogChoice {

    private final String LOG_TITLE = "ChosenCustomersAdapter";

    Context context;
    Activity activity;

    AdminCreateJobForUserActivity cja;
    MyAlertDialog mad;
    LayoutInflater inflater;
    DatabaseHelper dh;
    SaveLocalDatas sld;

    ArrayList<Customers> customers_list;
    ArrayList<Waters> waters_list;
    ArrayList<Draft> draft_list;

    //Gyerek recycler
    ChosenCustomerWatersListForJobAdapter adapter;
    int customer_cost;

    String[] waters_name_to_show;
    public boolean[] chosen_waters_boolean;
    boolean[] tmp_chosen_waters_boolean;

    public ChosenCustomersListForJobAdapter(Context context, Activity activity, ArrayList<Customers> customers_data, ArrayList<Waters> water_list, ArrayList<Draft> draft_list) {
        this.inflater = LayoutInflater.from(context);
        this.customers_list = customers_data;
        this.waters_list = water_list;
        this.draft_list = draft_list;

        this.context = context;
        this.activity = activity;
        dh = new DatabaseHelper(context, activity);
        sld = new SaveLocalDatas(activity);
        this.cja = (AdminCreateJobForUserActivity) context;
        this.mad = new MyAlertDialog(context, activity);

        this.customer_cost = 0;
    }

    @NonNull
    @Override
    public ChosenCustomersListForJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_chosen_customer_layout, parent, false);
        return new ChosenCustomersListForJobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChosenCustomersListForJobAdapter.ViewHolder holder, int position) {
        ArrayList<Draft> child_draft_list = new ArrayList<>();

        //Elemek megad??sa
        holder.customer_name.setText(customers_list.get(position).getFullname().toUpperCase(Locale.ROOT));
        holder.customer_city.setText(customers_list.get(position).getCity());
        holder.customer_address.setText(customers_list.get(position).getAddress());

        if(customers_list.get(position).getBill() == 1) {
            holder.need_bill_icon.setVisibility(View.VISIBLE);
        } else {
            holder.need_bill_icon.setVisibility(View.GONE);
        }
        holder.need_bill_icon.setTooltipText("K??r sz??ml??t");

        //Megrendel??h??z tartoz?? ??sszeg ki??r??sa
        calculateCostForWater(holder, position);

        //Gyerek-adapter megh??v??s, adatok ??tad??sa
        dh.getDraftData("SELECT * FROM " + dh.DRAFT + " WHERE CustomerID = " + customers_list.get(position).getId() + " AND UserID = " + sld.loadCurrentUserID() + ";", child_draft_list);

        //AlertDialog be??ll??t??sok
        initializeArrays(waters_list.size());
        setShowableNames(waters_list);

        //
        holder.add_water.setOnClickListener(v -> {
            Log.i(LOG_TITLE, "Kattint??s -> V??z hozz??ad??s (" + position + ")");
            setChosenWatersChecked(position);
            mad.AlertMultiSelectDialog("V??lassz vizet", waters_name_to_show, chosen_waters_boolean, tmp_chosen_waters_boolean, "Rendben", "M??gse", holder, position,this);
        });

        holder.add_water.setOnLongClickListener(v -> {
            holder.add_water.setTooltipText("Vizek hozz??ad??sa");
            return false;
        });


        loadElements(holder, child_draft_list);

    }

    private void loadElements(@NonNull ViewHolder holder, ArrayList<Draft> draft_list) {
        adapter = new ChosenCustomerWatersListForJobAdapter(context, activity, waters_list, draft_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recycler.setLayoutManager(manager);
        holder.recycler.setAdapter(adapter);
        Log.i(LOG_TITLE, "Megrendel??k bet??lt??se az AlertDialogba");
    }


    public void calculateCostForWater(@NonNull ViewHolder holder, int position) {
        customer_cost = 0;
        for(int i = 0; i < draft_list.size(); i++) {
            for(int j = 0; j < waters_list.size(); j++) {
                if (draft_list.get(i).getCustomerid() == customers_list.get(position).getId()) {
                    if (draft_list.get(i).getWaterid() == waters_list.get(j).getId()) {
                        customer_cost += draft_list.get(i).getWater_amount() * waters_list.get(j).getPrice();
                    }
                }
            }
        }
        String result_cost = NumberSplit.splitNum(customer_cost);
        holder.cost.setText(result_cost);
    }


    @Override
    public int getItemCount() {
        return customers_list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        int amount_of_chosen_waters = 0;
        for (boolean b : chosen_waters_boolean) {
            if (b) amount_of_chosen_waters++;
        }

        //Csak akkor fusson le, ha van kiv??lasztva v??z
        if(amount_of_chosen_waters > 0) {
            Log.e(LOG_TITLE, "");

            //El??sz??r ??r??tj??k a Draft t??bl??t
           if (!dh.sql("DELETE FROM " + dh.DRAFT + " WHERE CustomerID = " + customers_list.get(position).getId() + " AND UserID = " + sld.loadCurrentUserID())) return;
           Log.i(LOG_TITLE, "Kor??bbi Draft elemek t??r??lve.");

            //T??r??lj??k a Customers And Waters t??bla adatait is (A program felt??telezi, hogy a v??lasztott vizek m??skor is kellenek)
            if(!dh.sql("DELETE FROM " + dh.CAW + " WHERE CustomerID = " + customers_list.get(position).getId() + ";")) {
                Log.e(LOG_TITLE, "Nem siker??lt t??r??lni a CAW adatokat!");
                return;
            }

            //Felt??ltj??k az ??j adatokat
            for (int i = 0; i < chosen_waters_boolean.length; i++) {
                if (chosen_waters_boolean[i]) {
                    //Felt??ltj??k a Draft adatokat
                    if (!dh.sql("INSERT INTO " + dh.DRAFT + " (UserID, CustomerID, WaterID) VALUES (" + sld.loadCurrentUserID() + ", " + customers_list.get(position).getId() + ", " + waters_list.get(i).getId() + ");")) {
                        Log.e(LOG_TITLE, "Nem siker??lt felt??lteni a v??zlat adatokat. (DRAFT)");
                        return;
                    }
                    Log.i(LOG_TITLE, "::: A " + waters_list.get(i).getName() + " v??z felt??ltve. (" + waters_list.get(i).getId() + ")  [DRAFT]");

                    //Felt??ltj??k az ??j vizeket a felhaszn??l??khoz
                    if (!dh.sql("INSERT INTO " + dh.CAW + " (CustomerID, WaterID) VALUES (" + customers_list.get(position).getId() + ", " + waters_list.get(i).getId() + ");")) {
                        Log.e(LOG_TITLE, "Nem siker??lt felt??lteni a megrendel?? vizeit (CustomersAndWaters)");
                        return;
                    }
                    Log.i(LOG_TITLE, "::: A " + waters_list.get(i).getName() + " v??z felt??ltve. (" + waters_list.get(i).getId() + ") [CAW]");
                }
            }

            cja.all_caw_list.clear();
            dh.getCAWData(cja.all_caw_list, "");

            //Most a draftban ??t??rjuk a mennyis??geket
            int draft_user, draft_customer, draft_water;
            for(int i = 0; i < draft_list.size(); i++) {
                if(draft_list.get(i).getWater_amount() > 1 && draft_list.get(i).getCustomerid() == customers_list.get(position).getId() && draft_list.get(i).getUserid() == sld.loadCurrentUserID()) {
                    draft_user = sld.loadCurrentUserID();
                    draft_customer = draft_list.get(i).getCustomerid();
                    draft_water = draft_list.get(i).getWaterid();

                    dh.sql("UPDATE " + dh.DRAFT + " SET WaterAmount = " + draft_list.get(i).getWater_amount()
                            + " WHERE UserID =" + draft_user + " AND CustomerID =" + draft_customer + " AND WaterID =" + draft_water);

                }
            }

            draft_list.clear();

            //Megjelen??teni az ??jakat
            cja.chosen_customers_list.clear();
            cja.loadDraftElements();
            cja.showCustomersInRecyclerView();
            cja.calculateGlobalIncome();

        } else {
            Toast.makeText(context, "Legal??bb egy v??z legyen! Vagy t??r??ld a megrendel??!", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TITLE, "Nem lett v??z megadva.");
        }
    }

    @Override
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position) {
        System.arraycopy(tmp_chosen_waters_boolean, 0, chosen_waters_boolean, 0, chosen_waters_boolean.length);
    }

    private void setChosenWatersChecked(int position) {
        Arrays.fill(chosen_waters_boolean, false);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chosen_waters_boolean.length; i++) {
            for(int j = 0; j < draft_list.size(); j++) {
                if(draft_list.get(j).getWaterid() == waters_list.get(i).getId() && draft_list.get(j).getCustomerid() == customers_list.get(position).getId()) {
                    sb.append("true").append(",");
                    chosen_waters_boolean[i] = true;
                }
            }
        }

        Log.i(LOG_TITLE, "A sz??ks??ges elemek kipip??l??sa AlertDialogban. (" + position + ". index?? elem)");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView customer_name, customer_city, customer_address, cost;
        RecyclerView recycler;
        ImageView add_water, need_bill_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.chosen_customer_name_gui);
            customer_city  = itemView.findViewById(R.id.chosen_customer_city_gui);
            customer_address = itemView.findViewById(R.id.chosen_customer_address_gui);
            cost = itemView.findViewById(R.id.chosen_customer_cost_gui);
            recycler = itemView.findViewById(R.id.chosen_customer_water_recycler);
            add_water = itemView.findViewById(R.id.add_more_water);
            need_bill_icon = itemView.findViewById(R.id.need_bill_icon);
        }
    }

    public void initializeArrays(int size) {
        waters_name_to_show = new String[size];
        chosen_waters_boolean = new boolean[size];
        tmp_chosen_waters_boolean = new boolean[size];
    }

    public void setShowableNames(ArrayList<Waters> list) {
        for(int i = 0; i < list.size(); i++)
            waters_name_to_show[i] = list.get(i).getName();
    }
}
