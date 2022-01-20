import org.junit.Test;

import edu.kit.kastel.sdq.eclipse.grading.client.websocket.ArtemisFeedbackWebsocket;

public class StudentClientTests {

	@Test
	public void test_websocket() {
		
		ArtemisFeedbackWebsocket websocket = new ArtemisFeedbackWebsocket();
		websocket.connect();
	}
}
