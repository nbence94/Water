package nb.app.waterdelivery.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.control.AdminMonthSettlementActivity;
import nb.app.waterdelivery.data.CustomData;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;

public class AdminControlMonthAdapter extends RecyclerView.Adapter<AdminControlMonthAdapter.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<String> months_list;
    DatabaseHelper dh;
    AdminMonthSettlementActivity auja;

    AdminControlMonthChildAdapter adapter;
    public ArrayList<CustomData> customers_list;
    SaveLocalDatas sld;
    ArrayList<Boolean> expanded_list;

    public AdminControlMonthAdapter(Context context, Activity activity, ArrayList<String> m_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.months_list = m_list;
        dh = new DatabaseHelper(context, activity);
        auja = (AdminMonthSettlementActivity) context;

        sld = new SaveLocalDatas(activity);
        customers_list = new ArrayList<>();
        this.expanded_list = new ArrayList<>();
        for(int i = 0; i < m_list.size(); i++) {
            expanded_list.add(false);
        }
    }

    @NonNull
    @Override
    public AdminControlMonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_my_settlements_layout, parent, false);
        return new AdminControlMonthAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminControlMonthAdapter.ViewHolder holder, int position) {
        holder.name.setText(months_list.get(position));
        String[] month = months_list.get(position).split("\\.");
        showElements(holder, month[0], getMonthsNumber(month[1]));

        boolean kinyitva = expanded_list.get(position);
        holder.item.setVisibility(kinyitva ? View.VISIBLE : View.GONE);

        holder.card.setOnClickListener(v -> {
            expanded_list.set(position, !kinyitva);
            notifyItemChanged(position);
        });

        /*for(int i = 0 ; i < customers_list.size(); i++) {
            if(customers_list.get(i).getFinished() == null) {
                holder.attention.setVisibility(View.VISIBLE);
                break;
            }
        }*/

        holder.attention.setTooltipText("Vannak lezáratlan munkák!");
    }

    public void showElements(@NonNull AdminControlMonthAdapter.ViewHolder holder, String year, String month) {
        customers_list.clear();
        //TODO Ide nem is feltétlen Settlement kell!
        //dh.getSettlementData("SELECT * FROM " + dh.SETTLEMENT + " WHERE YEAR(Created) = " + year + " AND MONTH(Created) = " + month + ";", settlement_list);
        dh.getCustomData("SELECT c.ID, c.Fullname, SUM(jaw.WaterAmount * w.Price) AS Income\n" +
                "FROM customers c " +
                "JOIN customerinjob cij ON cij.CustomerID = c.ID " +
                "JOIN jobsinsettlement jis ON jis.JobID = cij.JobID " +
                "JOIN jobandwater jaw ON jaw.JobID = jis.JobID " +
                "JOIN waters w ON w.ID = jaw.WaterID " +
                "JOIN settlement s ON s.ID = jis.SettlementID " +
                "WHERE YEAR(s.Created) = " + year + " AND MONTH(s.Created) = " + month + " " +
                "GROUP BY c.ID", customers_list);
        int income = 0;
        for(int i = 0; i < customers_list.size(); i++) {
            income += customers_list.get(i).getNumber();
        }
        @SuppressLint("DefaultLocale") String result_cost = String.format("%,d", income).replace(",", " ");
        customers_list.add(new CustomData(-1, "Havi bevétel: " + result_cost + " Ft", 0));
        adapter = new AdminControlMonthChildAdapter(context,  activity, customers_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recycler.setLayoutManager(manager);
        holder.recycler.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return months_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ConstraintLayout item;
        RecyclerView recycler;
        CardView card;
        ImageView attention;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.settlement_name_gui);
            item = itemView.findViewById(R.id.expand_layout);
            recycler = itemView.findViewById(R.id.settlements_for_month_recycler);
            card = itemView.findViewById(R.id.my_settlements_carditem_gui);
            attention = itemView.findViewById(R.id.this_month_is_okay_gui);
        }
    }

    public String getMonthsNumber(String num_of_month) {
        switch (num_of_month) {
            case "Január": return "1";
            case "Február": return "2";
            case "Március": return "3";
            case "Április": return "4";
            case "Május": return "5";
            case "Június": return "6";
            case "Július": return "7";
            case "Augusztus": return "8";
            case "Szeptember": return "9";
            case "Október": return "10";
            case "November": return "11";
            case "December": return "12";
        }
        return "0";
    }
}
