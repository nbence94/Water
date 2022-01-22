package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.CustomersInJob;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JobAndWaters;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.jobs.JobVisitActivity;

public class CurrentChosenJobAdapter extends RecyclerView.Adapter<CurrentChosenJobAdapter.ViewHolder>{

    LayoutInflater inflater;
    Context context;
    Activity activity;

    ArrayList<CustomersInJob> customers_in_jobs_list;
    ArrayList<JobAndWaters> waters_in_jobs_list;
    ArrayList<Waters> water_details_list;
    ArrayList<Customers> customers_detail_list;
    ArrayList<Boolean> expanded_list;

    DatabaseHelper dh;
    MyAlertDialog mad;
    JobVisitActivity jva;


    public CurrentChosenJobAdapter(Context context, Activity activity, ArrayList<CustomersInJob> cij_list,
                                            ArrayList<JobAndWaters> wij_list, ArrayList<Waters> wd_list,
                                             ArrayList<Customers> cd_list
    )  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.customers_in_jobs_list = cij_list;
        this.waters_in_jobs_list = wij_list;
        this.water_details_list = wd_list;
        this.customers_detail_list = cd_list;

        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        jva = (JobVisitActivity) context;

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

    @Override
    public void onBindViewHolder(@NonNull CurrentChosenJobAdapter.ViewHolder holder, int position) {

        boolean kinyitva = expanded_list.get(position);
        holder.item.setVisibility(kinyitva ? View.VISIBLE : View.GONE);

        holder.job_item.setOnClickListener(v -> {
            expanded_list.set(position, !kinyitva);
            notifyItemChanged(position);
        });

       int customer_id = customers_in_jobs_list.get(position).getCustomerid();
       String name = "-";
       for(int i = 0; i < customers_detail_list.size(); i++) {
            if(customer_id == customers_detail_list.get(i).getId()) {
                name = customers_detail_list.get(i).getFullname();
                if(customers_detail_list.get(i).getBill() == 1) {
                    holder.bill.setVisibility(View.VISIBLE);
                } else {
                    holder.bill.setVisibility(View.GONE);
                }
            }
       }
       holder.customer_name.setText(name);
       holder.bill.setTooltipText("Kér számlát");

       if( customers_in_jobs_list.get(position).getFinish() != null) {
           String finish = "Lezárva: " + customers_in_jobs_list.get(position).getFinish();
           holder.finish_date.setText(finish);
           holder.finish_check.setChecked(true);
           holder.finish_check.setEnabled(false);
       } else {
           holder.finish_date.setVisibility(View.GONE);
           holder.finish_check.setChecked(false);
           holder.finish_check.setEnabled(true);
       }

       StringBuilder waters = new StringBuilder();
       StringBuilder water_details = new StringBuilder();
       int global_income = 0;

       String water_name, water_amount, water_price;
       for(int i = 0; i < waters_in_jobs_list.size(); i++) {
           if(waters_in_jobs_list.get(i).getCustomerid() == customer_id) {
               for(int j = 0; j < water_details_list.size(); j++) {
                   if(water_details_list.get(j).getId() == waters_in_jobs_list.get(i).getWaterid()) {
                        water_name = water_details_list.get(j).getName();
                        waters.append(water_name);

                        water_amount = waters_in_jobs_list.get(i).getWateramount() + " db";
                        water_price = String.valueOf(waters_in_jobs_list.get(i).getWateramount() * water_details_list.get(j).getPrice());
                        water_details.append("[ ").append(water_amount).append(" - ").append(water_price).append(" Ft ]");

                        /*if(i < waters_in_jobs_list.size() - 2) {
                            water_details.append("\n");
                            waters.append("\n");
                        }*/

                       water_details.append("\n");
                       waters.append("\n");

                       global_income += Integer.parseInt(water_price);
                   }
               }
           }
       }
       holder.waters_text.setText(waters.toString());
       holder.water_details_text.setText(water_details.toString());

       String income_text = "Teljes ár: " + global_income + " Ft";
       holder.income_text.setText(income_text);

       holder.finish_check.setOnClickListener(v -> {
           if(jva.customers_in_jobs_list.get(position).getFinish() == null) {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
               String created_date = sdf.format(new Date());

               int jid = customers_in_jobs_list.get(position).getJobid();
               int cid = customers_in_jobs_list.get(position).getCustomerid();

               if(!dh.sql("UPDATE " + dh.CIJ + " SET Finish = '" + created_date + "' WHERE CustomerID = " + cid)) {
                   return;
               }

               jva.customers_in_jobs_list.set(position, new CustomersInJob(jid, cid, created_date));
               jva.showElements();
           }
       });

    }

    @Override
    public int getItemCount() {
        return customers_in_jobs_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView customer_name, waters_text, water_details_text, income_text, finish_date;
        CheckBox finish_check;
        ConstraintLayout item;
        ImageView bill;
        CardView job_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.job_customer_gui);
            waters_text = itemView.findViewById(R.id.current_job_water_datas);
            water_details_text = itemView.findViewById(R.id.current_job_water_details_datas);
            income_text = itemView.findViewById(R.id.current_job_income);
            finish_date = itemView.findViewById(R.id.current_job_finish_date);
            finish_check = itemView.findViewById(R.id.current_job_customer_checkbox);
            item = itemView.findViewById(R.id.expand_layout);
            bill = itemView.findViewById(R.id._current_job_need_bill_icon);
            job_item = itemView.findViewById(R.id.current_job_item);
        }
    }
}
