package nb.app.waterdelivery.adapters;

import android.app.Activity;
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

import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.Customers;

public class MyCustomersAdapter extends RecyclerView.Adapter<MyCustomersAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    ArrayList<Customers> data_list;
    MyAlertDialog mad;
    Activity activity;

    private final ArrayList<Customers> search_data_list;
    ArrayList<Customers> result_data_list;

    Context context;

    public MyCustomersAdapter(Context context, Activity activity, ArrayList<Customers> list) {
        this.inflater = LayoutInflater.from(context);
        this.data_list = list;
        this.context = context;
        this.mad = new MyAlertDialog(context, activity);
        this.activity = activity;
        this.search_data_list = new ArrayList<>(data_list);
    }

    @NonNull
    @Override
    public MyCustomersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_my_customers_layout, parent, false);
        return new MyCustomersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCustomersAdapter.ViewHolder holder, int position) {
        holder.name_text.setText(data_list.get(position).getFullname());
        holder.city_text.setText(data_list.get(position).getCity());
        holder.address_text.setText(data_list.get(position).getAddress());
        holder.phone_text.setText(data_list.get(position).getPhone_one());

        holder.item.setOnClickListener(v -> {
            String telephone = (data_list.get(position).getPhone_two().equals("")) ? "-" : data_list.get(position).getPhone_two();

            StringBuilder data = new StringBuilder();
            data.append("Név: ").append(data_list.get(position).getFullname()).append("\n")
                    .append("Település: ").append(data_list.get(position).getCity()).append("\n")
                    .append("Cím: ").append(data_list.get(position).getAddress()).append("\n")
                    .append("\n")
                    .append("Elérhetőség:").append("\n")
                    .append("Telefon 1: ").append(data_list.get(position).getPhone_one()).append("\n")
                    .append("Telefon 2: ").append(telephone).append("\n")
                    .append("E-mail: ").append(data_list.get(position).getEmail()).append("\n")
                    .append("\n")
                    .append("További adatok:").append("\n");

            int week = data_list.get(position).getWater_weeks();

            if(week > 1) {
                data.append(week).append(" hetente kér vizet").append("\n");
            } else {
                data.append("Hetente kér vizet").append("\n");
            }

            if(data_list.get(position).getBill() == 1) data.append("Számlát kér!");

            mad.AlertInfoDialog("Adatok", data.toString(), "Rendben");
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_text, city_text, address_text, phone_text;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.my_customer_name_gui);
            city_text = itemView.findViewById(R.id.my_customer_city_gui);
            address_text = itemView.findViewById(R.id.my_customer_address_gui);
            phone_text = itemView.findViewById(R.id.my_customer_phone_gui);

            item = itemView.findViewById(R.id.custom_my_customer_item);
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
