package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    }


    @NonNull
    @Override
    public CurrentChosenJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_current_job_customer_layout, parent, false);
        return new CurrentChosenJobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentChosenJobAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView customer_name, waters_text, income_text, finish_date;
        CheckBox finish_check;
        ConstraintLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.job_customer_gui);
            waters_text = itemView.findViewById(R.id.current_job_water_datas);
            income_text = itemView.findViewById(R.id.current_job_income);
            finish_date = itemView.findViewById(R.id.current_job_finish_date);
            finish_check = itemView.findViewById(R.id.current_job_customer_checkbox);
            item = itemView.findViewById(R.id.expand_layout);
        }
    }
}
