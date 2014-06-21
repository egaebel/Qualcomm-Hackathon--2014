
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles reading and writing of byte arrays through sockets. Uses a Handler
 * to post messages to UI thread for UI updates.
 *
 */
public class DisClient {

    private Socket socket = null;
    private Activity mAct = null;
//    private byte[] size = new byte[4];
    private byte[] bArray;
//    private byte[] buf = new byte[4096];
    
    public DisClient(Socket socket) {
        this.socket = socket;
//        this.mAct = mAct;
    }

    private InputStream iStream;
    private DataInputStream dataStream;
//    private OutputStream oStream;
    private static final String TAG = "RecFileManager";

    
    public byte[] getReceivedByteArray() {
    	return bArray;
    }

    public int recTrunkNum() {
    	int trunkNum=-1;
        try {
        	//receive size of byte array
            iStream = socket.getInputStream();
            dataStream = new DataInputStream(iStream);
            trunkNum = dataStream.readInt();
            final int trunkNumDis = trunkNum;
        	Log.d(TAG, "trunkNum " + trunkNum);            
        	this.mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(mAct, "trunkNum: "+trunkNumDis, Toast.LENGTH_LONG).show();;
                }
            });
        }    
        catch (IOException e) {
        	e.printStackTrace();
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        finally {
        	try{ 
        		iStream.close();
        		dataStream.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
        return trunkNum;
    }
}            
//            byte[] buffer = new byte[1024];
//            int bytes;
//            handler.obtainMessage(WiFiServiceDiscoveryActivity.MY_HANDLE, this)
//                    .sendToTarget();
//
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = iStream.read(buffer);
//                    if (bytes == -1) {
//                        break;
//                    }
//
//                    // Send the obtained bytes to the UI Activity
//                    Log.d(TAG, "Rec:" + String.valueOf(buffer));
//                    handler.obtainMessage(WiFiServiceDiscoveryActivity.MESSAGE_READ,
//                            bytes, -1, buffer).sendToTarget();
//                } catch (IOException e) {
//                    Log.e(TAG, "disconnected", e);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }    
//        }


