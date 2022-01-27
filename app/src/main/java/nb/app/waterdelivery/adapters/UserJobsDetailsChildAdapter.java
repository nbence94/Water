package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;

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

    public UserJobsDetailsChildAdapter(Context context, Activity activity, ArrayList<Customers> c_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.customers_list = c_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        aujda = (AdminUserJobDetailsActivity) context;
        sld = new SaveLocalDatas(activity);
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
            mad.AlertInfoDialog(customer.getFullname(),"Ilyen vizeket kapott","Rendben");
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
