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

import java.util.ArrayList;

import nb.app.waterdelivery.MainActivity;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.WarningDialogChoice;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.jobs.EditMyJobActivity;
import nb.app.waterdelivery.jobs.MyJobsActivity;

public class CurrentJobAdapter extends RecyclerView.Adapter<CurrentJobAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Jobs> job_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    MainActivity mja;

    public CurrentJobAdapter(Context context, Activity activity, ArrayList<Jobs> job_list)  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.job_list = job_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        mja = (MainActivity) context;
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

        String income_text = "Bevétel: " + job_list.get(position).getIncome();
        holder.income.setText(income_text);

        if(job_list.get(position).getFinish() != null) {
            holder.job_date.setVisibility(View.VISIBLE);
            String date = "Lezárva: " + job_list.get(position).getFinish();
            holder.job_date.setText(date);
        } else {
            holder.job_date.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(v -> {
            Toast.makeText(context, "Ez át fog vinni egy másik oldalra", Toast.LENGTH_SHORT).show();
        });

        holder.item.setOnLongClickListener(v -> {
            //mad.AlertWarningDialog("Megerősítés", "Biztosan törlöd ezt a munkát?", "Igen", "Nem", holder, position, this);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    /*@Override
    public void OnPositiveClick(@NonNull CurrentJobAdapter.ViewHolder holder, int position) {
        int job_id = job_list.get(position).getId();
        if(dh.sql("CALL delete_job(" + job_id + ");")) {
            Toast.makeText(context, "A kijelölt munka törölve!", Toast.LENGTH_SHORT).show();
            mja.showElements();
            //notifyDataSetChanged();
        }
        else {
            Toast.makeText(context, "Törlés sikertelen!", Toast.LENGTH_SHORT).show();
        }

        //TODO Megcsinálni, hogy törlés után újratöltsön az oldal
    }

    @Override
    public void OnNegativeClick(@NonNull CurrentJobAdapter.ViewHolder holder, int position) {
    }*/

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
