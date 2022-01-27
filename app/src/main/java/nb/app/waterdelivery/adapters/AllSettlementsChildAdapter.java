package nb.app.waterdelivery.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.admin.AllSettlementActivity;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.SaveLocalDatas;
import nb.app.waterdelivery.data.Settlement;

public class AllSettlementsChildAdapter extends RecyclerView.Adapter<AllSettlementsChildAdapter.ViewHolder> {

    private final String LOG_TITLE = "AllSettlementsAdapter";

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Settlement> settlement_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    AllSettlementActivity asa;
    SaveLocalDatas sld;

    public AllSettlementsChildAdapter(Context context, Activity activity, ArrayList<Settlement> settlement_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.settlement_list = settlement_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        asa = (AllSettlementActivity) context;
        sld = new SaveLocalDatas(activity);
    }

    @NonNull
    @Override
    public AllSettlementsChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_settlmentname_layout, parent, false);
        return new AllSettlementsChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllSettlementsChildAdapter.ViewHolder holder, int position) {
        holder.checkbox.setVisibility(View.VISIBLE);

        Settlement settlement = settlement_list.get(position);
        holder.name.setText(settlement.getName());

        if( settlement.getFinished() != null) {
            holder.checkbox.setChecked(true);
            holder.checkbox.setEnabled(false);
        } else {
            holder.checkbox.setChecked(false);
            holder.checkbox.setEnabled(true);
        }

        holder.checkbox.setOnClickListener(v ->{
            if(holder.checkbox.isChecked()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String finish_date = sdf.format(new Date());
                int id = settlement_list.get(position).getId();

                if(!dh.sql("UPDATE " + dh.SETTLEMENT + " SET Finished = '" + finish_date + "', finisherid='" + sld.loadUserID() + "' WHERE ID = " + id)) {
                    return;
                }

                holder.checkbox.setEnabled(false);
            }
        });

        holder.item.setOnClickListener(v -> {
            StringBuilder dialog_message = new StringBuilder();

            //dialog_message.append("Munkák száma: ").append(dh.getExactInt("SELECT COUNT(*) FROM jobsinsettlement WHERE settlementID = " + settlement.getId())).append("\n");
            //dialog_message.append("Megrendelők száma: ").append(dh.getExactInt("SELECT COUNT(*) FROM customerinjob cij, jobsinsettlement jis WHERE cij.JobID = jis.JobID AND settlementid = " + settlement.getId())).append("\n").append("\n");
            dialog_message.append("Befolyt összeg megrendelőnként:").append("\n");
            dialog_message.append(getItems("SELECT c.Fullname, SUM(jaw.WaterAmount * w.Price) AS Ár " +
                                                "FROM customers c, jobandwater jaw, waters w, jobsinsettlement jis " +
                                                "WHERE c.ID = jaw.CustomerID AND jaw.WaterID = w.ID AND jis.JobID = jaw.JobID " +
                                                "AND jis.SettlementID = " + settlement.getId() + " " +
                                                "GROUP BY c.Fullname", " Ft")).append("\n");

            dialog_message.append("Teljes bevétel: ")
                    .append(dh.getExactInt("SELECT SUM(w.Price * jaw.WaterAmount) As Ár " +
                                                "FROM jobsinsettlement jis, jobandwater jaw, waters w " +
                                                "WHERE jaw.JobID = jis.JobID AND w.ID = jaw.WaterID " +
                                                "AND settlementid = " + settlement.getId())).append(" Ft").append("\n").append("\n");

            dialog_message.append("Leadott vízmennyiség: ").append("\n");
            dialog_message.append(getItems("SELECT w.Name, SUM(jaw.WaterAmount) " +
                    "FROM customers c, jobandwater jaw, waters w, jobsinsettlement jis " +
                    "WHERE c.ID = jaw.CustomerID AND jaw.WaterID = w.ID AND jis.JobID = jaw.JobID " +
                    "AND jis.SettlementID = " + settlement.getId() +
                    " GROUP BY w.Name", " db")).append("\n");


            if(dh.getExactInt("SELECT finisherid FROM " + dh.SETTLEMENT + " WHERE ID = " + settlement.getId() + ";") != 0) {
                dialog_message.append(dh.getExactString("SELECT u.Fullname FROM settlement s, users u WHERE s.finisherID = u.ID AND s.ID = " + settlement.getId() + ";")).append(" jóváhagyta!");

                String date = dh.getExactString("SELECT Finished FROM " + dh.SETTLEMENT + " WHERE ID = " + settlement.getId() + ";");
                String[] date_array = date.split("\\.");
                dialog_message.append(date_array[0]);
            } else {
                dialog_message.append("Még nincs jóváhagyva!");
            }

            mad.AlertInfoDialog("Munka adatok",dialog_message.toString(), "Rendben");
        });

    }

    public String getItems(String select, String amount) {
        Connection con = dh.connectionClass(context);
        StringBuilder result = new StringBuilder();

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {
                    result.append(rs.getString(1)).append(": ").append(rs.getString(2)).append(" ").append(amount).append("\n");
                }
            }
            Log.i(LOG_TITLE, "SIKERES (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "SIKERTELEN (" + select + ")");
        }

        return result.toString();
    }

    @Override
    public int getItemCount() {
        return settlement_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CardView item;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.custom_user_name_gui);
            item = itemView.findViewById(R.id.custom_user_item);
            checkbox = itemView.findViewById(R.id.settlement_checkbox);
        }
    }
}