
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

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
    private final int THREAD_COUNT = 10;
    private Activity mAct;
    private int commType;
    private static final String TAG = "GroupOwnerSocketHandler";
    
    private final int DISTRIBUTE = 4;
    private final int DECISION = 1;
    
    private final int SEND = 0;
    private final int RECEIVE = 1;

    public GroupOwnerSocketHandler(Activity mAct, int commType) throws IOException {
        try {
            socket = new ServerSocket(4545);
            this.mAct=mAct;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
            try {
            	// instead of initiating a chat, we receive a file
                pool.execute(new RecFileManager(socket.accept(), this.mAct));
                Log.d(TAG, "Launching receiver side to receive files");
                
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
            }
        }
}
