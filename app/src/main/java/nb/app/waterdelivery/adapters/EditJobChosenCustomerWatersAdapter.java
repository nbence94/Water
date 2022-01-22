package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.EditJobOnDialogTextChange;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.alertdialog.OnDialogTextChange;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.JawDraft;
import nb.app.waterdelivery.data.Waters;
import nb.app.waterdelivery.jobs.EditMyJobActivity;

public class EditJobChosenCustomerWatersAdapter extends RecyclerView.Adapter<EditJobChosenCustomerWatersAdapter.ViewHolder> implements EditJobOnDialogTextChange {

    LayoutInflater inflater;
    ArrayList<Waters> waters_list;
    ArrayList<JawDraft> draft_list;

    MyAlertDialog mad;
    EditMyJobActivity emja;
    DatabaseHelper dh;

    Context context;
    Activity activity;
    int index;
    int water_id, customer_id, user_id;

    public EditJobChosenCustomerWatersAdapter(Context context, Activity activity, ArrayList<Waters> waters_data, ArrayList<JawDraft> draft_list) {
        this.inflater = LayoutInflater.from(context);
        this.waters_list = waters_data;
        this.draft_list = draft_list;
        this.context = context;
        this.activity = activity;
        this.index = 0;

        mad = new MyAlertDialog(context, activity);
        this.emja = (EditMyJobActivity) context;
        this.dh = new DatabaseHelper(context, activity);
    }

    @NonNull
    @Override
    public EditJobChosenCustomerWatersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_water_for_customer_layout, parent, false);
        return new EditJobChosenCustomerWatersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditJobChosenCustomerWatersAdapter.ViewHolder holder, int position) {
        int water_amount = draft_list.get(position).getWater_amount();
        holder.water_amount.setText(String.valueOf(water_amount));
        calculateCost(holder, position);

        holder.item.setOnClickListener(v -> {
            customer_id = draft_list.get(position).getCustomerid();
            water_id = draft_list.get(position).getWaterid();
            mad.editJobAlertInputDialog("Kért víz mennyisége", String.valueOf(water_amount), "Rendben", position, holder,1, this);
        });
    }

    private void calculateCost(@NonNull EditJobChosenCustomerWatersAdapter.ViewHolder holder, int position) {
        for(int i = 0; i < waters_list.size(); i++) {
            if(waters_list.get(i).getId() == draft_list.get(position).getWaterid()) {
                holder.water_name.setText(waters_list.get(i).getName());
                holder.water_cost.setText(String.valueOf(waters_list.get(i).getPrice() * draft_list.get(position).getWater_amount()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return draft_list.size();
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
    public void onAlertDialogTextChange(@NonNull ViewHolder holder, int position) {
        if(!isNumeric(mad.result_text)) {
            Toast.makeText(context, "A beírt érték nem megfelelő!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(mad.result_text) < 1 || Integer.parseInt(mad.result_text) > 1000 ) {
            Toast.makeText(context, "A vizek száma 0 és 1000 közötti lehet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!dh.sql("UPDATE " + dh.EDITDRAFT + " SET WaterAmount = " + mad.result_text + " WHERE WaterID=" + water_id + " AND JobID= " + emja.job_id + " AND CustomerID=" + customer_id + ";")) {
            Toast.makeText(context, "Sikertelen módosítás", Toast.LENGTH_SHORT).show();
        }

        draft_list.get(position).setWater_amount(Integer.parseInt(mad.result_text));
        calculateCost(holder, position);
        emja.calculateGlobalIncome();
        emja.showCustomersInRecyclerView();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView item;
        TextView water_name, water_cost, water_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.custom_water_item);
            water_name = itemView.findViewById(R.id.custom_water_name_gui);
            water_cost = itemView.findViewById(R.id.custom_water_cost_gui);
            water_amount = itemView.findViewById(R.id.custom_water_amount_gui);

        }
    }
}