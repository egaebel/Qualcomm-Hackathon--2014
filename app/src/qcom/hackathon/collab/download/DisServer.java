
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class DisServer {

    private Socket socket = null;
//    private byte[] bArrayToSend;

    public DisServer(Socket socket) {
        this.socket = socket;
//        this.bArrayToSend = bArray;
    }

    private OutputStream oStream;
    private DataOutputStream dataOutStream;    
    private static final String TAG = "SendFileManager";


    public void sendTrunkNum(int val) {
        try {
            oStream = socket.getOutputStream();
            dataOutStream = new DataOutputStream(oStream);
            dataOutStream.writeInt(val);
        	Log.d(TAG, "server distribute trunkNum: " + val);            
        }    
        catch (IOException e) {
        	e.printStackTrace();
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        finally {
        	try{ 
        		oStream.close();
        		dataOutStream.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
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



}
