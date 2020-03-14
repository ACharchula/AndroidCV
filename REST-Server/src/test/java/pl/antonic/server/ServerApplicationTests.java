package pl.antonic.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class ServerApplicationTests extends OpenCVTest {

	@Autowired
	private ImageProcessingController imageProcessingController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void loadContextTest() {
		assertNotNull(imageProcessingController);
	}

	@Test
	public void loadOpenCVTest() {
		loadOpenCV();
	}

	@Test
	public void photoProcessingEndpointTest() throws Exception {
		loadOpenCV();

		String path = this.getClass().getClassLoader().getResource("test.jpg").getPath();
		MockMultipartFile file =
				new MockMultipartFile("file", "file.jpg", "image/*", Files.readAllBytes(Paths.get(path)));

		String json = "{\"methods\":[{\"id\":\"HISTOGRAM_EQUALIZATION\",\"arguments\":[]}]}";

		this.mockMvc.perform(
				MockMvcRequestBuilders.multipart("/process_image")
						.file(file)
						.param("json", json))
				.andExpect(status().is(200));
	}

	@Test
	public void videoProcessingEndpointTest() throws Exception {
		loadOpenCV();

		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.mp4");
		byte[] bytes = new byte[input.available()];
		input.read(bytes);
		MockMultipartFile file =
				new MockMultipartFile("file", "file.mp4", "video/*", bytes);

		String json = "{\"methods\":[{\"id\":\"HISTOGRAM_EQUALIZATION\",\"arguments\":[]}]}";

		this.mockMvc.perform(
				MockMvcRequestBuilders.multipart("/process_video")
						.file(file)
						.param("json", json))
				.andExpect(status().is(200));

	}

	@Test
	public void testConnectionEndpointTest() throws Exception {
		this.mockMvc.perform(get("/test_connection")).andExpect(status().is(200));
	}
}
