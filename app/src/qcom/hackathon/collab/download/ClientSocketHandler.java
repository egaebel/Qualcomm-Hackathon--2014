
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/*
 * The non-group owners always act as clients in socket initialization.  
 * The Handler exposes a socket that connects with the group owner.
 */

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private InetAddress mAddress;
    private Socket socket;

    public ClientSocketHandler(InetAddress groupOwnerAddress) {
        this.mAddress = groupOwnerAddress;
    }
    
    public Socket getNonGOSocket() {
    	return socket;
    }

    @Override
    public void run() {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    WiFiDConnectionManager.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
        
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

}
