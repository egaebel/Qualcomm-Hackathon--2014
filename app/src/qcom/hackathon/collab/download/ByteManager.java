package qcom.hackathon.collab.download;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ByteManager {

	public ByteManager() {
		
		
	}
	
	public void kickStart() {
		
	}
	
	public class ResponseCallback {
		
		/**
		 * The offset within the actual chunk.
		 */
		private int chunkOffset;
		/**
		 * ByteArray
		 */
		private ByteArrayOutputStream writer;
		private int chunkNum;
		private int chunkSize;
		private File file;
		
		public ResponseCallback(WiFiDConnectionManager wifiMan, 
								String filename, int chunkNum, int chunkSize) throws IOException {
			
			this.chunkNum = chunkNum;
			this.chunkSize = chunkSize;
			chunkOffset = 0;
			file = new File(filename);
			writer = new ByteArrayOutputStream(1024);
		}
		
		public void writeToByteBuffer(byte[] buffer, int bufferLen) {
			
			writer.write(buffer, (chunkNum * chunkSize) + chunkOffset, bufferLen);
			chunkOffset += bufferLen;
		}
		
		public void writeBufferToFriends(byte[] buffer, int bufferLen) {
			
			//wifiMan. TODO: write bytes to friendos
		}
		
		public void writeChunkToFile() throws IOException {
			
			FileOutputStream out = new FileOutputStream(file);
			writer.writeTo(out);
			writer.close();
			out.close();
		}
	}
}
