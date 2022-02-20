package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import nb.app.waterdelivery.adapters.ChosenCustomerWatersAdapter;

public interface OnDialogTextChange {
    void onAlertDialogTextChange(@NonNull RecyclerView.ViewHolder holder, int position);
}
