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
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.alertdialog.MyAlertDialog;
import nb.app.waterdelivery.data.DatabaseHelper;
import nb.app.waterdelivery.data.Jobs;
import nb.app.waterdelivery.data.Settlement;
import nb.app.waterdelivery.jobs.MyJobsActivity;
import nb.app.waterdelivery.settlements.MySettlementsActivity;

public class MySettlementsAdapter extends RecyclerView.Adapter<MySettlementsAdapter.ViewHolder> {

    private final String LOG_TITLE = "MySettlementsAdapter";

    LayoutInflater inflater;
    Context context;
    Activity activity;
    ArrayList<Settlement> settlement_list;
    DatabaseHelper dh;
    MyAlertDialog mad;
    MySettlementsActivity mja;

    public MySettlementsAdapter(Context context, Activity activity, ArrayList<Settlement> settlement_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.settlement_list = settlement_list;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
        mja = (MySettlementsActivity) context;
    }

    @NonNull
    @Override
    public MySettlementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_settlmentname_layout, parent, false);
        return new MySettlementsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySettlementsAdapter.ViewHolder holder, int position) {
        Settlement settlement = settlement_list.get(position);
        holder.name.setText(settlement.getName());

        holder.item.setOnClickListener(v -> {
            StringBuilder dialog_message = new StringBuilder();

            dialog_message.append("Munkák száma: ").append(dh.getExactInt("SELECT COUNT(*) FROM jobsinsettlement WHERE settlementID = " + settlement.getId())).append("\n");
            dialog_message.append("Megrendelők száma: ").append(dh.getExactInt("SELECT COUNT(*) FROM customerinjob cij, jobsinsettlement jis WHERE cij.JobID = jis.JobID AND settlementid = " + settlement.getId())).append("\n").append("\n");
            dialog_message.append("Leadott vizek száma:").append("\n");
            dialog_message.append(getWaters("SELECT w.Name, SUM(WaterAmount) FROM jobsinsettlement jis, jobandwater jaw, waters w WHERE jaw.JobID = jis.JobID AND w.ID = jaw.WaterID AND settlementid = " + settlement.getId() + " GROUP BY w.Name")).append("\n");
            dialog_message.append("Teljes bevétel: ").append(dh.getExactInt("SELECT SUM(w.Price * jaw.WaterAmount) As Ár FROM jobsinsettlement jis, jobandwater jaw, waters w WHERE jaw.JobID = jis.JobID AND w.ID = jaw.WaterID AND settlementid = " + settlement.getId())).append(" Ft").append("\n").append("\n");

            if(dh.getExactInt("SELECT finisherid FROM " + dh.SETTLEMENT + " WHERE ID = " + settlement.getId() + ";") != 0) {
                dialog_message.append(dh.getExactString("SELECT u.Fullname FROM settlement s, users u WHERE s.finisherID = u.ID AND s.ID = " + settlement.getId() + ";")).append(" jóváhagyta!");
                dialog_message.append(dh.getExactString("SELECT Finished FROM " + dh.SETTLEMENT + " WHERE ID = " + settlement.getId() + ";"));
            } else {
                dialog_message.append("Még nincs jóváhagyva!");
            }

            //TODO Kiírni, hogy:
            //TODO Hány munkából áll
            //TODO Hány megrendelő van hozzá
            //TODO Az összebevétel
            //TODO Mennyi víz lett teadva vele
            //TODO Jóvá lett hagyva, ki hagyta jóvá

            mad.AlertInfoDialog("Munka adatok",dialog_message.toString(), "Rendben");
        });

    }

    public String getWaters(String select) {
        Connection con = dh.connectionClass(context);
        StringBuilder result = new StringBuilder();

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {
                    result.append(rs.getString(1)).append(": ").append(rs.getString(2)).append(" db").append("\n");
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.custom_user_name_gui);
            item = itemView.findViewById(R.id.custom_user_item);
        }
    }
}
