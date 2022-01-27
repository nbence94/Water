package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.myWarningDialogChoice;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.jobs.EditMyJobActivity;
import nb.app.waterdelivery.jobs.MyJobsActivity;

public class MyJobsAdapter extends RecyclerView.Adapter<MyJobsAdapter.ViewHolder> implements myWarningDialogChoice {

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Jobs> job_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    MyJobsActivity mja;

    int number_of_rows;

    public MyJobsAdapter(Context context, Activity activity, ArrayList<Jobs> job_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.job_list = job_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        mja = (MyJobsActivity) context;
    }

    @NonNull
    @Override
    public MyJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_my_jobs_layout, parent, false);
        return new MyJobsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyJobsAdapter.ViewHolder holder, int position) {

        holder.job_name.setText(job_list.get(position).getName());
        holder.income.setText(String.valueOf(job_list.get(position).getIncome()));

        if(job_list.get(position).getFinish() != null) {
            holder.job_date.setVisibility(View.GONE);
        } else {
            holder.job_date.setVisibility(View.VISIBLE);
            holder.job_date.setText(job_list.get(position).getFinish());
        }

        holder.item.setOnClickListener(v -> {

            if(!checkJobStatus(position, "Figyelmeztetés", "Nem módosítható, mert tartozik hozzá leadott víz!")) {
                return;
            }

            Intent edit_job = new Intent(activity, EditMyJobActivity.class);
            edit_job.putExtra("job_id", job_list.get(position).getId());
            edit_job.putExtra("job_name", job_list.get(position).getName());
            activity.startActivityForResult(edit_job, 1);

        });

        holder.item.setOnLongClickListener(v -> {
            mad.myWarningDialog("Megerősítés", "Biztosan törlöd ezt a munkát?", "Igen", "Nem", holder, position, this);
            return false;

        });

    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    public boolean checkJobStatus(int position, String title, String msg) {
        number_of_rows = dh.getExactInt("SELECT COUNT(*) " +
                "FROM " + dh.CIJ +
                " WHERE JobID = " + job_list.get(position).getId() + " AND Finish IS NOT NULL");

        if(number_of_rows > 0) {
            mad.AlertInfoDialog(title,msg,"Rendben");
            return false;
        }

        return true;
    }

    @Override
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position) {

        int job_id = job_list.get(position).getId();

        /*if(dh.sql("CALL delete_job(" + job_id + ");")) {
            Toast.makeText(context, "A kijelölt munka törölve!", Toast.LENGTH_SHORT).show();
            job_list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, job_list.size());
        }
        else {
            Toast.makeText(context, "Törlés sikertelen!", Toast.LENGTH_SHORT).show();
        }*/

        if(!checkJobStatus(position, "Figyelmeztetés", "Nem törölhető, mert tartozik hozzá leadott víz!")) {
            return;
        }

        //TODO Ezt jó lenne megcsinálni TRANZAKCIÓval, mert így besülhet néha
        if(!dh.delete(dh.JAW ,"JobID = " + job_id)) {
            Toast.makeText(context, "A törlés sikertelen!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!dh.delete(dh.CIJ ,"JobID = " + job_id)) {
            Toast.makeText(context, "A törlés sikertelen!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!dh.delete(dh.JOBS,"ID = " + job_id)) {
            Toast.makeText(context, "A törlés sikertelen!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Munka törölve!", Toast.LENGTH_SHORT).show();
        job_list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, job_list.size());

    }

    @Override
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView job_name, job_date, income;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            job_name = itemView.findViewById(R.id.custom_job_name_gui);
            job_date = itemView.findViewById(R.id.custom_job_date_gui);
            item = itemView.findViewById(R.id.custom_job_item);
            income = itemView.findViewById(R.id.custom_job_income_gui);
        }

    }
}
