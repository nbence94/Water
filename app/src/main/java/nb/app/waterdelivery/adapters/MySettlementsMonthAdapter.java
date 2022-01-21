package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;
import nb.app.waterdelivery.settlements.MySettlementsActivity;

public class MySettlementsMonthAdapter extends RecyclerView.Adapter<MySettlementsMonthAdapter.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<String> months_list;
    DatabaseHelper dh;
    MySettlementsActivity msa;

    MySettlementsAdapter adapter;
    ArrayList<Settlement> settlement_list;
    SaveLocalDatas sld;
    ArrayList<Boolean> expanded_list;

    public MySettlementsMonthAdapter(Context context, Activity activity, ArrayList<String> m_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.months_list = m_list;
        dh = new DatabaseHelper(context, activity);
        msa = (MySettlementsActivity) context;

        sld = new SaveLocalDatas(activity);
        settlement_list = new ArrayList<>();
        this.expanded_list = new ArrayList<>();
        for(int i = 0; i < m_list.size(); i++) {
            expanded_list.add(false);
        }
    }

    @NonNull
    @Override
    public MySettlementsMonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_my_settlements_layout, parent, false);
        return new MySettlementsMonthAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySettlementsMonthAdapter.ViewHolder holder, int position) {
        holder.name.setText(months_list.get(position));
        showElements(holder);

        boolean kinyitva = expanded_list.get(position);
        holder.item.setVisibility(kinyitva ? View.VISIBLE : View.GONE);

        holder.name.setOnClickListener(v -> {
            expanded_list.set(position, !kinyitva);
            notifyItemChanged(position);
        });
    }

    public void showElements(@NonNull MySettlementsMonthAdapter.ViewHolder holder) {
        settlement_list.clear();
        dh.getSettlementData("SELECT * FROM " + dh.SETTLEMENT + " WHERE UserID = " + sld.loadUserID() + ";", settlement_list);
        adapter = new MySettlementsAdapter(context,  activity, settlement_list);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.settlement_name_gui);
            item = itemView.findViewById(R.id.expand_layout);
            recycler = itemView.findViewById(R.id.settlements_for_month_recycler);
        }
    }
}
