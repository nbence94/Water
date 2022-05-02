package nb.app.waterdelivery.adapters.currentjob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogTextChange;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JobAndWaters;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.helper.NumberSplit;
import nb.app.waterdelivery.jobs.JobVisitActivity;

public class CurrentChosenJobChildAdapter extends RecyclerView.Adapter<CurrentChosenJobChildAdapter.ViewHolder> implements OnDialogTextChange {

    LayoutInflater inflater;
    ArrayList<Waters> waters_list;
    ArrayList<JobAndWaters> jaw_list;

    MyAlertDialog mad;
    JobVisitActivity jva;
    DatabaseHelper dh;

    Context context;
    Activity activity;
    int index;
    int water_id, customer_id, job_id;
    int closed;

    public CurrentChosenJobChildAdapter(Context context, Activity activity, ArrayList<Waters> waters_data, ArrayList<JobAndWaters> draft_list, int closed) {
        this.inflater = LayoutInflater.from(context);
        this.waters_list = waters_data;
        this.jaw_list = draft_list;
        this.context = context;
        this.activity = activity;
        this.index = 0;
        this.closed = closed;

        mad = new MyAlertDialog(context, activity);
        this.jva = (JobVisitActivity) context;
        this.dh = new DatabaseHelper(context, activity);
    }

    @NonNull
    @Override
    public CurrentChosenJobChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_waters_in_job_layout, parent, false);
        return new CurrentChosenJobChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentChosenJobChildAdapter.ViewHolder holder, int position) {

        int water_amount = jaw_list.get(position).getWateramount();

        //TODO Ezt ide tettem, majd teszteld, hogy jó-e
        int index = 0;
        for(int i = 0; i < waters_list.size(); i++) {
            if(waters_list.get(i).getId() == jaw_list.get(position).getWaterid()) {
                index = i;
                break;
            }
        }


        holder.water_name.setText(waters_list.get(index).getName());
        holder.water_amount.setText(String.valueOf(water_amount));

        calculateCost(holder, position);


        holder.settings_button.setEnabled(closed == 0);

        holder.settings_button.setOnClickListener(v -> {
            job_id = jaw_list.get(position).getJobid();
            customer_id = jaw_list.get(position).getCustomerid();
            water_id = jaw_list.get(position).getWaterid();
            mad.AlertInputDialog("Kért víz mennyisége", String.valueOf(water_amount), "Rendben", position, holder,1, this);
        });
    }

    private void calculateCost(@NonNull CurrentChosenJobChildAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < waters_list.size(); i++) {
            if(waters_list.get(i).getId() == jaw_list.get(position).getWaterid()) {
                holder.water_cost.setText(NumberSplit.splitNum(waters_list.get(i).getPrice() * jaw_list.get(position).getWateramount()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return jaw_list.size();
    }

    private static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public void onAlertDialogTextChange(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(!isNumeric(mad.result_text)) {
            Toast.makeText(context, "A beírt érték nem megfelelő!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(mad.result_text) < 1 || Integer.parseInt(mad.result_text) > 1000 ) {
            Toast.makeText(context, "A vizek száma 0 és 1000 közötti lehet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!dh.sql("UPDATE " + dh.JAW + " SET WaterAmount = " + mad.result_text + " WHERE WaterID=" + water_id + " AND JobID= " + job_id + " AND CustomerID=" + customer_id + ";")) {
            Toast.makeText(context, "Sikertelen módosítás", Toast.LENGTH_SHORT).show();
        }
        jaw_list.get(position).setWateramount(Integer.parseInt(mad.result_text));
        calculateCost((CurrentChosenJobChildAdapter.ViewHolder) holder, position);
        jva.loadIncomes();
        //jva.reload();
        jva.showElements();
        //TODO Talán az hatásos lenne, ha itt lenne egy metódus hívás, ami becsukja, aztán kinyitja az elemeket
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView item;
        TextView water_name, water_cost, water_amount;
        ImageView settings_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.custom_water_item);
            water_name = itemView.findViewById(R.id.custom_water_name_gui);
            water_cost = itemView.findViewById(R.id.custom_water_cost_gui);
            water_amount = itemView.findViewById(R.id.custom_water_amount_gui);
            settings_button = itemView.findViewById(R.id.imageView);

        }
    }
}
