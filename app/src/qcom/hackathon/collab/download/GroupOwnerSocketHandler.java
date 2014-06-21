package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.net.Socket;
import java.util.List;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 * 
 * The group owner always acts as a server when sockets are being initialized.
 * The handler aggregates and exposes a list of sockets.
 */
public class GroupOwnerSocketHandler extends Thread {

    ServerSocket socket = null;
    private List<Socket> socketList;
    
    // TODO Get thread count from size of group.
    private final int THREAD_COUNT = 10;
    private static final String TAG = "GroupOwnerSocketHandler";

    public GroupOwnerSocketHandler() throws IOException {
        try {
            socket = new ServerSocket(4545);
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }
    
    public List<Socket> getGOSocketList() {
    	return socketList;
    }

    @Override
    public void run() {
            try {
            	// initialize sockets with clients and put them in 
                for(int i = 0; i < THREAD_COUNT; i++) {
                	socketList.add(i, socket.accept());
                }
                Log.d(TAG, "Launching receiver side to receive files");
                
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
            }
        }
}
