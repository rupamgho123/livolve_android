package com.hackday.livolve;

import android.app.Application;

import com.android.volley.toolbox.Volley;

public class LivolveApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Livolve.requestQueue = Volley.newRequestQueue(getApplicationContext());
	}
}
