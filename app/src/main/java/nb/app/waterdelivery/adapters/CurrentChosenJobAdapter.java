package nb.app.waterdelivery.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.currentjob.CurrentChosenJobChildAdapter;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.CustomersInJob;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JobAndWaters;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.helper.DateTrim;
import nb.app.waterdelivery.helper.NumberSplit;
import nb.app.waterdelivery.helper.PhoneNumberFormat;
import nb.app.waterdelivery.jobs.JobVisitActivity;

public class CurrentChosenJobAdapter extends RecyclerView.Adapter<CurrentChosenJobAdapter.ViewHolder>{

    LayoutInflater inflater;
    Context context;
    Activity activity;
    CurrentChosenJobChildAdapter adapter;

    ArrayList<CustomersInJob> customers_in_jobs_list;
    ArrayList<JobAndWaters> waters_in_jobs_list;
    ArrayList<Waters> water_details_list;
    ArrayList<Customers> customers_detail_list;
    ArrayList<Boolean> expanded_list;
    ArrayList<JobAndWaters> jaw_list = new ArrayList<>();
    ArrayList<String> customers_income;

    DatabaseHelper dh;
    MyAlertDialog mad;
    JobVisitActivity jva;
    PhoneNumberFormat pnf;

    public CurrentChosenJobAdapter(Context context, Activity activity, ArrayList<CustomersInJob> cij_list,
                                            ArrayList<JobAndWaters> wij_list, ArrayList<Waters> wd_list,
                                             ArrayList<Customers> cd_list, ArrayList<String> customers_income, ArrayList<Boolean> expand
    )  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.customers_in_jobs_list = cij_list;
        this.waters_in_jobs_list = wij_list;
        this.water_details_list = wd_list;
        this.customers_detail_list = cd_list;
        this.customers_income = customers_income;

        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        pnf = new PhoneNumberFormat();
        jva = (JobVisitActivity) context;

        //this.expanded_list = expand;
        this.expanded_list = new ArrayList<>();
        for(int i = 0; i < customers_in_jobs_list.size(); i++) {
            expanded_list.add(false);
        }

    }

    @NonNull
    @Override
    public CurrentChosenJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_current_job_customer_layout, parent, false);
        return new CurrentChosenJobAdapter.ViewHolder(view);
    }

    public void showElements(@NonNull CurrentChosenJobAdapter.ViewHolder holder, int position, int customer, int closed) {
        jaw_list.clear();
        dh.getJAWData("SELECT * FROM " + dh.JAW + " WHERE JobID=" + waters_in_jobs_list.get(position).getJobid() + " AND CustomerID=" + customer + ";" ,jaw_list);

        adapter = new CurrentChosenJobChildAdapter(context,  activity, water_details_list, jaw_list, closed);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(manager);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentChosenJobAdapter.ViewHolder holder, int position) {

        //Fontos dolgok kinyerése
        int customer_id = customers_in_jobs_list.get(position).getCustomerid();

        int index = -1;
        for(int i = 0; i < customers_detail_list.size(); i++) {
            if(customer_id == customers_detail_list.get(i).getId()) {
                index = i;
                break;
            }
        }

        //Megrendelő lezárt-e
        int closed = customers_in_jobs_list.get(position).getFinish() == null ? 0 : 1;
        showElements(holder, position, customer_id, closed);

        //Elemek kinyitása és összecsukása
        boolean kinyitva = expanded_list.get(position);
        holder.item.setVisibility(kinyitva ? View.VISIBLE : View.GONE);
        holder.job_item.setOnClickListener(v -> {
            expanded_list.set(position, !kinyitva);
            //jva.expanded_list.set(position, !kinyitva);
            notifyItemChanged(position);
        });

        //Megrendelő adatainak kiírása
        holder.customer_name.setText(customers_detail_list.get(index).getFullname());

        String full_address = customers_detail_list.get(index).getCity() + " - " + customers_detail_list.get(index).getAddress();
        holder.customers_address.setText(full_address.toUpperCase(Locale.ROOT));

        String phones = "Elérhetőség:\n";
        String phone_number = customers_detail_list.get(index).getPhone_one();
        if(pnf.checkMobile(phone_number)) {
            phones += PhoneNumberFormat.mobileFormat(phone_number);
        }
        else if(pnf.checkPhone(phone_number)) {
            phones += PhoneNumberFormat.phoneFormat(phone_number);
        }
        else {
            phones += customers_detail_list.get(index).getPhone_one();
        }

        if(!customers_detail_list.get(index).getPhone_two().equals("")) {
            phone_number = customers_detail_list.get(index).getPhone_two();

            if(pnf.checkMobile(phone_number)) {
                phones += "\n" + PhoneNumberFormat.mobileFormat(phone_number);
            }
            else if(pnf.checkPhone(phone_number)) {
                phones += "\n" + PhoneNumberFormat.phoneFormat(phone_number);
            }
            else {
                phones += "\n" + customers_detail_list.get(index).getPhone_two();
            }

        }

        holder.customers_tel.setText(phones);

        if(customers_detail_list.get(index).getBill() == 1) {
            holder.bill.setVisibility(View.VISIBLE);
        } else {
            holder.bill.setVisibility(View.GONE);
        }
        holder.bill.setTooltipText("Kér számlát");


        if(customers_in_jobs_list.get(position).getFinish() != null) {
           String finish = "Lezárva: " + DateTrim.trim(customers_in_jobs_list.get(position).getFinish());
           holder.finish_date.setText(finish);
           holder.finish_check.setChecked(true);
           holder.finish_check.setEnabled(false);
        } else {
           holder.finish_date.setVisibility(View.GONE);
           holder.finish_check.setChecked(false);
           holder.finish_check.setEnabled(true);
        }

       String income_text = "Teljes ár: " + NumberSplit.splitNum(Integer.parseInt(customers_income.get(position))) + " Ft";
       holder.income_text.setText(income_text);

       //Gombok
       holder.finish_check.setOnClickListener(v -> {
           if(jva.customers_in_jobs_list.get(position).getFinish() == null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
               String created_date = sdf.format(new Date());

               int jid = customers_in_jobs_list.get(position).getJobid();
               int cid = customers_in_jobs_list.get(position).getCustomerid();

               if(!dh.sql("UPDATE " + dh.CIJ + " SET Finish = '" + created_date + "' WHERE CustomerID = " + cid + " AND JobID=" + jid)) {
                   return;
               }

               jva.customers_in_jobs_list.set(position, new CustomersInJob(jid, cid, created_date));
               jva.showElements();

               boolean all_of_theam_is_done = true;
               for(int i = 0; i < jva.customers_in_jobs_list.size(); i++) {
                   if(jva.customers_in_jobs_list.get(i).getFinish() == null) {
                       all_of_theam_is_done = false;
                       break;
                   }
               }

               if(all_of_theam_is_done) {
                   if(!dh.sql("UPDATE " + dh.JOBS + " SET Finish = '" + created_date + "' WHERE ID=" + jid)) {
                       return;
                   }
                   Toast.makeText(context, "A tervezet automatikusan lezárásra került", Toast.LENGTH_SHORT).show();
               }
           }
       });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void reload() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return customers_in_jobs_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView customer_name, customers_address, customers_tel, income_text, finish_date;
        CheckBox finish_check;
        ConstraintLayout item;
        ImageView bill;
        CardView job_item;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.job_customer_gui);
            recyclerView = itemView.findViewById(R.id.current_job_water_recycler);
            income_text = itemView.findViewById(R.id.current_job_income);
            finish_date = itemView.findViewById(R.id.current_job_finish_date);
            finish_check = itemView.findViewById(R.id.current_job_customer_checkbox);
            item = itemView.findViewById(R.id.expand_layout);
            bill = itemView.findViewById(R.id._current_job_need_bill_icon);
            job_item = itemView.findViewById(R.id.current_job_item);
            customers_address = itemView.findViewById(R.id.current_job_address);
            customers_tel = itemView.findViewById(R.id.current_job_phone);
        }
    }
}
