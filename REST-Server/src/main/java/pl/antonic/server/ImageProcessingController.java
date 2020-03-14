package pl.antonic.server;

import com.google.gson.Gson;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.antonic.server.image.processing.ImageProcessor;
import pl.antonic.server.image.processing.StandardImageProcessor;
import pl.antonic.server.methods.ImageProcessingMethodWrapper;
import pl.antonic.server.methods.Methods;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;

@RestController
public class ImageProcessingController {

    ImageProcessor processor = new StandardImageProcessor();

    @GetMapping("test_connection")
    public @ResponseBody byte[] testConnection() throws IOException {
        return Files.readAllBytes(Paths.get("dummy"));
    }

    @PostMapping("process_video")
    public @ResponseBody byte[] processVideo(@RequestParam("file") MultipartFile file,
                                             @RequestParam("json") String json) throws IOException {

        ImageProcessingMethodWrapper options = new Gson().fromJson(json, ImageProcessingMethodWrapper.class);
        byte[] video = file.getBytes();

        String srcPath = System.currentTimeMillis() + ".mp4";
        FileOutputStream fileOutputStream = new FileOutputStream(srcPath);
        fileOutputStream.write(video);
        fileOutputStream.close();

        VideoCapture videoCapture = new VideoCapture(srcPath);

        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        double width = videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double height = videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        Mat src = new Mat();
        String newPath = System.currentTimeMillis() + ".mp4";

        if (options.contains(Methods.HISTOGRAM_CALCULATION)) {
            width = 512;
            height = 400;
        }

        VideoWriter videoWriter = new VideoWriter(newPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, new Size(width, height), true);
        while(videoCapture.read(src)) {
            src = processor.process(src, options);

            if (src.channels() == 1) {
                cvtColor(src, src, COLOR_GRAY2BGR);
            }

            videoWriter.write(src);
        }

        videoCapture.release();
        videoWriter.release();

        byte[] result = Files.readAllBytes(Paths.get(newPath));
        Files.delete(Paths.get(newPath));
        Files.delete(Paths.get(srcPath));
        System.out.println("Successfully performed video processing!");
        return result;
    }

    @PostMapping("process_image")
    public @ResponseBody byte[] processImage(@RequestParam("file") MultipartFile file,
                                             @RequestParam("json") String json) throws IOException {

        ImageProcessingMethodWrapper options = new Gson().fromJson(json, ImageProcessingMethodWrapper.class);

        byte[] image = file.getBytes();
        Mat src = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.IMREAD_COLOR);

        src = processor.process(src, options);

        String fileName = System.currentTimeMillis() + ".jpg";
        Imgcodecs.imwrite(fileName, src);

        byte[] result = Files.readAllBytes(Paths.get(fileName));
        Files.delete(Paths.get(fileName));
        System.out.println("Successfully performed image processing!");
        return result;
    }
}
