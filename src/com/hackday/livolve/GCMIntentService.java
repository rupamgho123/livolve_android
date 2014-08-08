package com.hackday.livolve;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	protected GCMIntentService(String senderId) {
		super(senderId);
	}
	
	public GCMIntentService() {
        super("Test");
    }

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("","");
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d("","");
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d("","");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
