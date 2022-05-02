package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.users.AdminCheckUserActivity;
import nb.app.waterdelivery.admin.AdminCustomerEditActivity;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.Users;

public class AllCustomersAdapter extends RecyclerView.Adapter<AllCustomersAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    ArrayList<Customers> data_list;

    private final ArrayList<Customers> search_data_list;
    ArrayList<Customers> result_data_list;

    Context context;
    Activity activity;

    public AllCustomersAdapter(Context context, Activity activity, ArrayList<Customers> list) {
        this.inflater = LayoutInflater.from(context);
        this.data_list = list;
        this.context = context;
        this.activity = activity;

        this.search_data_list = new ArrayList<>(data_list);
    }

    @NonNull
    @Override
    public AllCustomersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_allusers_layout, parent, false);
        return new AllCustomersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCustomersAdapter.ViewHolder holder, int position) {
        holder.name_text.setText(data_list.get(position).getFullname());
        if(data_list.get(position).getUserid() < 1) {
            holder.warning_icon.setVisibility(View.VISIBLE);
        } else {
            holder.warning_icon.setVisibility(View.GONE);
        }

        holder.warning_icon.setOnClickListener(v -> {
            Toast.makeText(context, "A megrendelő nincs rendelve senkihez!", Toast.LENGTH_SHORT).show();
        });

        holder.item.setOnClickListener(v -> {
            //activity.finish();
            Intent admin_customer_edit_screen = new Intent(context, AdminCustomerEditActivity.class);
            admin_customer_edit_screen.putExtra("customer_id", data_list.get(position).getId());
            admin_customer_edit_screen.putExtra("customer_name", data_list.get(position).getFullname());
            admin_customer_edit_screen.putExtra("customer_city", data_list.get(position).getCity());
            admin_customer_edit_screen.putExtra("customer_address", data_list.get(position).getAddress());
            admin_customer_edit_screen.putExtra("customer_email", data_list.get(position).getEmail());
            admin_customer_edit_screen.putExtra("customer_phone", data_list.get(position).getPhone_one());
            admin_customer_edit_screen.putExtra("customer_phoneplus", data_list.get(position).getPhone_two());
            admin_customer_edit_screen.putExtra("customer_ww", data_list.get(position).getWater_weeks());
            admin_customer_edit_screen.putExtra("customer_bill", data_list.get(position).getBill());
            admin_customer_edit_screen.putExtra("customer_comment", data_list.get(position).getComment());
            activity.startActivityForResult(admin_customer_edit_screen, 1);
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_text;
        CardView item;
        ImageView warning_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.custom_user_name_gui);
            item = itemView.findViewById(R.id.custom_user_item);
            warning_icon = itemView.findViewById(R.id.item_icon);
        }
    }

    @Override
    public Filter getFilter() {
        return searching;
    }

    Filter searching = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence searchField) {
            result_data_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_data_list.addAll(search_data_list);
            } else {
                for(int i = 0; i < search_data_list.size(); i++) {
                    if(search_data_list.get(i).getFullname().toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_data_list.add(search_data_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data_list.clear();
            data_list.addAll(result_data_list);
            notifyDataSetChanged();
        }
    };
}
