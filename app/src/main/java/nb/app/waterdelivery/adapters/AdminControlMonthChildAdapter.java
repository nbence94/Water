package nb.app.waterdelivery.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.AdminMonthSettlementActivity;
import nb.app.waterdelivery.admin.users.AdminUserSettlementsActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.CustomData;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class AdminControlMonthChildAdapter extends RecyclerView.Adapter<AdminControlMonthChildAdapter.ViewHolder> {

    private final String LOG_TITLE = "UserJobsChildAdapter";

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<CustomData> customers_data_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    AdminMonthSettlementActivity auja;
    SaveLocalDatas sld;

    public AdminControlMonthChildAdapter(Context context, Activity activity, ArrayList<CustomData> settlement_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.customers_data_list = settlement_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        auja = (AdminMonthSettlementActivity) context;
        sld = new SaveLocalDatas(activity);
    }

    @NonNull
    @Override
    public AdminControlMonthChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_settlmentname_layout, parent, false);
        return new AdminControlMonthChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminControlMonthChildAdapter.ViewHolder holder, int position) {
        holder.checkbox.setVisibility(View.GONE);

        holder.name.setText(customers_data_list.get(position).getName());

        holder.item.setOnClickListener(v -> {
            if(customers_data_list.get(position).getId() > 0) {
                @SuppressLint("DefaultLocale") String result_cost = String.format("%,d Ft", customers_data_list.get(position).getNumber()).replace(",", " ");
                StringBuilder msg = new StringBuilder();
                msg.append("FIZETETT").append("\n");
                msg.append(result_cost);

                mad.AlertInfoDialog(customers_data_list.get(position).getName(), msg.toString(), "Rendben");
            }
        });

    }

    @Override
    public int getItemCount() {
        return customers_data_list.size();
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

