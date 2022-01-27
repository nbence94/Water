package nb.app.waterdelivery.alertdialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import nb.app.waterdelivery.R;
import nb.app.waterdelivery.adapters.ChosenCustomerWatersAdapter;
import nb.app.waterdelivery.adapters.ChosenCustomersAdapter;
import nb.app.waterdelivery.adapters.EditJobChosenCustomerAdapter;
import nb.app.waterdelivery.adapters.EditJobChosenCustomerWatersAdapter;
import nb.app.waterdelivery.adapters.MultiSelectAdapter;
import nb.app.waterdelivery.adapters.SingleSelectAdapter;

public class MyAlertDialog {

    Context context;
    Activity activity;
    MultiSelectAdapter multi_adapter;
    SingleSelectAdapter single_adapter;

    //All
    Button yes_button, no_button;
    TextView dialog_title, dialog_message;
    ImageView icon;
    EditText input_text;
    public boolean closeable = true;

    //SelectAlertDialog
    RecyclerView recycler;
    public boolean[] selected_array;

    //EditText Alert dialog
    public String result_text;

    public MyAlertDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public MyAlertDialog(Context context, Activity activity, int length) {
        this.context = context;
        this.activity = activity;

        this.selected_array = new boolean[length];
    }

    public void AlertInfoDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_info_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((TextView) view.findViewById(R.id.textMessage)).setTextSize(20);
        ((Button) view.findViewById(R.id.button)).setText(button_title);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.info_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    @SuppressLint("CutPasteId")
    public void AlertInputDialog(String title, String message, String button_title, int position, @NonNull ChosenCustomerWatersAdapter.ViewHolder holder, int input_type, final OnDialogTextChange listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_edittext_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        this.yes_button = view.findViewById(R.id.button);
        this.icon = view.findViewById(R.id.imageIcon);
        this.dialog_title = view.findViewById(R.id.textTitle);
        this.input_text = view.findViewById(R.id.textMessage);

        switch (input_type) {
            case 0:
                input_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                break;
            case 1:
                input_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                break;
            default:
                Log.e("MyAlertDialog", "Ilyen típus nincs! 0: szöveg 1: szám");
                break;
        }

        yes_button.setText(button_title);
        icon.setImageResource(R.drawable.info_icon);
        dialog_title.setText(title);
        input_text.setText(message);
        input_text.setTextSize(20);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            result_text = ((EditText) view.findViewById(R.id.textMessage)).getText().toString();
            listener.onAlertDialogTextChange(holder, position);

            if(closeable) alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void editJobAlertInputDialog(String title, String message, String button_title, int position, @NonNull EditJobChosenCustomerWatersAdapter.ViewHolder holder, int input_type, final EditJobOnDialogTextChange listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_edittext_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        this.yes_button = view.findViewById(R.id.button);
        this.icon = view.findViewById(R.id.imageIcon);
        this.dialog_title = view.findViewById(R.id.textTitle);
        this.input_text = view.findViewById(R.id.textMessage);

        switch (input_type) {
            case 0:
                input_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                break;
            case 1:
                input_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                break;
            default:
                Log.e("MyAlertDialog", "Ilyen típus nincs! 0: szöveg 1: szám");
                break;
        }

        yes_button.setText(button_title);
        icon.setImageResource(R.drawable.info_icon);
        dialog_title.setText(title);
        input_text.setText(message);
        input_text.setTextSize(20);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            result_text = ((EditText) view.findViewById(R.id.textMessage)).getText().toString();
            listener.onAlertDialogTextChange(holder, position);

            if(closeable) alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    //Eredeti
    public void AlertMultiSelectDialog(String title, String[] elements, boolean[] selected_elements, boolean[] tmp_selected_elements, String buttonYes, String ButtonNo, @NonNull ChosenCustomersAdapter.ViewHolder holder, int position, final OnDialogChoice listener) {
        this.multi_adapter = new MultiSelectAdapter(context, activity, elements, selected_elements);
        this.selected_array = new boolean[selected_elements.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_multiselect_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);
        this.recycler = my_view.findViewById(R.id.elements);
        this.yes_button = my_view.findViewById(R.id.buttonYes);
        this.no_button = my_view.findViewById(R.id.buttonNo);
        this.icon = my_view.findViewById(R.id.imageIcon);
        this.dialog_title = my_view.findViewById(R.id.textTitle);

        //Megjelenő elemek
        dialog_title.setText(title);
        yes_button.setText(buttonYes);
        no_button.setText(ButtonNo);
        icon.setImageResource(R.drawable.select_icon);

        final AlertDialog alertDialog = builder.create();

        System.arraycopy(selected_elements, 0, tmp_selected_elements, 0, selected_elements.length);

        multi_adapter = new MultiSelectAdapter(context, activity, elements, selected_elements);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(multi_adapter);

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            for(int i = 0; i < selected_array.length; i++) {
                selected_elements[i] = multi_adapter.selected_items[i];
            }
            listener.OnPositiveClick(holder, position);
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            listener.OnNegativeClick(holder, position);
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void editJobMultiSelect(String title, String[] elements, boolean[] selected_elements, boolean[] tmp_selected_elements, String buttonYes, String ButtonNo, @NonNull EditJobChosenCustomerAdapter.ViewHolder holder, int position, final EditJobOnDialogChoice listener) {
        this.multi_adapter = new MultiSelectAdapter(context, activity, elements, selected_elements);
        this.selected_array = new boolean[selected_elements.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_multiselect_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);
        this.recycler = my_view.findViewById(R.id.elements);
        this.yes_button = my_view.findViewById(R.id.buttonYes);
        this.no_button = my_view.findViewById(R.id.buttonNo);
        this.icon = my_view.findViewById(R.id.imageIcon);
        this.dialog_title = my_view.findViewById(R.id.textTitle);

        //Megjelenő elemek
        dialog_title.setText(title);
        yes_button.setText(buttonYes);
        no_button.setText(ButtonNo);
        icon.setImageResource(R.drawable.select_icon);

        final AlertDialog alertDialog = builder.create();

        System.arraycopy(selected_elements, 0, tmp_selected_elements, 0, selected_elements.length);

        multi_adapter = new MultiSelectAdapter(context, activity, elements, selected_elements);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(multi_adapter);

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            for(int i = 0; i < selected_array.length; i++) {
                selected_elements[i] = multi_adapter.selected_items[i];
            }
            listener.OnPositiveClick(holder, position);
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            listener.OnNegativeClick(holder, position);
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    public void AlertSingleSelectDialog(String title, String[] elements, boolean[] selected_element, boolean[] tmp_selected_element, String buttonYes, String ButtonNo, @NonNull ChosenCustomersAdapter.ViewHolder holder, int position, final OnDialogChoice listener) {
        this.single_adapter = new SingleSelectAdapter(context, activity, elements, selected_element);
        this.selected_array = new boolean[selected_element.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_multiselect_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);
        this.recycler = my_view.findViewById(R.id.elements);
        this.yes_button = my_view.findViewById(R.id.buttonYes);
        this.no_button = my_view.findViewById(R.id.buttonNo);
        this.icon = my_view.findViewById(R.id.imageIcon);
        this.dialog_title = my_view.findViewById(R.id.textTitle);

        //Megjelenő elemek
        dialog_title.setText(title);
        yes_button.setText(buttonYes);
        no_button.setText(ButtonNo);
        icon.setImageResource(R.drawable.select_icon);

        final AlertDialog alertDialog = builder.create();

        System.arraycopy(selected_element, 0, tmp_selected_element, 0, selected_element.length);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(single_adapter);

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            for(int i = 0; i < selected_array.length; i++) {
                selected_element[i] = single_adapter.selected_item[i];
            }
            listener.OnPositiveClick(holder, position);
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            listener.OnNegativeClick(holder, position);
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void AlertSuccesDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_success_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.button)).setText(button_title);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.done_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void AlertErrorDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_error_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.button)).setText(button_title);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.error_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    /*public void AlertWarningDialog(String title, String message, String buttonYes, String ButtonNo, @NonNull MyJobsAdapter.ViewHolder holder, int position, final WarningDialogChoice listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_warning_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);

        ((TextView) my_view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) my_view.findViewById(R.id.textMessage)).setText(message);
        ((Button) my_view.findViewById(R.id.buttonYes)).setText(buttonYes);
        ((Button) my_view.findViewById(R.id.buttonNo)).setText(ButtonNo);
        ((ImageView) my_view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning_icon);

        final AlertDialog alertDialog = builder.create();

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            listener.OnPositiveClick(holder, position);
            alertDialog.dismiss();
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            listener.OnNegativeClick(holder, position);
            alertDialog.dismiss();

        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }*/

    public void myWarningDialog(String title, String message, String buttonYes, String ButtonNo, @NonNull RecyclerView.ViewHolder holder, int position, final myWarningDialogChoice listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_warning_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);

        ((TextView) my_view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) my_view.findViewById(R.id.textMessage)).setText(message);
        ((Button) my_view.findViewById(R.id.buttonYes)).setText(buttonYes);
        ((Button) my_view.findViewById(R.id.buttonNo)).setText(ButtonNo);
        ((ImageView) my_view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning_icon);

        final AlertDialog alertDialog = builder.create();

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            listener.OnPositiveClick(holder, position);
            alertDialog.dismiss();
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            listener.OnNegativeClick(holder, position);
            alertDialog.dismiss();

        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}
