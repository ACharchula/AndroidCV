package pl.antonic.server.image.processing;

import org.opencv.core.Mat;
import pl.antonic.server.methods.ImageProcessingMethodWrapper;

public interface ImageProcessor {

    Mat process(Mat src, ImageProcessingMethodWrapper options);
}
