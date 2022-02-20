package nb.app.waterdelivery.alertdialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnDialogChoice {
    public void OnPositiveClick(@NonNull RecyclerView.ViewHolder holder, int position);
    public void OnNegativeClick(@NonNull RecyclerView.ViewHolder holder, int position);
}
