package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.waters.EditWaterActivity;

public class AllWatersAdapter extends RecyclerView.Adapter<AllWatersAdapter.ViewHolder>{

    LayoutInflater inflater;
    ArrayList<Waters> data_list;

    Context context;
    Activity activity;

    SaveLocalDatas sld;

    public AllWatersAdapter(Context context, Activity activity, ArrayList<Waters> waters_data) {
        this.inflater = LayoutInflater.from(context);
        this.data_list = waters_data;
        this.context = context;
        this.activity = activity;
        this.sld = new SaveLocalDatas(activity);
    }


    @NonNull
    @Override
    public AllWatersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_water_layout, parent, false);
        return new AllWatersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllWatersAdapter.ViewHolder holder, int position) {
        holder.name_field.setText(data_list.get(position).getName());
        String cost = data_list.get(position).getPrice() + " Ft/ballon";
        holder.cost_field.setText(cost);

        holder.item.setOnClickListener(v -> {
            if(sld.loadUserRoleID() < 3) {
                return;
            }
            //activity.finish();
            Intent edit_water = new Intent(context, EditWaterActivity.class);
            edit_water.putExtra("water_id", data_list.get(position).getId());
            edit_water.putExtra("water_name", data_list.get(position).getName());
            edit_water.putExtra("water_cost", data_list.get(position).getPrice());
            activity.startActivityForResult(edit_water, 1);
        });
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_field, cost_field;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_field = itemView.findViewById(R.id.custom_water_name_gui);
            cost_field = itemView.findViewById(R.id.custom_water_cost_gui);
            item = itemView.findViewById(R.id.custom_water_item);
        }
    }
}
