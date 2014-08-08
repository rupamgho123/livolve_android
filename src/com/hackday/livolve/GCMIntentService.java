package com.hackday.livolve;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.Util;

public class GCMIntentService extends GCMBaseIntentService{

	protected GCMIntentService(String senderId) {
		super(senderId);
	}
	
	public GCMIntentService() {
        super("Test");
    }

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("Error",arg1);
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Bundle extras = arg1.getExtras();
		
		Intent intent = new Intent();
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setAction(Constants.CONVERSATION_ACTION);
		intent.putExtras(extras);
		sendBroadcast(intent);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Util.register(arg0,arg1);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}
}
