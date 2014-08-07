package com.hackday.livolve.activity;

public interface IActivity {
	boolean shouldExitOnNewActivityLaunch();
	boolean shouldShowActionBar();
	String getTag();
}
