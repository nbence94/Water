package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;

import nb.app.waterdelivery.adapters.ChosenCustomerWatersAdapter;
import nb.app.waterdelivery.adapters.EditJobChosenCustomerWatersAdapter;

public interface EditJobOnDialogTextChange {
    void onAlertDialogTextChange(@NonNull EditJobChosenCustomerWatersAdapter.ViewHolder holder, int position);
}
