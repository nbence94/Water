package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.AdminUserJobDetailsActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;


public class UserJobsDetailsAdapter extends RecyclerView.Adapter<UserJobsDetailsAdapter.ViewHolder>{

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Jobs> job_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    AdminUserJobDetailsActivity aujda;
    ArrayList<Boolean> expanded_list;

    //Gyerek-adapter
    ArrayList<Customers> customers_list;
    UserJobsDetailsChildAdapter adapter;

    public UserJobsDetailsAdapter(Context context, Activity activity, ArrayList<Jobs> job_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.job_list = job_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        aujda = (AdminUserJobDetailsActivity) context;

        this.expanded_list = new ArrayList<>();
        for(int i = 0; i < job_list.size(); i++) {
            expanded_list.add(false);
        }

        customers_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserJobsDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_admin_userjobs_layout, parent, false);
        return new UserJobsDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJobsDetailsAdapter.ViewHolder holder, int position) {

        showElements(holder, position);

        holder.job_name.setText(job_list.get(position).getName());
        String income_text = job_list.get(position).getIncome() + " Ft";
        holder.income.setText(income_text);

        String created_text = "Létrehozva: ";
        if(job_list.get(position).getCreated() != null) {
            String[] date_array = job_list.get(position).getCreated().split("\\.");
            created_text += date_array[0];
        } else {
            created_text += " - ";
        }
        holder.job_created.setText(created_text);

        String finished_text = "Elvégezve: ";
        if(job_list.get(position).getFinish() != null) {
            String[] date_array = job_list.get(position).getFinish().split("\\.");
            finished_text += date_array[0];
        } else {
            finished_text += " - ";
        }
        holder.job_finished.setText(finished_text);

        boolean kinyitva = expanded_list.get(position);
        holder.expand.setVisibility(kinyitva ? View.VISIBLE : View.GONE);

        holder.item.setOnClickListener(v -> {
            expanded_list.set(position, !kinyitva);
            notifyItemChanged(position);
        });


    }

    public void showElements(@NonNull UserJobsDetailsAdapter.ViewHolder holder, int position) {
        customers_list.clear();
        dh.getCustomersData("SELECT * FROM " + dh.CUSTOMERS + " c, " + dh.CIJ + " cij WHERE c.ID = cij.CustomerID AND cij.JobID = " +  job_list.get(position).getId() + "; ", customers_list);
        adapter = new UserJobsDetailsChildAdapter(context,  activity, customers_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recycler.setLayoutManager(manager);
        holder.recycler.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView job_name, job_created, job_finished, income;
        CardView item;
        ConstraintLayout expand;
        RecyclerView recycler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            job_name = itemView.findViewById(R.id.custom_user_job_name_gui);
            job_created = itemView.findViewById(R.id.custom_user_job_created_gui);
            job_finished = itemView.findViewById(R.id.custom_user_job_finish_gui);
            item = itemView.findViewById(R.id.custom_user_job_item);
            income = itemView.findViewById(R.id.custom_user_job_profit_gui);
            expand = itemView.findViewById(R.id.expand_layout);
            recycler = itemView.findViewById(R.id.customers_for_jobs_recycler);
        }

    }
}
