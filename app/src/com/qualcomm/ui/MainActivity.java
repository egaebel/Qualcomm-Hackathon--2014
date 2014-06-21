package com.qualcomm.ui;

import qcom.hackathon.collab.download.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements TabListener {
	RelativeLayout rl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_bar_main);
		try {
			rl = (RelativeLayout) findViewById(R.id.mainLayout);
			fragMentTra = getFragmentManager().beginTransaction();
			ActionBar bar = getActionBar();
			bar.addTab(bar.newTab().setText("Welcome").setTabListener(this));
			bar.addTab(bar.newTab().setText("Peers").setTabListener(this));
			bar.addTab(bar.newTab().setText("Progress").setTabListener(this));

			bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
					| ActionBar.DISPLAY_USE_LOGO);
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			bar.setDisplayShowHomeEnabled(true);
			bar.setDisplayShowTitleEnabled(false);
			bar.show();

		} catch (Exception e) {
			e.getMessage();
		}
		/**
		 * Hiding Action Bar
		 */
	}

	FragMent1 fram1;
	FragmentTransaction fragMentTra = null;
	FragMent2 fram2;
	FragMent3 fram3;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_action_bar_main, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		if (tab.getText().equals("Welcome")) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram1 = new FragMent1();
			Bundle args = new Bundle();
			String[] movieList = new String[]{"movie1", "movie2", "movie3", "movie4"};
			args.putStringArray("list", movieList);
			fram1.setArguments(args);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram1);
			fragMentTra.commit();
		} else if (tab.getText().equals("Peers")) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram2 = new FragMent2();
			Bundle args = new Bundle();
			String[] peerList = new String[]{"peer1", "peer2", "peer3", "peer4"};
			args.putStringArray("list", peerList);
			fram2.setArguments(args);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram2);
			fragMentTra.commit();
		} else if (tab.getText().equals("Progress")) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram3 = new FragMent3();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram3);
			fragMentTra.commit();
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

}
