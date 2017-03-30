package com.growthbeat.message.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.model.PlainButton;
import com.growthbeat.message.model.PlainMessage;

public class PlainMessageFragment extends DialogFragment {

    private PlainMessage plainMessage = null;

    public PlainMessageFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
        } catch (Exception e) {
            getActivity().finish();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Object message = getArguments().get("message");
        if (message == null)
            return null;
        if (!(message instanceof PlainMessage))
            return null;

        this.plainMessage = (PlainMessage) message;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle(plainMessage.getCaption());

        if (plainMessage.getButtons() == null)
            return null;

        final PlainButton positiveButton;
        final PlainButton negativeButton;

        switch (plainMessage.getButtons().size()) {
            case 1:
                positiveButton = (PlainButton) plainMessage.getButtons().get(0);
                negativeButton = null;
                break;
            case 2:
                positiveButton = (PlainButton) plainMessage.getButtons().get(0);
                negativeButton = (PlainButton) plainMessage.getButtons().get(1);
                break;
            default:
                return null;
        }

        dialogBuilder.setMessage(plainMessage.getText());
        if (positiveButton != null) {
            dialogBuilder.setPositiveButton(positiveButton.getLabel(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GrowthMessage.getInstance().selectButton(positiveButton, plainMessage);
                    if (!getActivity().isFinishing())
                        getActivity().finish();
                }
            });
        }

        if (negativeButton != null) {
            dialogBuilder.setNegativeButton(negativeButton.getLabel(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GrowthMessage.getInstance().selectButton(negativeButton, plainMessage);
                    if (!getActivity().isFinishing())
                        getActivity().finish();
                }
            });
        }

        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null && !getActivity().isFinishing())
            getActivity().finish();
    }

}
