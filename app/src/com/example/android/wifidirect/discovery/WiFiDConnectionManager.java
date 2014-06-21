package com.example.android.wifidirect.discovery;

import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;

import com.example.android.wifidirect.discovery.WiFiDirectBroadcastReceiver;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.WiFiDevicesAdapter;

public class WiFiDConnectionManager {
	
	public WifiP2pManager manager;
	public Channel channel;
	public BroadcastReceiver receiver;
	public IntentFilter intentFilter;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	
	public static final String TAG = "WiFiDConnectionManager";
	
    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_collaborativedownload";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    
    private List<WiFiP2pService> serviceList = new ArrayList<WiFiP2pService>();
	
	
	/** 
	 * Constructor
	 * Makes manager object, calls initialize, makes intent filter, makes broadcast receiver
	 * @return error code?
	 */
	public WiFiDConnectionManager(Context mainContext, Activity mainActivity) {
		
		manager = (WifiP2pManager) mainContext.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(mainContext, Looper.getMainLooper(), null);
		
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		receiver = new WiFiDirectBroadcastReceiver(manager, channel, mainActivity);
	}
	
	public List<WiFiP2pService> getServiceList() {
		return serviceList;
	}
	
	
	/*
	 * Discover other phones running our app, compiles a list of services (other phones)
	 */
	public void discoverService() {

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

	
        manager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                            String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered. Is this our app?

                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

                            // update the UI and add the item the discovered
                            // device.
                   
                                WiFiP2pService service = new WiFiP2pService();
                                service.device = srcDevice;
                                service.instanceName = instanceName;
                                service.serviceRegistrationType = registrationType;
                                serviceList.add(service);
                                Log.d(TAG, "onBonjourServiceAvailable "
                                        + instanceName);
                            
                        }

                    }
                }, new DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                	@Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(TXTRECORD_PROP_AVAILABLE));
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.i(TAG,"Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG,"Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Log.i(TAG,"Service discovery failed");

            }
        });
    }
	
	
    public void connectP2p(WiFiP2pService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG,"Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                Log.i(TAG,"Failed connecting to service");
            }
        });
    }
	
	/*
	 * Sends out a hard coded string to confirm that all connected phones are using the app,
	 * responds to all strings received with a standard response, and 
	 * terminates all connections that do not respond correctly within 5 seconds.
	 */
	public int filterGroup() {
		
		
		return 0;	
	}
	
}
