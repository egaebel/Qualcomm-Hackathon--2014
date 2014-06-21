
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private Activity mAct= null;
//    private ChatManager chat;
    private InetAddress mAddress;

    public ClientSocketHandler(Activity mAct, InetAddress groupOwnerAddress) {
        this.mAct = mAct;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    WiFiDConnectionManager.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            SendFileManager snd = new SendFileManager(socket, this.mAct);
            new Thread(snd).start();
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
