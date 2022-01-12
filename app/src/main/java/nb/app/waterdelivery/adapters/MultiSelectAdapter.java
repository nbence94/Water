package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.R;

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    String[] items;
    public boolean[] selected_items;
    MyAlertDialog mad;

    public MultiSelectAdapter(Context context, Activity activity, String[] items, boolean[] selected_items) {
        this.context = context;
        this.activity = activity;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.selected_items = selected_items;
        this.mad = new MyAlertDialog(context, activity, items.length);
    }

    @NonNull
    @Override
    public MultiSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_multi_elements_layout, parent, false);
        return new MultiSelectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiSelectAdapter.ViewHolder holder, int position) {
        holder.checkBox.setText(items[position]);

        holder.checkBox.setChecked(selected_items[position]);
        holder.checkBox.setOnClickListener(v -> {
            selected_items[position] = holder.checkBox.isChecked();
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.item);
        }
    }
}
