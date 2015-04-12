package com.yoolean.hotspot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.yoolean.client.HotSpotClient;
import com.yoolean.common.model.HotSpot;

/**
 * Created by chenhang on 2015/4/11.
 */
public class NewHotSpotDialogFragment extends DialogFragment implements
        DialogInterface.OnClickListener {
    private View dialog = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog =
                getActivity().getLayoutInflater()
                        .inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return (builder.setTitle(R.string.create_new_hotspot_name).setView(dialog)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        HotSpot hotSpot = new HotSpot();
        String owner = getValueOfTextView(R.id.hot_spot_owner_value);
        hotSpot.setOwner(owner);
        String title = getValueOfTextView(R.id.hotspot_title_value);
        hotSpot.setTitle(title);
        String description = getValueOfTextView(R.id.hotspot_desc_value);
        hotSpot.setDescription(description);
        hotSpot.setLocation(CachedLocation.get());

        HotSpotCreator creator=new HotSpotCreator(getString(R.string.yoolean_platform_url),hotSpot);
        creator.start();
    }

    private String getValueOfTextView(int id) {
        EditText name = (EditText) dialog.findViewById(id);
        return name.getText().toString();
    }

    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }

    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}
