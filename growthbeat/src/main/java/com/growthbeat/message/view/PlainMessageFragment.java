package com.growthbeat.message.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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

        if (plainMessage.getButtons().size() < 3) {
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
        } else {
            String[] items = {positiveButton.getLabel(), neutralButton.getLabel(), negativeButton.getLabel()};

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            ScrollView scrollView = new ScrollView(getActivity());
            TextView textView = new TextView(getActivity());
            textView.setText(plainMessage.getText());
            scrollView.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

            layout.addView(scrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
            final ListView listView = new ListView(getActivity());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            GrowthMessage.getInstance().selectButton(positiveButton, plainMessage);
                            break;
                        case 1:
                            GrowthMessage.getInstance().selectButton(neutralButton, plainMessage);
                            break;
                        case 2:
                            GrowthMessage.getInstance().selectButton(negativeButton, plainMessage);
                            break;
                        default:
                            break;
                    }
                    if (!getActivity().isFinishing())
                        getActivity().finish();
                }
            });
            layout.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

            dialogBuilder.setView(layout);
        }

        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        return dialog;

    }

}
