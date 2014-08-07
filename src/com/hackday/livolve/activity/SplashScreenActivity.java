package com.hackday.livolve.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.Util;

public class SplashScreenActivity extends LivolveActivity{

	ViewGroup centerContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		centerContent = (ViewGroup)findViewById(R.id.centerContent);
		handler.postDelayed(goToLogin, 5 * Constants.MILLIS_SECOND);
		handler.postDelayed(animateContent, Constants.MILLIS_SECOND);
	}

	Runnable goToLogin = new Runnable() {

		@Override
		public void run() {
			if(Util.isUserLogin(getApplicationContext()))
				goTo(MainActivity.class);
			else
				goTo(LoginActivity.class);
		}
	};

	Runnable animateContent = new Runnable() {

		@Override
		public void run() {
			Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
			slide.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					centerContent.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}
			});
			slide.setFillAfter(true);
			centerContent.startAnimation(slide);
		}
	};

	@Override
	public boolean shouldExitOnNewActivityLaunch() {
		return true;
	}

	@Override
	public boolean shouldShowActionBar() {
		return false;
	}

	@Override
	public String getTag() {
		return "SplashScreenActivity";
	}
}
