package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import nb.app.waterdelivery.adapters.MyJobsAdapter;

public interface myWarningDialogChoice {
    void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position);
    void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position);
}
