package nb.app.waterdelivery.alertdialog;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogHelper {

    public static void setMessage(Context context, String title, String message, String button_title) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        alertdialog.setTitle(title);
        
        alertdialog.setMessage(message);

        alertdialog.setPositiveButton(button_title, (dialog, whichButton) -> {
        });

        alertdialog.show();
    }

}
