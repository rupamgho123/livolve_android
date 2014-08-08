package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hackday.livolve.DialogType;
import com.hackday.livolve.Livolve;
import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.UrlConstants;
import com.hackday.livolve.util.Util;

public class IssueDetailActivity extends LivolveActivity{

	ListView listView;
	TextView summaryField;
	TextView titleField;
	List<Conversation> list;

	MyListAdapter adapter;
	String issueId;
	String title;
	String summary;

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			try
			{
				Bundle extras = arg1.getExtras();
				JSONObject user = new JSONObject(extras.getString("user"));
				//				String userid = user.getString(Constants.ID);
				//				String storedId = Util.getUserId(getApplicationContext());
				//				if(userid.equals(storedId))
				//						return;

				String userName = user.getString(Constants.NAME);
				String date = extras.getString(Constants.CREATED_AT);
				String content = "answered by "+userName+" on "+date;
				String id = extras.getString(Constants.ID);
				Conversation conversation = new Conversation(Integer.parseInt(id),extras.getString(Constants.VALUE),content,false);
				if(!list.contains(conversation))
					list.add(conversation);
				
				adapter.notifyDataSetChanged();
				listView.smoothScrollToPosition(adapter.getCount());
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversations);
		getActionBar().setDisplayUseLogoEnabled(false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		issueId = getIntent().getStringExtra(Constants.ISSUE_ID);
		title = getIntent().getStringExtra(Constants.TITLE);
		summary = getIntent().getStringExtra(Constants.SUMMARY);
		listView = (ListView)findViewById(R.id.listView);
		summaryField = (TextView)findViewById(R.id.summary);
		titleField = (TextView)findViewById(R.id.title);

		summaryField.setText(summary);
		titleField.setText(title);

		registerForContextMenu(listView);
		callForData();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		intentFilter.addAction(Constants.CONVERSATION_ACTION);

		registerReceiver(receiver, intentFilter);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	void callForData(){
		adapter = new MyListAdapter();
		listView.setAdapter(adapter);

		showMyDialog("Fetching Conversations...", "conversation", DialogType.PROGRESS);
		Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getConversationUrl(issueId), 
				new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				removeMyDialog("conversation");
				list = new ArrayList<IssueDetailActivity.Conversation>();
				for(int i=0;i<response.length();i++)
				{
					try {
						JSONObject item = response.getJSONObject(i);
						JSONObject user = item.getJSONObject("user");
						String solution = item.getString("is_solution");
						String userName = user.getString(Constants.NAME);
						String date = item.getString(Constants.CREATED_AT);
						String content = "answered by "+userName+" on "+date;
						list.add(new Conversation(item.getInt(Constants.ID),item.getString(Constants.VALUE),content,!solution.equalsIgnoreCase("N")));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				adapter = new MyListAdapter(list);
				listView.setAdapter(adapter);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				removeMyDialog("conversation");
				showMyDialog(Util.getMessageFromVolleyError(error), "error", DialogType.ERROR);
			}
		}));
	}
	private class Conversation{
		int id;
		String answer;
		String detail;
		boolean isAnswer;

		Conversation(int id,String answer,String detail,boolean isAnswer){
			this.id = id;
			this.answer = answer;
			this.detail = detail;
			this.isAnswer = isAnswer;
		}

		@Override
		public boolean equals(Object o) {
			Conversation obj = (Conversation) o;
			return obj.id == this.id;
		}
	}

	static class ViewHolder {
		TextView answer;
		TextView detail;
		CheckBox checkbox;
	}

	private class MyListAdapter extends BaseAdapter{

		private List<Conversation> list;
		MyListAdapter(List<Conversation> list)
		{
			this.list = list;
		}

		MyListAdapter()
		{
			this.list = new ArrayList<IssueDetailActivity.Conversation>();
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
				arg1 = getLayoutInflater().inflate(R.layout.conversation_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.answer = (TextView) arg1.findViewById(R.id.answer);
				viewHolder.detail = (TextView) arg1.findViewById(R.id.detail);
				viewHolder.checkbox = (CheckBox) arg1.findViewById(R.id.checkBox);
				arg1.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			Conversation item = (Conversation)getItem(arg0);
			viewHolder.answer.setText(item.answer);
			viewHolder.detail.setText(item.detail);
			viewHolder.checkbox.setVisibility(item.isAnswer?View.VISIBLE:View.INVISIBLE);
			viewHolder.checkbox.setFocusable(false);
			viewHolder.checkbox.setFocusableInTouchMode(false);
			viewHolder.checkbox.setClickable(false);
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
		return "IssueDetailActivity";
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options...");
		menu.add(0,v.getId(),0,"Mark as answer");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		showMyDialog("Marking answer...", "answer", DialogType.PROGRESS);
		switch(item.getOrder())
		{
		case 0:
			Conversation conversation = (Conversation)adapter.getItem(info.position);
			Livolve.requestQueue.add(new StringRequest(Request.Method.PUT, UrlConstants.getMarkAnswerUrl(""+conversation.id), new Response.Listener<String>() {

				@Override
				public void onResponse(String response) {
					removeMyDialog("answer");
					callForData();
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					removeMyDialog("answer");
				}
			}));
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void submit(View v) throws JSONException{
		EditText field = (EditText) findViewById(R.id.textField);
		String data = field.getText().toString();
		field.setText("");
		hideSoftKeyboard();
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("value", data);
		jsonRequest.put("user_id", Util.getUserId(getApplicationContext()));
		jsonRequest.put("issue_id", issueId);

		Livolve.requestQueue.add(new JsonObjectRequest(UrlConstants.createConversationUrl(), jsonRequest, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				//				callForData();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}));
	}

	public void hideSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
}
