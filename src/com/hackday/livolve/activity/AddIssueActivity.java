package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackday.livolve.DialogType;
import com.hackday.livolve.Livolve;
import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.UrlConstants;
import com.hackday.livolve.util.Util;

public class AddIssueActivity extends LivolveActivity{

	EditText title;
	EditText summary;
	List<User> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_issue);
		title = (EditText)findViewById(R.id.title);
		summary = (EditText)findViewById(R.id.summary);
		fetchFriends();
	}

	private class User{
		public User(int int1, String string) {
			this.id = int1;
			this.name = string; 
		}
		public long id;
		public CharSequence name;
	}

	private void fetchFriends() {
		String teamId = Util.getTeamId(getApplicationContext());
		Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getFriendsUrl(teamId), new Response.Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				list = new ArrayList<User>();
				for(int i=0;i<response.length();i++)
				{
					try {
						JSONObject object = (JSONObject) response.get(i);
						list.add(new User(object.getInt(Constants.ID),object.getString(Constants.NAME)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}));
	}

	static class ViewHolder {
		TextView name;
	}

	private class MyListAdapter extends BaseAdapter{

		private List<User> list;
		MyListAdapter(List<User> list)
		{
			this.list = list;
		}

		MyListAdapter()
		{
			this.list = new ArrayList<User>();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return list.get(arg0).id;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
			{
				arg1 = getLayoutInflater().inflate(R.layout.friend_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.name = (TextView) arg1.findViewById(R.id.name);
				arg1.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			User item = (User)getItem(arg0);
			viewHolder.name.setText(item.name);
			return arg1;
		}

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
		return "AddIssueActivity";
	}

	public void submit(View v) throws JSONException{
		showMyDialog("Adding issue", "add_issue", DialogType.PROGRESS);
		JSONObject body = new JSONObject();
		body.put("user_id",Util.getUserId(getApplicationContext()));
		body.put("title",title.getText().toString());
		body.put("summary",summary.getText().toString());
		Livolve.requestQueue.add(new JsonObjectRequest(UrlConstants.addIssueUrl(), body, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				removeMyDialog("add_issue");
				try {
					inviteFriends(response.getString(Constants.ID));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				removeMyDialog("add_issue");
			}
		}));
	}

	void inviteFriendsCall(String issueID) throws JSONException{
		showMyDialog("Inviting friends", "invite_friends", DialogType.PROGRESS);
		JSONObject body = new JSONObject();
		body.put("issue_id", issueID);
		JSONArray jsonArray = new JSONArray();
		
		for(int i=0;i<list.size() && checked[i];i++)
			jsonArray.put(i,list.get(i).id);
		body.put("users", jsonArray);
		Livolve.requestQueue.add(new JsonObjectRequest(UrlConstants.getInviteFriendsUrl(), body, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				removeMyDialog("invite_friends");
				finish();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				removeMyDialog("invite_friends");
			}
		}));
	}

	private boolean[] checked;

	public void inviteFriends(final String issueID){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		checked = new boolean[list.size()];
		CharSequence[] items = new CharSequence[list.size()];
		for(int i = 0 ;i<list.size();i++){
			items[i] = list.get(i).name;
		}

		AlertDialog dialog = builder.setTitle("Select team members").setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checked[which] = isChecked;
			}
		}).setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					inviteFriendsCall(issueID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).create();

		dialog.show();
	}
}
