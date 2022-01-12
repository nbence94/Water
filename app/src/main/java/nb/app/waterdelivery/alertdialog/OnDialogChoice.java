package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;

import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;

public interface OnDialogChoice {
    public void OnPositiveClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position);
    public void OnNegativeClick(@NonNull ChosenCustomersAdapter.ViewHolder holder, int position);
}
