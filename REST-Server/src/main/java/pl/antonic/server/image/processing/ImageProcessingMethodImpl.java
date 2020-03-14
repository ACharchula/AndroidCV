package pl.antonic.server.image.processing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.*;

public class ImageProcessingMethodImpl {

    private ImageProcessingMethodImpl() { }

    public static Mat bilateralFilter(Mat src, int diameter) {
        src = gray2bgr(src);
        Mat dst = new Mat();
        Imgproc.bilateralFilter(src, dst, diameter, (double) diameter * 2, (double) diameter / 2);
        return dst;
    }

    public static Mat gaussianBlur(Mat src, int kernelSize) {
        src = gray2bgr(src);
        Mat dst = new Mat();
        int kernel = (kernelSize * 2) + 1;
        Imgproc.GaussianBlur(src, dst, new Size(kernel, kernel), 0, 0);
        return dst;
    }

    public static Mat erosion(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.erode(src, dst, element);
        return dst;
    }

    public static Mat dilation(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.dilate(src, dst, element);
        return dst;
    }

    public static Mat opening(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.morphologyEx(src, dst, MORPH_OPEN, element);
        return dst;
    }

    public static Mat closing(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.morphologyEx(src, dst, MORPH_CLOSE, element);
        return dst;
    }

    public static Mat gradient(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.morphologyEx(src, dst, MORPH_GRADIENT, element);
        return dst;
    }

    public static Mat topHat(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.morphologyEx(src, dst, MORPH_TOPHAT, element);
        return dst;
    }

    public static Mat blackHat(Mat src, int selectedType, int kernelSize) {
        src = gray2bgr(src);
        Mat element = getElement(selectedType, kernelSize);
        Mat dst = new Mat();
        Imgproc.morphologyEx(src, dst, MORPH_BLACKHAT, element);
        return dst;
    }

    public static Mat lineExtract(Mat src, int vertical) {
        Mat gray = bgr2gray(src);

        Mat bw = new Mat();

        Core.bitwise_not(gray, gray);
        Imgproc.adaptiveThreshold(gray, bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);

        Mat result = bw.clone();

        if (vertical == 0) {
            int verticalSize = result.rows() / 30;
            Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 1,verticalSize));
            Imgproc.erode(result, result, verticalStructure);
            Imgproc.dilate(result, result, verticalStructure);
        } else {
            int horizontalSize = result.cols() / 30;
            Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalSize,1));
            Imgproc.erode(result, result, horizontalStructure);
            Imgproc.dilate(result, result, horizontalStructure);
        }
        Core.bitwise_not(result, result);
        Mat edges = new Mat();
        Imgproc.adaptiveThreshold(result, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, -2);
        Mat kernel = Mat.ones(2, 2, CvType.CV_8UC1);
        Imgproc.dilate(edges, edges, kernel);
        Mat smooth = new Mat();
        result.copyTo(smooth);
        Imgproc.blur(smooth, smooth, new Size(2, 2));
        smooth.copyTo(result, edges);
        return result;
    }

    private static Mat getElement(int selectedType, int kernelSize) {
        int erosionType = 0;

        if (selectedType == 0) {
            erosionType = MORPH_RECT;
        } else if (selectedType == 1) {
            erosionType = MORPH_CROSS;
        } else if (selectedType == 2) {
            erosionType = MORPH_ELLIPSE;
        }

        return getStructuringElement(erosionType, new Size((2*kernelSize + 1), (2*kernelSize + 1)), new Point(kernelSize, kernelSize));
    }

    public static Mat basicThreshold(Mat src, int threshold, int type) {
        Mat gray = bgr2gray(src);
        Mat dst = new Mat();
        Imgproc.threshold(gray, dst, threshold, 255, type);
        return dst;

    }

    public static Mat histogramEqualization(Mat src) {
        src = bgr2gray(src);
        Mat dst = new Mat();
        Imgproc.equalizeHist(src, dst);
        return dst;
    }

    public static Mat contoursFinding(Mat src, int threshold) {
        Random rng = new Random(12345);
        src = bgr2gray(src);
        Imgproc.blur(src, src, new Size(3, 3));
        Mat cannyOutput = new Mat();

        Imgproc.Canny(src, cannyOutput, threshold, threshold * 2);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); ++i) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, new Point());
        }

        return drawing;
    }

    public static Mat sobelDerivatives(Mat src) {
        Mat src_gray = bgr2gray(src);
        Imgproc.GaussianBlur(src_gray, src_gray, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        Imgproc.Sobel( src_gray, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT );
        Imgproc.Sobel( src_gray, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0, Core.BORDER_DEFAULT );
        // converting back to CV_8U
        Mat grad = new Mat();
        Core.convertScaleAbs( grad_x, abs_grad_x );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );
        return grad;
    }

    public static Mat laplaceOperator(Mat src) {
        Mat src_gray = bgr2gray(src);
        Imgproc.GaussianBlur( src_gray, src_gray, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
        Mat abs_dst = new Mat();
        Mat dst = new Mat();
        Imgproc.Laplacian( src_gray, dst, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT );
        // converting back to CV_8U
        Core.convertScaleAbs( dst, abs_dst );
        return abs_dst;
    }

    public static Mat histogramCalculation(Mat src) {
        src = gray2bgr(src);

        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);
        int histSize = 256;
        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );
        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);
        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }
        return histImage;
    }

    public static Mat convexHull(Mat src, int threshold) {
        Random rng = new Random(12345);
        Mat srcGray = bgr2gray(src);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contours, i, color);
            Imgproc.drawContours(drawing, hullList, i, color );
        }

        return drawing;
    }

    public static Mat cannyEdgeDetector(Mat src, int threshold) {
        Mat gray = bgr2gray(src);
        Mat srcBlur = new Mat();
        Imgproc.blur(gray, srcBlur, new Size(3, 3));
        Mat detectedEdges = new Mat();
        Imgproc.Canny(srcBlur, detectedEdges, threshold, threshold * 3, 3, false);
        Mat dst = new Mat(gray.size(), CvType.CV_8UC3, Scalar.all(0));
        gray.copyTo(dst, detectedEdges);
        return dst;
    }

    private static Mat bgr2gray(Mat src) {
        Mat gray = new Mat();

        if (src.channels() == 3) {
            cvtColor(src, gray, COLOR_BGR2GRAY);
        } else {
            gray = src;
        }

        return gray;
    }

    private static Mat gray2bgr (Mat src) {
        Mat color = new Mat();

        if (src.channels() == 1) {
            cvtColor(src, color, COLOR_GRAY2BGR);
        } else {
            color = src;
        }

        return color;
    }
}
