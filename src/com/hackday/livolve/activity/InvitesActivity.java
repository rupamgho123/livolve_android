package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hackday.livolve.Issue;
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

	private void getInvites() {
		Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getInvitesForMe(Util.getUserId(getApplicationContext())), new Response.Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				List<Issue> list = new ArrayList<Issue>();
				for(int i=0;i<response.length();i++)
				{
					try {
						JSONObject json = (JSONObject) response.get(i);
						String id = json.getString(Constants.ID);
						String value = json.getString(Constants.TITLE);
						String status = json.getString(Constants.STATUS);
						String userId = json.getString(Constants.USER_ID);
						String summary = json.getString(Constants.SUMMARY);
						list.add(new Issue(id,value,status,userId,summary));
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
	}

	private class MyListAdapter extends BaseAdapter{

		private List<Issue> list;
		MyListAdapter(List<Issue> list)
		{
			this.list = list;
		}

		MyListAdapter()
		{
			this.list = new ArrayList<Issue>();
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
			return Long.parseLong(list.get(arg0).getId());
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
			{
				arg1 = getLayoutInflater().inflate(R.layout.list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.answer = (TextView) arg1.findViewById(R.id.textView);
				arg1.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			Issue item = (Issue)getItem(arg0);
			viewHolder.answer.setText(item.getValue());
			return arg1;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 0, 0, "Accept invitation");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getOrder())
		{
		case 0:
			String inviteId = adapter.list.get(info.position).getId();
			acceptInvitation(inviteId);
			return true;
		default: return super.onContextItemSelected(item);
		}
	}

	private void acceptInvitation(String inviteId) {
		Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getInvitesForMe(Util.getUserId(getApplicationContext())), new Response.Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}));
	}
}
