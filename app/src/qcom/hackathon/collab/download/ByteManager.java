package qcom.hackathon.collab.download;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.widget.Toast;

public class ByteManager implements ConnectionInfoListener {

	private static final String TAG = "CONNECTION INFO LISTENER";
	private WiFiDConnectionManager wifiDConnMan;
	private Client client;
	private Activity activity;
	
	public ByteManager(String ipAddress, int portNum, Activity activity) throws IOException {
		
		client = new Client(ipAddress, portNum);
		wifiDConnMan = new WiFiDConnectionManager(activity.getApplicationContext(), activity);
		this.activity = activity;
	}
	
	public void kickstart() {
		
		//discovery
		
		//get the data determined from discovery
		
		//pass the data to demand, and load the callback
		//ResponseCallback callback = ResponseCallback();
		//client.demand(callback, groupId, groupSize, chunkSize, filename);
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
        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler();
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(p2pInfo.groupOwnerAddress);
            handler.start();
        }
        
        try {
        	handler.join();
        } catch (InterruptedException e) {
        	
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
}