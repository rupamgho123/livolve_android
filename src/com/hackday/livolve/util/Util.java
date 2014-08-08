package com.hackday.livolve.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackday.livolve.Livolve;

public class Util {
	public static String getMessageFromVolleyError(VolleyError er){
		try
		{
			if(er.getCause().getMessage() != null)
				return er.getCause().getMessage();
			else if (er.networkResponse != null)
				return new String(er.networkResponse.data);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "UNKNOWN";
	}

	public static void saveUserCredentials(JSONObject jsonObject,
			Context applicationContext) throws JSONException{
		SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString(Constants.EMAIL,jsonObject.getString(Constants.EMAIL));
		edit.putString(Constants.ROLE,jsonObject.getString(Constants.ROLE));
		edit.putString(Constants.NAME,jsonObject.getString(Constants.NAME));
		edit.putString(Constants.ID,jsonObject.getString(Constants.ID));
		edit.putString(Constants.TEAM_ID,jsonObject.getString(Constants.TEAM_ID));
		edit.commit();
	}

	public static boolean isUserLogin(Context applicationContext){
		SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		return prefs.contains(Constants.EMAIL);
	}

	public static void clearData(Context applicationContext) {
		SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	public static String getUserId(Context applicationContext) {
		SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		return prefs.getString(Constants.ID, null);
	}

	public static String getTeamId(Context applicationContext) {
		SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.DEFAULT_SP, Context.MODE_PRIVATE);
		return prefs.getString(Constants.TEAM_ID, null);
	}

	public static void register(Context context, String registrationId){
		Livolve.requestQueue.add(new StringRequest(Request.Method.PUT, UrlConstants.getRegistrationUrl(Util.getUserId(context),registrationId),new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}));
	}
}
