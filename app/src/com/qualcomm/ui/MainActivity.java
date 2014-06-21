package com.qualcomm.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qcom.hackathon.collab.download.ByteManager;
import qcom.hackathon.collab.download.ClientSocketHandler;
import qcom.hackathon.collab.download.GroupOwnerSocketHandler;
import qcom.hackathon.collab.download.R;
import qcom.hackathon.collab.download.WiFiDConnectionManager;
import qcom.hackathon.collab.download.WiFiP2pService;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements TabListener, ConnectionInfoListener {
	public static RelativeLayout rl;

	//public WiFiDConnectionManager wifi_ctrl = null; 
	private ByteManager byteMan;
	public static List<WiFiP2pService> peerList = null; 
	static FragMent1 fram1;
	static FragmentTransaction fragMentTra = null;
	static FragMent2 fram2;
	static FragMent3 fram3;
	private String TAG = "mainactivity";
	
	
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
		
		//initialize wifi connection, searching for devices around
		//wifi_ctrl = new WiFiDConnectionManager(getApplicationContext(), this);
		try {
			byteMan = new ByteManager("76.167.74.100", 80, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			String[] peerListName = new String[peerList.size()];
			for (int i=0; i < peerList.size(); i++){
				peerListName[i] = peerList.get(i).device.deviceName;
			}
			args.putStringArray("list", peerListName);
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
	
	public void wel2peer(){
		peerList = byteMan.getServiceWifiCtrlServiceList();//wifi_ctrl.getServiceList();

		this.getActionBar().setSelectedNavigationItem(1);
		for (WiFiP2pService eachDevice:peerList){
			byteMan.connectP2p(eachDevice);
			Log.d("connecting to device", eachDevice.device.deviceName);
		}
/*		
		try {
			rl.removeAllViews();
		} catch (Exception e) {
		}
		FragMent2 fram2 = new FragMent2();
		Bundle args = new Bundle();
		String[] peerListName = new String[peerList.size()];
		for (int i=0; i < peerList.size(); i++){
			peerListName[i] = peerList.get(i).instanceName;
		}
		args.putStringArray("list", peerListName);
		fram2.setArguments(args);
		fragMentTra.addToBackStack(null);
		fragMentTra = getFragmentManager().beginTransaction();
		fragMentTra.add(rl.getId(), fram2);
		fragMentTra.commit();
*/
	}
	
	
	
	/**
	 * connectioninfolistener, called when a connection is established
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;
		Toast.makeText(getApplicationContext(), "connection established", Toast.LENGTH_LONG).show();;        
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */
        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(this);
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(this,p2pInfo.groupOwnerAddress);
            handler.start();
        }
        
        try {
        	handler.join();
        } catch (InterruptedException e) {
        	
        }
	}

    @Override
    protected void onStop() {
    	//wifi_ctrl.stop();
    	byteMan.stopWifiD();
        super.onStop();
    }

    @Override
    public void onResume() {
    	//wifi_ctrl.resume(this);
    	byteMan.resumeWifiD(this);
        super.onResume();
    }

    @Override
    public void onPause() {
    	//wifi_ctrl.pause(this);
    	byteMan.pauseWifiD(this);
        super.onPause();
    }

}
