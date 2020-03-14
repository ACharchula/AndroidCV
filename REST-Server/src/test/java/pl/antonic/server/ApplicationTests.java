package pl.antonic.server;

import com.google.gson.Gson;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pl.antonic.server.image.processing.ImageProcessingMethodImpl;
import pl.antonic.server.methods.ImageProcessingMethodWrapper;
import pl.antonic.server.methods.Methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

public class ApplicationTests extends OpenCVTest {

    private String photoPath = this.getClass().getClassLoader().getResource("test.jpg").getPath();


    @Test
    public void methodWrapperDeserializationTest() {
        String json = "{\"methods\":[{\"id\":\"HISTOGRAM_EQUALIZATION\",\"arguments\":[]},{\"id\":\"EROSION\",\"arguments\":[\"10\",\"0\"]}]}";
        ImageProcessingMethodWrapper options = new Gson().fromJson(json, ImageProcessingMethodWrapper.class);

        assertNotNull(options);
        assertNotNull(options.getMethods());

        assertEquals(2, options.getMethods().size());

        assertEquals(Methods.HISTOGRAM_EQUALIZATION, options.getMethods().get(0).getId());
        assertEquals(Methods.EROSION, options.getMethods().get(1).getId());

        assertEquals(0, options.getMethods().get(0).getArguments().size());

        assertEquals(2, options.getMethods().get(1).getArguments().size());
        assertEquals("10", options.getMethods().get(1).getArguments().get(0));
        assertEquals("0", options.getMethods().get(1).getArguments().get(1));
    }

    @Test
    public void bgr2grayTest() {
        loadOpenCV();
        Mat src = Imgcodecs.imread(photoPath, Imgcodecs.IMREAD_COLOR);
        assertNotNull(ImageProcessingMethodImpl.histogramEqualization(src));
    }

    @Test
    public void gray2bgrTest() {
        loadOpenCV();
        Mat src = Imgcodecs.imread(photoPath, Imgcodecs.IMREAD_COLOR);
        Imgproc.cvtColor(src, src, COLOR_BGR2GRAY);
        assertNotNull(ImageProcessingMethodImpl.gaussianBlur(src, 10));
    }

}
