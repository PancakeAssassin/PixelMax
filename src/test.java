import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class test {

	/**
	 * @param args
	 */

	static String test= "TestImages/test.png";
	static String test1= "TestImages/test1.png";
	static String test2= "TestImages/test2.png";
	static String stitch1= "TestImages/stitch1.jpg";
	static String stitch2= "TestImages/stitch2.jpg";
	
	public static void main(String[] args) {
		// TODO Autoimport org.opencv.core.Core;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat cannyTransform= ImageProc.performCannyTransform(test);
		Highgui.imwrite("TestImages/cannyResult.png", cannyTransform);
		
		Mat equalization= ImageProc.equalizeImage(test);
		Highgui.imwrite("TestImages/equalize.png", equalization);
		
		Mat blend= ImageProc.blendImages(test1, test2);
		Highgui.imwrite("TestImages/blend.png", blend);
		
		Mat line= ImageProc.lineTransform(test);
		Highgui.imwrite("TestImages/hough.png", line);
		
		Mat sharp= ImageProc.sharpenImage(test);
		Highgui.imwrite("TestImages/sharp.png", sharp);
		
		Mat stitched= ImageProc.stitchImages(stitch1, stitch2);
		Highgui.imwrite("/TestImages/stitched.jpg", stitched);
	}
}

