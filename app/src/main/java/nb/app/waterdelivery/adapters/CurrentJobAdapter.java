package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.MainActivity;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.jobs.JobVisitActivity;

public class CurrentJobAdapter extends RecyclerView.Adapter<CurrentJobAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Jobs> job_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    MainActivity mja;
    SaveLocalDatas sld;

    public CurrentJobAdapter(Context context, Activity activity, ArrayList<Jobs> job_list)  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.job_list = job_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        mja = (MainActivity) context;
        sld = new SaveLocalDatas(activity);
    }

    @NonNull
    @Override
    public CurrentJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_main_jobs_layout, parent, false);
        return new CurrentJobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentJobAdapter.ViewHolder holder, int position) {
        holder.job_name.setText(job_list.get(position).getName());

        String income_text = "Bevétel: " + job_list.get(position).getIncome() + " Ft";
        holder.income.setText(income_text);

        if(job_list.get(position).getFinish() != null) {
            holder.job_date.setVisibility(View.VISIBLE);
            String date = "Lezárva: " + job_list.get(position).getFinish();
            String[] date_part = date.split("\\.");
            holder.job_date.setText(date_part[0]);
            holder.job_check.setChecked(true);
            holder.job_check.setEnabled(false);
        } else {
            holder.job_date.setVisibility(View.GONE);
            holder.job_check.setChecked(false);
            holder.job_check.setEnabled(true);
        }

        holder.item.setOnClickListener(v -> {
            Intent details = new Intent(activity, JobVisitActivity.class);
            details.putExtra("job_id", job_list.get(position).getId());
            details.putExtra("job_name", job_list.get(position).getName());
            activity.startActivityForResult(details, 1);
        });

        holder.job_check.setOnClickListener(v -> {
            if(job_list.get(position).getFinish() == null) {
                int number_of_rows = dh.getExactInt("SELECT COUNT(*) " +
                        "FROM " + dh.CIJ +
                        " WHERE JobID = " + job_list.get(position).getId() + " AND Finish IS NULL");

                if(number_of_rows > 0) {
                    Toast.makeText(context, "Még nem zárható le", Toast.LENGTH_SHORT).show();
                    holder.job_check.setChecked(false);
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String finished = sdf.format(new Date());

                if(!dh.sql("UPDATE " + dh.JOBS + " SET Finish = '" + finished + "' WHERE ID = " + job_list.get(position).getId())) {
                    return;
                }
                holder.job_check.setEnabled(false);

                int job_id = job_list.get(position).getId();
                String name = job_list.get(position).getName();
                String created = job_list.get(position).getCreated();
                int income = job_list.get(position).getIncome();

                job_list.set(position, new Jobs(job_id, name, created, finished, income, sld.loadUserID()));
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView job_name, job_date, income;
        CheckBox job_check;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            job_name = itemView.findViewById(R.id.main_jobs_jobname);
            job_date = itemView.findViewById(R.id.main_jobs_date);
            item = itemView.findViewById(R.id.main_jobs_item);
            income = itemView.findViewById(R.id.main_jobs_income);
            job_check = itemView.findViewById(R.id.main_jobs_check);
        }
    }
}
