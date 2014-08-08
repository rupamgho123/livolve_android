package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gcm.GCMRegistrar;
import com.hackday.livolve.DialogType;
import com.hackday.livolve.Issue;
import com.hackday.livolve.Livolve;
import com.hackday.livolve.R;
import com.hackday.livolve.util.Constants;
import com.hackday.livolve.util.IssueType;
import com.hackday.livolve.util.UrlConstants;
import com.hackday.livolve.util.Util;

public class MainActivity extends LivolveActivity{

	private String[] mPlanetTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(R.drawable.ic_drawer);

		mPlanetTitles = getResources().getStringArray(R.array.menu_list);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item, R.id.textView, mPlanetTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				//                getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				//                getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		initGCM();
	}



	private void initGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, "522992358628");
		} else {
			Log.v(TAG, "Already registered");
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		addDefaultTabs();
	}

	MyListFragment frag1;
	MyListFragment frag2;
	
	private void addDefaultTabs() {
		getActionBar().removeAllTabs();

		frag1 = new MyListFragment(IssueType.MINE);
		frag2 = new MyListFragment(IssueType.OTHERS);
		getActionBar().addTab(getActionBar().newTab()
				.setText("My Issues")
				.setTabListener(new MyTabListener(frag1,IssueType.MINE)), true);
		getActionBar().addTab(getActionBar().newTab()
				.setText("Other Issues")
				.setTabListener(new MyTabListener(frag2,IssueType.OTHERS)));
	}

	private class MyTabListener implements ActionBar.TabListener{

		public ListFragment fragment;
		public IssueType type;

		public MyTabListener(ListFragment fragment,IssueType type) {
			this.fragment = fragment;
			this.type = type;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Log.d(TAG,"");
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mDrawerLayout.closeDrawers();
			ft.replace(R.id.content_frame, fragment,"list");
			Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getIssuesUrl(type,Util.getUserId(getApplicationContext())),new Listener<JSONArray>() {

				@Override
				public void onResponse(JSONArray response) {

					List<Issue> list = new ArrayList<Issue>();
					for(int i=0;i<response.length();i++)
					{
						try {
							JSONObject json = (JSONObject)response.get(i);
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
					ListBaseAdapter adapter = new ListBaseAdapter(list);
					fragment.setListAdapter(adapter);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					showMyDialog(Util.getMessageFromVolleyError(error), "error", DialogType.ERROR);
				}

			}));
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}



	static class ViewHolder {
		TextView value;
		TextView status;
	}

	private class ListBaseAdapter extends BaseAdapter{

		private List<Issue> issues;

		ListBaseAdapter()
		{
			issues = new ArrayList<Issue>();
		}

		ListBaseAdapter(List<Issue> issues)
		{
			this.issues = issues;
		}

		private List<Issue> getList(){
			return issues;
		}

		@Override
		public int getCount() {
			return issues.size();
		}

		@Override
		public Object getItem(int arg0) {
			return issues.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return Long.parseLong(issues.get(arg0).getId());
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
			{
				arg1 = getLayoutInflater().inflate(R.layout.issue_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.status = (TextView) arg1.findViewById(R.id.status);
				viewHolder.value = (TextView) arg1.findViewById(R.id.value);
				arg1.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			Issue item = (Issue)getItem(arg0);
			viewHolder.status.setText(item.getStatus());
			viewHolder.status.setVisibility(View.INVISIBLE);
			viewHolder.value.setText(item.getValue());
			if(item.getStatus().equalsIgnoreCase("deleted"))
				viewHolder.value.setPaintFlags(viewHolder.value.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			else
				viewHolder.value.setPaintFlags(viewHolder.value.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
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
		return "MainActivity";
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	public void selectItem(int position) {
		switch(position){
		case 0:
			addIssue();
			break;
		case 1:
			showInvites();
			break;
		case 2:
			showProfile();
			break;
		case 3:
			logout();
			break;
		}

		mDrawerLayout.closeDrawers();
	}

	private void showProfile() {
		goTo(ProfileActivity.class);
	}

	private void showInvites() {
		goTo(InvitesActivity.class);
	}

	private void addIssue() {
		goTo(AddIssueActivity.class);
	}

	private void logout() {
		Util.clearData(this);
		goTo(LoginActivity.class);
		finish();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	public static class MyListFragment extends ListFragment{

		IssueType mine;
		public MyListFragment(IssueType mine) {
			this.mine = mine;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			getListView().setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Bundle extras = new Bundle();
					extras.putString(Constants.ISSUE_ID, Long.toString(arg3));
					LivolveActivity activity = (LivolveActivity)getActivity();
					ListBaseAdapter adapter = (ListBaseAdapter)getListView().getAdapter();
					List<Issue> list = adapter.getList();
					Issue issue = list.get(arg2);
					extras.putString(Constants.TITLE, issue.getValue());
					extras.putString(Constants.SUMMARY, issue.getSummary());
					activity.goTo(IssueDetailActivity.class, extras);
				}
			});

			if(mine == IssueType.MINE)
				getActivity().registerForContextMenu(getListView());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options");
		menu.add(0,0,0,"Close Issue");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ListBaseAdapter adapter = (ListBaseAdapter)frag1.getListView().getAdapter();
		List<Issue> list = adapter.getList();
		switch(item.getOrder()){
		case 0:
			markClosed(list.get(info.position));
		default:
			return super.onContextItemSelected(item);
		}
	}

	void markClosed(Issue issue){
		Livolve.requestQueue.add(new StringRequest(Request.Method.PUT,UrlConstants.updateIssueUrl(issue.getId(),"deleted"),new Listener<String>() {

			@Override
			public void onResponse(String response) {
				addDefaultTabs();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}

		}));
	}
}
