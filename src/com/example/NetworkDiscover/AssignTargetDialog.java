package com.example.NetworkDiscover;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * User: jason
 * Date: 9/11/13
 */
public class AssignTargetDialog extends DialogFragment {

    public interface AssignTargetListener {
        public void onDialogTargetAssignClick(DialogFragment dialog, String ssid, String sharedKey);
        public void onDialogCancelClick(DialogFragment dialog);
    }

    AssignTargetListener mAssignTargetListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAssignTargetListener = (AssignTargetListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                        "must implement Listener in the activity" );
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_target, null);
        final EditText ssidInput = (EditText)dialogView.findViewById(R.id.ssid_input);
        final EditText sharedKeyInput = (EditText)dialogView.findViewById(R.id.shared_key_input);

        builder.setView(dialogView)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ssidString = ssidInput.getText().toString();
                    String sharedKeyString = sharedKeyInput.getText().toString();
                    mAssignTargetListener.onDialogTargetAssignClick(AssignTargetDialog.this, ssidString, sharedKeyString);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAssignTargetListener.onDialogCancelClick(AssignTargetDialog.this);
                }
            });
        return builder.create();
    }
}
