package qcom.hackathon.collab.download;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import qcom.hackathon.collab.download.ByteManager.ResponseCallback;

/**
 * Handles communication with the server to request chunks,
 * and receive chunks.
 * 
 */
public class Client {
	
	private static final String TAG = "CLIENT";
	private String ipAddress;
	private int portNum;
	private HttpURLConnection conn;
	private URL url;
		
	/**
	 * Establish Server connection.
	 * 
	 * @param ipAddress, the string rep of an ipv4 ip address (192.162.1.1)
	 * @throws IOException 
	 */
	public Client(String ipAddress, int portNum) throws IOException {
		
		this.ipAddress = ipAddress;
		this.portNum = portNum;
	}
	
	/**
	 * Send the following:
	 * 	groupID
	 * 	groupSize
	 * 	filename
	 * 	chunkSize (varies for each participant)
	 * 
	 * @param groupId, some meaningful String, generated..very very intelligently.
	 * @param groupSize, the number of people in the group.
	 * @param filename
	 * @param chunkSize, size of the chunk, THIS user is downloading
	 * 
	 * @return true if the message was received, false otherwise.
	 * 
	 * @throws IOException
	 */
	public void demand(ResponseCallback callback, 
						int groupId, 
						int groupSize, 
						long chunkSize,
						int chunkNum,
						String filename) throws IOException {
	
		//HARD CODED FOR THE MUTHA FUCKING WIN
		groupId = 1;
		groupSize = 3;
		chunkSize = 158;
		filename = "test";
		
		url = new URL("http://" + this.ipAddress + ":" + this.portNum
						+ "/" + groupId 
						+ "-" + groupSize 
						+ "-" + chunkSize
						+ "-" + chunkNum
						+ "-" + filename);
		this.conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoInput(true);
		conn.connect();
		
		InputStream in = new BufferedInputStream(conn.getInputStream());

		//write chunk position to beginning!!!!!!!!!!!!!!
		callback.writeChunkPosition(chunkNum);
		
		byte[] buffer = new byte[1024];
		int bufferLen = 0;
		while ((bufferLen = in.read(buffer)) > 0) {
			
			callback.writeToByteBuffer(buffer, bufferLen);
		}
		
		callback.writeBufferToFriends();
		callback.writeChunkToFile();
	}
}