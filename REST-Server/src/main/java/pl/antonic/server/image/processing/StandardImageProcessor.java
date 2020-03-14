package pl.antonic.server.image.processing;

import org.opencv.core.Mat;
import pl.antonic.server.methods.ImageProcessingMethod;
import pl.antonic.server.methods.ImageProcessingMethodWrapper;

public class StandardImageProcessor implements ImageProcessor {

    @Override
    public Mat process(Mat src, ImageProcessingMethodWrapper options) {
        for (ImageProcessingMethod method : options.getMethods()) {
            switch (method.getId()) {
                case CONTOURS_FINDING:
                    src = ImageProcessingMethodImpl.contoursFinding(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                case HISTOGRAM_EQUALIZATION:
                    src = ImageProcessingMethodImpl.histogramEqualization(src);
                    break;
                case BILATERAL_FILTER:
                    src = ImageProcessingMethodImpl.bilateralFilter(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                case GAUSSIAN_BLUR:
                    src = ImageProcessingMethodImpl.gaussianBlur(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                case EROSION:
                    src = ImageProcessingMethodImpl.erosion(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case DILATION:
                    src = ImageProcessingMethodImpl.dilation(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case OPENING:
                    src = ImageProcessingMethodImpl.opening(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case CLOSING:
                    src = ImageProcessingMethodImpl.closing(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case BLACK_HAT:
                    src = ImageProcessingMethodImpl.blackHat(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case TOP_HAT:
                    src = ImageProcessingMethodImpl.topHat(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case GRADIENT:
                    src = ImageProcessingMethodImpl.gradient(src, Integer.parseInt(method.getArguments().get(1)), Integer.parseInt(method.getArguments().get(0)));
                    break;
                case LINE_EXTRACT:
                    src = ImageProcessingMethodImpl.lineExtract(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                case BASIC_THRESHOLD:
                    src = ImageProcessingMethodImpl.basicThreshold(src, Integer.parseInt(method.getArguments().get(0)), Integer.parseInt(method.getArguments().get(1)));
                    break;
                case SOBEL_DERIVATIVES:
                    src = ImageProcessingMethodImpl.sobelDerivatives(src);
                    break;
                case LAPLACE_OPERATOR:
                    src = ImageProcessingMethodImpl.laplaceOperator(src);
                    break;
                case HISTOGRAM_CALCULATION:
                    src = ImageProcessingMethodImpl.histogramCalculation(src);
                    break;
                case CANNY_EDGE_DETECTOR:
                    src = ImageProcessingMethodImpl.cannyEdgeDetector(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                case CONVEX_HULL:
                    src = ImageProcessingMethodImpl.convexHull(src, Integer.parseInt(method.getArguments().get(0)));
                    break;
                default:
                    System.out.println("Unexpected value: " + method.getId());
                    break;
            }
        }

        return src;
    }
}
