package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.adapters.MyJobsAdapter;

public interface WarningDialogChoice {
    void OnPositiveClick(@NonNull MyJobsAdapter.ViewHolder holder, int position);
    void OnNegativeClick(@NonNull MyJobsAdapter.ViewHolder holder, int position);
}
