package com.hackday.livolve.activity;

import com.hackday.livolve.R;

import android.os.Bundle;

public class InvitesActivity extends LivolveActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invites);
	}
	
	@Override
	public boolean shouldExitOnNewActivityLaunch() {
		return false;
	}

	@Override
	public boolean shouldShowActionBar() {
		return false;
	}

	@Override
	public String getTag() {
		return "InvitesActivity";
	}

}
