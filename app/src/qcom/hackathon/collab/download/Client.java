package qcom.hackathon.collab.download;

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
	public boolean demand(int groupId, int groupSize, long chunkSize, String filename) {

		//Abstractions to get SocketChannel to work
		InetSocketAddress inetSockAddr = null;
		
		try {
			inetSockAddr = new InetSocketAddress(InetAddress.getByName(ipAddress), portNum);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("get by name failed...unknown host exception");
		}	
		
		try {
			sockChan.connect(inetSockAddr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IO EXCEPTION ON CONNECT!");
		}

		if (sockChan.isConnected()) {
			
			System.out.println("Socket Channel Connected!!!!!");
			//Send Request-----------------------------
			//Create & Pack ByteBuffer
			int sendBufLength = 4 + 4 + 4 + 8 + (filename.length() * 2);
			ByteBuffer sendBuf = ByteBuffer.allocate(sendBufLength);
			sendBuf.putInt(sendBufLength);
			sendBuf.putInt(groupId);
			sendBuf.putInt(groupSize);
			sendBuf.putLong(chunkSize);
			for (int j = 0; j < filename.length(); j++) {

				sendBuf.putChar(filename.charAt(j));
			}
			
			System.out.println("Byte buffer packed!");
			
			//Send
			try {
				sockChan.write(sendBuf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Sockchannel write IO exception!");
			}
			System.out.println("sockCHan write completed!");
			//Await confirmation
			ByteBuffer confirmBuf = ByteBuffer.allocate(1024);
			try {
				while (sockChan.read(confirmBuf) > 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("sockChan read, IO EXCEPTION!");
			}
			
			System.out.println("Sockchan read completed!");
			
			for (int k = 0; k < confirmBuf.limit(); k++) {
				
				System.out.print(confirmBuf.get(k));
			}
			//Validate confirmation
			if (confirmBuf.equals(sendBuf)) {
				
				return true;
			}
			else {
				
				return false;
			}
		}
		else {

			System.out.println("Socket Channel CONNECTION FAILED!!!!!");
			
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