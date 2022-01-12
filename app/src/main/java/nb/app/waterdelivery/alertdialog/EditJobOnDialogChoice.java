package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.adapters.EditJobChosenCustomerAdapter;

public interface EditJobOnDialogChoice {
    public void OnPositiveClick(@NonNull EditJobChosenCustomerAdapter.ViewHolder holder, int position);
    public void OnNegativeClick(@NonNull EditJobChosenCustomerAdapter.ViewHolder holder, int position);
}
