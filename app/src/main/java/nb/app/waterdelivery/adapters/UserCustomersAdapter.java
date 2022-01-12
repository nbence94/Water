package nb.app.waterdelivery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.Customers;
import nb.app.waterdelivery.data.Jobs;

public class UserCustomersAdapter extends RecyclerView.Adapter<UserCustomersAdapter.ViewHolder> implements Filterable {

    Context context;
    LayoutInflater inflater;

    ArrayList<Customers> data_list;
    private final ArrayList<Customers> search_data_list;
    ArrayList<Customers> result_data_list;

    public UserCustomersAdapter (Context context, ArrayList<Customers> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.data_list = list;
        this.search_data_list = new ArrayList<>(data_list);
    }

    @NonNull
    @Override
    public UserCustomersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_admin_usercustomers_layout, parent, false);
        return new UserCustomersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCustomersAdapter.ViewHolder holder, int position) {
        holder.name.setText(data_list.get(position).getFullname());
        holder.city.setText(data_list.get(position).getCity());

        holder.item.setOnLongClickListener(v -> {
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.custom_user_customer_name_gui);
            city = itemView.findViewById(R.id.custom_user_customer_city_gui);
            item = itemView.findViewById(R.id.custom_user_customers_item);
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
