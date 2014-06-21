
package qcom.hackathon.collab.download;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;

/**
 * Handles reading and writing of byte arrays through sockets. Uses a Handler
 * to post messages to UI thread for UI updates.
 *
 */
public class RecFileManager {

    private Socket socket = null;
    private Activity mAct = null;
    private byte[] size = new byte[4];
    private byte[] bArray;
    private byte[] buf = new byte[4096];
    private List<Socket> socketList;
    List<byte[]> byteListArray = new ArrayList<byte[]>();
    
    public RecFileManager(List<Socket> socketList, Activity mAct) {
        this.socketList = socketList;
        this.mAct = mAct;
    }

    private InputStream iStream;
//    private OutputStream oStream;
    private static final String TAG = "RecFileManager";

    
    public List<byte[]> getReceivedByteArray() {
    	return byteListArray;
    }

    public void receiveFile() {
        try {
        	for(int i = 0; i < socketList.size(); i++) {
	        		
	        	//receive size of byte array
	            iStream = socket.getInputStream();
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            int sizeCheck = iStream.read(size,0,4);
	            if(sizeCheck != 4) {
	            	// TODO Throw error...or something
	            }
	            int nextBytePlace=0;
	            while(true) {
	            	Log.d(TAG, "receiver kept receiving");
	            	int n = iStream.read(buf);
	            	if( n < 0 ) break;
	            	baos.write(buf,nextBytePlace,n);
	            	nextBytePlace+=n;
	            }
	            bArray = baos.toByteArray();
	            byteListArray.add(bArray);
        	}
            /*
        	Log.d(TAG, "receiver finished receiving");
        	this.mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(mAct, "received" + file.toString(), Toast.LENGTH_LONG).show();;
                }
            });*/
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
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
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


