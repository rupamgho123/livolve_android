package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.hackday.livolve.Livolve;
import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.UrlConstants;
import com.hackday.livolve.util.Util;

public class InvitesActivity extends LivolveActivity{

	ListView listView;
	MyListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invites);
		listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);
		getInvites();
	}
	
	private class Invite{
		String created_at;
		String name;
		String title;
		String id;
		Invite(String id,String created_at,String name,String title)
		{
			this.id = id;
			this.created_at = created_at;
			this.name = name;
			this.title = title;
		}
	}

	private void getInvites() {
		Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getInvitesForMe(Util.getUserId(getApplicationContext())), new Response.Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				List<Invite> list = new ArrayList<Invite>();
				for(int i=0;i<response.length();i++)
				{
					try {
						JSONObject json = (JSONObject) response.get(i);
						String id = json.getString(Constants.ID);
						JSONObject user = json.getJSONObject("user");
						JSONObject issue = json.getJSONObject("issue");
						String created_at = issue.getString("created_at");
						String title = issue.getString("title");
						String userName = user.getString("name");
						list.add(new Invite(id,created_at,userName,title));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				adapter = new MyListAdapter(list);
				listView.setAdapter(adapter);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}));
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
		return "InvitesActivity";
	}

	private static class ViewHolder{
		TextView answer;
		TextView detail;
	}

	private class MyListAdapter extends BaseAdapter{

		private List<Invite> list;
		MyListAdapter(List<Invite> list)
		{
			this.list = list;
		}

		MyListAdapter()
		{
			this.list = new ArrayList<Invite>();
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
			return Long.parseLong(list.get(arg0).id);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
			{
				arg1 = getLayoutInflater().inflate(R.layout.invite_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.answer = (TextView) arg1.findViewById(R.id.answer);
				viewHolder.detail = (TextView) arg1.findViewById(R.id.detail);
				arg1.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			Invite item = (Invite)getItem(arg0);
			viewHolder.answer.setText(item.title);
			viewHolder.detail.setText(item.created_at);
			return arg1;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options");
		menu.add(0, 0, 0, "Accept invitation");
		menu.add(1, 1, 1, "Decline invitation");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		String inviteId = adapter.list.get(info.position).id;
		switch(item.getOrder())
		{
		case 0:
			acceptInvitation(inviteId,"accepted");
			return true;
		case 1:
			acceptInvitation(inviteId,"declined");
			return true;
		default: return super.onContextItemSelected(item);
		}
	}

	private void acceptInvitation(String inviteId,String status) {
		Livolve.requestQueue.add(new StringRequest(Request.Method.PUT, UrlConstants.updateInviteUrl(inviteId,status), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				getInvites();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}));
	}
}
