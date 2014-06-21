package com.example.android.wifidirect.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

/**
 * Handles communication with the server to request chunks,
 * and receive chunks.
 * 
 */
public class Client {
	
	private static final String TAG = "CLIENT";
	private String ipAddress;
	private int portNum;
	private SocketChannel sockChan;
		
	/**
	 * Establish Server connection.
	 * 
	 * @param ipAddress, the string rep of an ipv4 ip address (192.162.1.1)
	 * @throws IOException 
	 */
	public Client(String ipAddress, int portNum) throws IOException {
		
		this.ipAddress = ipAddress;
		this.portNum = portNum;
		sockChan = SocketChannel.open();
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
	public boolean demand(String groupId, int groupSize, String filename, int chunkSize) throws IOException {
		
		//Abstractions to get SocketChannel to work
		InetSocketAddress inetSockAddr = new InetSocketAddress(InetAddress.getByName(ipAddress), portNum);	
		sockChan.connect(inetSockAddr);
		
		if (sockChan.isConnected()) {
			Log.i(TAG, "Socket Channel Connected!!!!!");
			//Send Request-----------------------------
			//Create & Pack ByteBuffer
			ByteBuffer sendBuf = ByteBuffer.allocate(1024);
			for (int i = 0; i < groupId.length(); i++) {
				
				sendBuf.putChar(groupId.charAt(i));
			}
			sendBuf.putInt(groupSize);
			for (int j = 0; j < filename.length(); j++) {
			
				sendBuf.putChar(filename.charAt(j));
			}
			sendBuf.putInt(chunkSize);
			
			//Send
			sockChan.write(sendBuf);
			
			//Await confirmation
			ByteBuffer confirmBuf = ByteBuffer.allocate(1024);
			while (sockChan.read(confirmBuf) != 0) {
				
				//receive confirmation
				confirmBuf.equals(sendBuf);
			}
			
			//Validate confirmation
			//if valid return true
			
			return true;
		}
		else {
			
			Log.i(TAG, "Socket Channel CONNECTION FAILED!!!!!");
			//error
			return false;
		}
	}
	
	public void download() {
		
		if (sockChan.isConnected()) {
			
			
		}
		else {
			
			
		}
	}
	
}
