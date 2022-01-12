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
import nb.app.waterdelivery.data.Jobs;

public class UserJobsAdapter extends RecyclerView.Adapter<UserJobsAdapter.ViewHolder> implements Filterable {
    LayoutInflater inflater;
    Context context;

    ArrayList<Jobs> job_list;
    private final ArrayList<Jobs> search_data_list;
    ArrayList<Jobs> result_data_list;

    public UserJobsAdapter(Context context, ArrayList<Jobs> job_list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.job_list = job_list;
        this.search_data_list = new ArrayList<>(job_list);
    }

    @NonNull
    @Override
    public UserJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_admin_userjobs_layout, parent, false);
        return new UserJobsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJobsAdapter.ViewHolder holder, int position) {
        holder.name.setText(job_list.get(position).getName());
        holder.created.setText(job_list.get(position).getCreated());
        holder.finish.setText(job_list.get(position).getFinish());
        holder.income.setText(String.valueOf(job_list.get(position).getIncome()));

        holder.item.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, created, finish, income;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.custom_user_job_name_gui);
            created = itemView.findViewById(R.id.custom_user_job_created_gui);
            finish = itemView.findViewById(R.id.custom_user_job_finish_gui);
            income = itemView.findViewById(R.id.custom_user_job_profit_gui);
            item = itemView.findViewById(R.id.custom_user_job_item);
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
            job_list.clear();
            job_list.addAll(result_data_list);
            notifyDataSetChanged();
        }
    };
}
