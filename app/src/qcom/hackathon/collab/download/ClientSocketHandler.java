
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * The non-group owners always act as clients in socket initialization.  
 * The Handler exposes a socket that connects with the group owner.
 */

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private Activity mAct= null;
    private InetAddress mAddress;
    private int commType;
    
    private final int DISTRIBUTE = 4;
    private final int DECISION = 1;
    
    private final int SEND = 0;
    private final int RECEIVE = 1;

    public ClientSocketHandler(Activity mAct, InetAddress groupOwnerAddress, int commType) {
        this.mAct = mAct;
        this.mAddress = groupOwnerAddress;
        this.commType = commType;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    WiFiDConnectionManager.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            if(commType == RECEIVE) {
            	RecFileManager rec = new RecFileManager(socket, this.mAct);
            	rec.receiveFile();
            } else if(commType == SEND) {
            	SendFileManager snd = new SendFileManager(socket, this.mAct);
                snd.sendFile();
            }
        
//            chat = new ChatManager(socket, handler);
//            new Thread(chat).start();
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
