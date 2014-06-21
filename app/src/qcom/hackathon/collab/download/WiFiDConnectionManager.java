package qcom.hackathon.collab.download;

import java.util.HashMap;
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
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;


public class WiFiDConnectionManager {
	
	public WifiP2pManager manager;
	public Channel channel;
	public BroadcastReceiver receiver;
    public final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pDnsSdServiceRequest serviceRequest;
	
	public static final String TAG = "WiFiDConnectionManager";
	
    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_collaborativedownload";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    static final int SERVER_PORT = 4545;

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
		this.startRegistrationAndDiscovery();
	}
	
	public List<WiFiP2pService> getServiceList() {
		return serviceList;
	}
	
	
    /**
     * Registers a local service and then initiates a service discovery
     */
	
	public void startRegistrationAndDiscovery() {
	        Map<String, String> record = new HashMap<String, String>();
	        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

	        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
	                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
	        manager.addLocalService(channel, service, new ActionListener() {

	            @Override
	            public void onSuccess() {
	                Log.i(TAG, "Added Local Service");
	            }

	            @Override
	            public void onFailure(int error) {
	                Log.i(TAG, "Failed to add a service");
	            }
	        });
	        discoverService();
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
    
    public void resume(Activity uiActivity) {
    	uiActivity.registerReceiver(receiver, intentFilter);
    }
    
    public void pause(Activity uiActivity) {
    	uiActivity.unregisterReceiver(receiver);
    }
    
    
    public void stop() {
    	 if (manager != null && channel != null) {
             manager.removeGroup(channel, new ActionListener() {

                 @Override
                 public void onFailure(int reasonCode) {
                     Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                 }

                 @Override
                 public void onSuccess() {
                 }

             });
         }
    }
    
    
  
    
}
    
    
