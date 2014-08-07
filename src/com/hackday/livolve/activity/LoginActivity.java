package com.hackday.livolve.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackday.livolve.DialogType;
import com.hackday.livolve.Livolve;
import com.hackday.livolve.R;
import com.hackday.livolve.util.UrlConstants;
import com.hackday.livolve.util.Util;

public class LoginActivity extends LivolveActivity{

	EditText username;
	EditText password;
	Button loginbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginbutton = (Button) findViewById(R.id.loginButton);
		loginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					initiateLogin(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public boolean shouldExitOnNewActivityLaunch() {
		return true;
	}

	@Override
	public boolean shouldShowActionBar() {
		return false;
	}

	public void initiateLogin(View v) throws Exception{
		String username = this.username.getText().toString();
		String password = this.password.getText().toString();

		if(username.length() > 4 && password.length() > 4){
			showMyDialog("Login in progress....", "login", DialogType.PROGRESS);
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("email", username);
			jsonRequest.put("password", password);
			Livolve.requestQueue.add(new JsonObjectRequest(Request.Method.POST, UrlConstants.getLoginUrl(), jsonRequest, new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					removeMyDialog("login");
					try {
						Util.saveUserCredentials(response,getApplicationContext());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					goTo(MainActivity.class);
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					removeMyDialog("login");
					showMyDialog(Util.getMessageFromVolleyError(error), "error", DialogType.ERROR);
				}
			}));
		}
		else{
			showMyDialog("wrong credentials", "error", DialogType.ERROR);
		}
	}

	@Override
	public String getTag() {
		return "LoginActivity";
	}
}
