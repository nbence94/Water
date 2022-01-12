package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import nb.app.waterdelivery.admin.AdminCheckUserActivity;
import nb.app.waterdelivery.data.Users;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    ArrayList<Users> data_list;

    private final ArrayList<Users> search_data_list;
    ArrayList<Users> result_data_list;

    Context context;
    Activity activity;

    public AllUsersAdapter(Context context, Activity activity, ArrayList<Users> users_data) {
        this.inflater = LayoutInflater.from(context);
        this.data_list = users_data;
        this.context = context;
        this.activity = activity;

        this.search_data_list = new ArrayList<>(data_list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_allusers_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name_text.setText(data_list.get(position).getName());

        holder.item.setOnClickListener(v -> {
            //activity.finish();
            Intent admin_user_details_screen = new Intent(context, AdminCheckUserActivity.class);
            admin_user_details_screen.putExtra("user_id", data_list.get(position).getId());
            activity.startActivityForResult(admin_user_details_screen, 1);
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_text;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.custom_user_name_gui);
            item = itemView.findViewById(R.id.custom_user_item);
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
                    if(search_data_list.get(i).getName().toLowerCase().contains(searchField.toString().toLowerCase())) {
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
