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

		final PlainMessage plainMessage = (PlainMessage) message;

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

		dialogBuilder.setTitle(plainMessage.getCaption());
		dialogBuilder.setMessage(plainMessage.getText());

		if (plainMessage.getButtons() == null)
			return null;

		final PlainButton positiveButton;
		final PlainButton neutralButton;
		final PlainButton negativeButton;

		switch (plainMessage.getButtons().size()) {
		case 1:
			positiveButton = (PlainButton) plainMessage.getButtons().get(0);
			neutralButton = null;
			negativeButton = null;
			break;
		case 2:
			positiveButton = (PlainButton) plainMessage.getButtons().get(0);
			neutralButton = null;
			negativeButton = (PlainButton) plainMessage.getButtons().get(1);
			break;
		case 3:
			positiveButton = (PlainButton) plainMessage.getButtons().get(0);
			neutralButton = (PlainButton) plainMessage.getButtons().get(1);
			negativeButton = (PlainButton) plainMessage.getButtons().get(2);
			break;
		default:
			return null;
		}

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

		if (neutralButton != null) {
			dialogBuilder.setNeutralButton(neutralButton.getLabel(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GrowthMessage.getInstance().selectButton(neutralButton, plainMessage);
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
		dialog.setCanceledOnTouchOutside(false);

		return dialog;

	}

}
