package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;

import nb.app.waterdelivery.adapters.ChosenCustomerWatersAdapter;

public interface OnDialogTextChange {
    void onAlertDialogTextChange(@NonNull ChosenCustomerWatersAdapter.ViewHolder holder, int position);
}
