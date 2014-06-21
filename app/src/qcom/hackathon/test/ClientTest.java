package qcom.hackathon.test;

import java.io.IOException;

import org.junit.Test;

import qcom.hackathon.collab.download.Client;
import junit.framework.TestCase;

public class ClientTest extends TestCase {

	@Test
	public void clientTest() throws IOException {
		
		Client client = new Client("76.167.74.100", 80);
		client.demand("000000000", 88, "some_file", 999);
	}
}
