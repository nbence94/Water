package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.AdminUserJobDetailsActivity;
import nb.app.waterdelivery.admin.AdminUserJobsActivity;
import nb.app.waterdelivery.admin.AllSettlementActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;

public class UserJobsChildAdapter extends RecyclerView.Adapter<UserJobsChildAdapter.ViewHolder> {

    private final String LOG_TITLE = "UserJobsChildAdapter";

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Settlement> settlement_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    AdminUserJobsActivity auja;
    SaveLocalDatas sld;

    public UserJobsChildAdapter(Context context, Activity activity, ArrayList<Settlement> settlement_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.settlement_list = settlement_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        auja = (AdminUserJobsActivity) context;
        sld = new SaveLocalDatas(activity);
    }

    @NonNull
    @Override
    public UserJobsChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_settlmentname_layout, parent, false);
        return new UserJobsChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJobsChildAdapter.ViewHolder holder, int position) {
        holder.checkbox.setVisibility(View.VISIBLE);

        Settlement settlement = settlement_list.get(position);
        holder.name.setText(settlement.getName());

        if(settlement_list.get(position).getId() < 1) holder.checkbox.setVisibility(View.GONE);
        holder.checkbox.setEnabled(false);
        if(settlement.getFinished() != null) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        holder.item.setOnClickListener(v -> {
            if(settlement_list.get(position).getId() > 0) {
                Intent jobs = new Intent(context, AdminUserJobDetailsActivity.class);
                jobs.putExtra("settlementid", settlement_list.get(position).getId());
                activity.startActivityForResult(jobs, 1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return settlement_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CardView item;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.custom_user_name_gui);
            item = itemView.findViewById(R.id.custom_user_item);
            checkbox = itemView.findViewById(R.id.settlement_checkbox);
        }
    }
}