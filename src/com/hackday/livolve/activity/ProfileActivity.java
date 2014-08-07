package com.hackday.livolve.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;

public class ProfileActivity extends LivolveActivity{

	TextView name;
	TextView email;
	TextView role;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		
		name = (TextView) findViewById(R.id.name);
		email = (TextView) findViewById(R.id.email);
		role = (TextView) findViewById(R.id.role);
		
		SharedPreferences pref = getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		name.setText(pref.getString(Constants.NAME, ""));
		email.setText(pref.getString(Constants.EMAIL, ""));
		role.setText(pref.getString(Constants.ROLE, ""));
	}
	
	
	@Override
	public boolean shouldExitOnNewActivityLaunch() {
		return false;
	}

	@Override
	public boolean shouldShowActionBar() {
		return true;
	}

	@Override
	public String getTag() {
		return "ProfileActivity";
	}

}
