package com.hackday.livolve.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hackday.livolve.DialogType;
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
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		addDefaultTabs();
	}



	private void addDefaultTabs() {
		getActionBar().removeAllTabs();
		
	    
	    getActionBar().addTab(getActionBar().newTab()
                .setText("My Issues")
                .setTabListener(new MyTabListener(new MyListFragment(),IssueType.MINE)), true);
		getActionBar().addTab(getActionBar().newTab()
                .setText("Other Issues")
                .setTabListener(new MyTabListener(new MyListFragment(),IssueType.OTHERS)));
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
			Livolve.requestQueue.add(new JsonArrayRequest(UrlConstants.getIssuesUrl(type,getApplicationContext()),new Listener<JSONArray>() {

				@Override
				public void onResponse(JSONArray response) {
					
					List<Issue> list = new ArrayList<Issue>();
					for(int i=0;i<response.length();i++)
					{
						try {
							JSONObject json = (JSONObject)response.get(i);
							String id = json.getString(Constants.ID);
							String value = json.getString(Constants.VALUE);
							String status = json.getString(Constants.STATUS);
							String userId = json.getString(Constants.USER_ID);
							list.add(new Issue(id,value,status,userId));
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
	
	private class Issue{
		private String id;
		private String value;
		private String status;
		private String userId;

		public Issue(String id, String value, String status, String userId) {
			setId(id);
			setStatus(status);
			setUserId(userId);
			setValue(value);
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
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
			return Long.parseLong(issues.get(arg0).id);
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
			viewHolder.value.setText(item.getValue());
			
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

}
