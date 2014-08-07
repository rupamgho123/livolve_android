package com.hackday.livolve.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.hackday.livolve.DialogType;

public class LivolveDialogFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		String message = arguments.getString("message");
		DialogType type = DialogType.getTypeByID(arguments.getInt("id"));
		switch(type){
		case ERROR:{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(message);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return builder.create();
		}
		case PROGRESS:{
			ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage(message);
			return dialog;
		}
		case CONFIRMATION:{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(message);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return builder.create();
		}
		default:return super.onCreateDialog(savedInstanceState);
		}
	}
}
