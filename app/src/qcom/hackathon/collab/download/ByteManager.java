package qcom.hackathon.collab.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ByteManager implements ConnectionInfoListener {

	private static final String TAG = "CONNECTION INFO LISTENER";
	private WiFiDConnectionManager wifiDConnMan;
	private Client client;
	private Activity activity;
	private int phase = 0; //phase: 0 -- trunk number ;  1 -- distribute
	private int trunkNumRec= -1;
	private static final int SERVER_PORT= 8000;
	private static final int CLIENT_PORT= 8000;	
	
	public ByteManager(String ipAddress, int portNum, Activity activity) throws IOException {
		
		client = new Client(ipAddress, portNum);
		wifiDConnMan = new WiFiDConnectionManager(activity.getApplicationContext(), activity, this);
		this.activity = activity;
	}
	
	public void kickstart() {
		
		//discovery
		
		//get the data determined from discovery
		
		//pass the data to demand, and load the callback
		//ResponseCallback callback = ResponseCallback();
		//client.demand(callback, groupId, groupSize, chunkSize, filename);
	}
	
	public int getTrunkNum(){
		return this.trunkNumRec;
	}
	
	public void stopWifiD() {
		
		wifiDConnMan.stop();
	}
	
	public void resumeWifiD(Activity act) {
		
		wifiDConnMan.resume(act);
	}
	
	public void pauseWifiD(Activity act) {
		
		wifiDConnMan.pause(act);
	}
	
	public List<WiFiP2pService> getServiceWifiCtrlServiceList() {
		
		return wifiDConnMan.getServiceList();
	}
	
	public void connectP2p(WiFiP2pService service) {
		
		wifiDConnMan.connectP2p(service);
	}

	/**
	 * connectioninfolistener, called when a connection is established
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;
		Toast.makeText(activity, "connection established", Toast.LENGTH_LONG).show();;        
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */
		if (0 == phase){
	        if (p2pInfo.isGroupOwner) {
	            Log.d(TAG, "Connected as group owner");
	            this.trunkNumRec=0;
	            //Starting the task. 
	            new SocketAsync().execute(SERVER_PORT);	            
	        } else {
	            Log.d(TAG, "Connected as peer");
	            String hostAddr=p2pInfo.groupOwnerAddress.getHostAddress();	     
	            try {
					this.trunkNumRec=new ClientSocketAsync().execute((Object)hostAddr).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            Toast.makeText(this.activity, "trunc number: "+this.trunkNumRec, Toast.LENGTH_LONG).show();
//	            handler = new ClientSocketHandler(activity, p2pInfo.groupOwnerAddress);
//	            handler.start();
	        }
	        
		}
	}
	
	public class ResponseCallback {
		
		/**
		 * The offset within the actual chunk.
		 */
		private int chunkOffset;
		/**
		 * Byte buffer that holds the bytes until the chunk can be written back to file.
		 */
		private ByteArrayOutputStream writer;
		/**
		 * The offset of the chunk in the file.
		 * 
		 * # of bytes of offset == (chunkNum * chunkSize)
		 */
		private int chunkNum;
		/**
		 * The size of the chunk to download.
		 */
		private int chunkSize;
		/**
		 * The File object to write to.
		 */
		private File file;
		/**
		 * The offset the chunk starts at from the beginning of the file in number of bytes
		 */
		private int byteOffset;
		
		/**
		 *  
		 * 
		 * @param wifiMan
		 * @param filename
		 * @param chunkNum
		 * @param chunkSize
		 * @throws IOException
		 */
		public ResponseCallback(WiFiDConnectionManager wifiMan, 
								String filename, int chunkNum, int chunkSize) throws IOException {
			
			this.chunkNum = chunkNum;
			this.chunkSize = chunkSize;
			chunkOffset = 0;
			file = new File(filename);
			writer = new ByteArrayOutputStream(1024);
			byteOffset = -1;
		}

		public void writeToByteBuffer(byte[] buffer, int bufferLen) {
			
			writer.write(buffer, (chunkNum * chunkSize) + chunkOffset, bufferLen);
			chunkOffset += bufferLen;
		}
		
		public void writeBufferToFriends(byte[] buffer, int bufferLen) {
			
			//wifiMan. TODO: write bytes to friendos
			int byteOffset = (chunkNum * chunkSize) + chunkOffset;
		}
		
		public void writeChunkToFile() throws IOException {
			
			FileOutputStream out = new FileOutputStream(file);
			writer.writeTo(out);
			out.write(writer.toByteArray(), (chunkNum * chunkSize), writer.toByteArray().length);
			writer.close();
			out.close();
		}

		/**
		 * @return the byteOffset
		 */
		public int getByteOffset() {
			return byteOffset;
		}

		/**
		 * @param byteOffset the byteOffset to set
		 */
		public void setByteOffset(int byteOffset) {
			this.byteOffset = byteOffset;
		}
	}
	
	
	class SocketAsync extends AsyncTask<Integer, Void, Void> {
		        @Override
		        protected Void doInBackground(Integer... port) {
		            try {
		                ServerSocket socket = new ServerSocket(port[0]);
		                for (int i =0 ; i< 1; i++){
			                Socket connectSocket = socket.accept();
			            	DisServer mServer = new DisServer(connectSocket);
			            	mServer.sendTrunkNum(i+1); // group owner gets trunk 0
		                }
		        	
		            } catch (IOException e) {
		            	Log.d(TAG,
		            			"Failed to create a server socket- " + e.getMessage());
		            }
		            return null;
		        }
		    }

	class ClientSocketAsync extends AsyncTask<Object, Void, Integer> {
        @Override
        protected Integer doInBackground(Object... port) {
            Socket clientSocket = new Socket();
            int ret=-1;
            try {
				clientSocket.bind(null);
	            clientSocket.connect(new InetSocketAddress((String)port[0],
	            		SERVER_PORT), CLIENT_PORT);
	            DisClient mClient = new DisClient(clientSocket);
	            ret = mClient.recTrunkNum();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return ret;
        }
    }
	
}


