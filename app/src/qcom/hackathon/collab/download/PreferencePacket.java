package qcom.hackathon.collab.download;

import java.util.List;
import java.util.ArrayList;

public class PreferencePacket {
	public int dataAmt;
	public List<String> files;
	
	public PreferencePacket(int dataAmt, List<String> files) {
		this.dataAmt = dataAmt;
		this.files = new ArrayList<String>(files);
	}
}
