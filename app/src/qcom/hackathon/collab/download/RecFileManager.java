
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

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class RecFileManager implements Runnable {

    private Socket socket = null;
    private Activity mAct = null;
    
    public RecFileManager(Socket socket, Activity mAct) {
        this.socket = socket;
        this.mAct = mAct;
    }

    private InputStream iStream;
//    private OutputStream oStream;
    private static final String TAG = "ChatHandler";

    @Override
    public void run() {
        try {
        	//receive message from 
            iStream = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int nextBytePlace=0;
            while(true) {
            	Log.d(TAG, "receiver kept receiving");
            	int n = iStream.read(buf);
            	if( n < 0 ) break;
            	baos.write(buf,nextBytePlace,n);
            	nextBytePlace+=n;
            }
            byte data[] = baos.toByteArray();
            
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath());
            dir.mkdirs();
            final File file = new File(dir, "test");
            FileOutputStream fos = new FileOutputStream(file);            
            fos.write(data);
            fos.close();            
        	Log.d(TAG, "receiver finished receiving");
        	this.mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(mAct, "received" + file.toString(), Toast.LENGTH_LONG).show();;
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
        	try {
        		socket.close();
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


