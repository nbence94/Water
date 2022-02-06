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

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.AdminUserJobDetailsActivity;
import nb.app.waterdelivery.admin.AdminUserJobsActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.CustomersInJob;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JobAndWaters;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;
import nb.app.waterdelivery.data.Waters;

public class UserJobsDetailsChildAdapter extends RecyclerView.Adapter<UserJobsDetailsChildAdapter.ViewHolder> {

    private final String LOG_TITLE = "UserJobsDetailsChildAdapter";

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Customers> customers_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    AdminUserJobDetailsActivity aujda;
    SaveLocalDatas sld;

    ArrayList<Waters> waters_list;
    ArrayList<JobAndWaters> jaw_list;
    ArrayList<CustomersInJob> cij_list;
    int job_id;

    public UserJobsDetailsChildAdapter(Context context, Activity activity, ArrayList<Customers> c_list, ArrayList<CustomersInJob> cij_list, ArrayList<Waters> waters_list, ArrayList<JobAndWaters> jaw_list, int job_id) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.customers_list = c_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        aujda = (AdminUserJobDetailsActivity) context;
        sld = new SaveLocalDatas(activity);

        this.waters_list = waters_list;
        this.jaw_list = jaw_list;
        this.cij_list = cij_list;
        this.job_id = job_id;
    }

    @NonNull
    @Override
    public UserJobsDetailsChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_settlmentname_layout, parent, false);
        return new UserJobsDetailsChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJobsDetailsChildAdapter.ViewHolder holder, int position) {
        holder.checkbox.setVisibility(View.GONE);

        Customers customer = customers_list.get(position);
        holder.name.setText(customer.getFullname());

        holder.item.setOnClickListener(v -> {
            StringBuilder msg = new StringBuilder();

            for(int i = 0; i < cij_list.size(); i++) {
                    if (cij_list.get(i).getCustomerid() == customers_list.get(position).getId()) {
                        String[] date_array = cij_list.get(i).getFinish().split("\\.");
                        msg.append("SZÁLLÍTÁS LEADVA").append("\n").append(date_array[0]).append("\n").append("\n");
                        break;
                }
            }

            msg.append("LEADOTT VIZEK").append("\n");
            int income = 0;
            for(int i = 0; i < waters_list.size(); i++) {
                for(int j = 0; j < jaw_list.size(); j++) {
                    if(jaw_list.get(j).getCustomerid() == customers_list.get(position).getId()) {
                        if(jaw_list.get(j).getWaterid() == waters_list.get(i).getId()) {
                            msg.append(waters_list.get(i).getName()).append(": ").append(jaw_list.get(j).getWateramount()).append(" db").append("\n");
                            income += jaw_list.get(j).getWateramount() * waters_list.get(i).getPrice();
                        }
                    }
                }
            }

            msg.append("\n").append("BEVÉTEL").append("\n").append(income).append(" Ft");

            mad.AlertInfoDialog("További információ", msg.toString(),"Rendben");
        });
    }

    @Override
    public int getItemCount() {
        return customers_list.size();
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
