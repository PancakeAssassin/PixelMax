import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class ImageProc {

	public static final int REFLECT_OVER_X_AXIS= 0;
	public static final int REFLECT_ACROSS_Y_AXIS= 1;
	
	public static Mat sharpenImage(String filename)
	{
		Mat image= LoadImage(filename);
		Mat destination= new Mat(image.rows(), image.cols(), image.type());
		Imgproc.GaussianBlur(image, destination, new Size(0,0), 10);
		Core.addWeighted(image, 1.5, destination, -0.5, 0, destination);
		
		return destination;
	}
	
	public static Mat lineTransform(String filename)
	{
		Mat originalImage= LoadImage(filename);
		
		if(originalImage.empty())
		{
			System.out.println("Cannot open file");
			return null;
		}
		
		Mat lineDetect= performCannyTransform(filename);
		
		Mat lines = new Mat();
		Imgproc.HoughLinesP(lineDetect, lines, 1, Math.PI/180, 100, 20, 20);
		for(int x= 0; x < lines.cols(); x++)
		{
			double[] vec= lines.get(0, x);
			double x1= vec[0];
			double y1= vec[1];
			double x2= vec[2];
			double y2= vec[3];
			
			Point start= new Point(x1, y1);
			Point end= new Point(x2, y2);
			
			Core.line(originalImage, start, end, new Scalar(255,0 , 0), 2);
		}
		
		return originalImage;
	}
	
	public static Mat performCannyTransform(String filename)
	{
		Mat originalImage= LoadImage(filename);
		
		if(originalImage.empty())
		{
			System.out.println("Cannot open file");
			return null;
		}
		
		Imgproc.cvtColor(originalImage, originalImage, Imgproc.COLOR_RGBA2GRAY);
		Imgproc.GaussianBlur(originalImage, originalImage, new Size(5, 5), 2, 2);
		
		int CannyLowerThreshold = 35;
		int CannyUpperThreshold = 75;
		
		Imgproc.Canny(originalImage, originalImage, CannyLowerThreshold, CannyUpperThreshold);
		
		return originalImage;
	}
	
	public static Mat equalizeImage(String filename)
	{
		Mat originalImage = LoadImage(filename);
		
		if(originalImage.empty())
		{
			System.out.println("Cannot open file");
			return null;
		}
		
		Imgproc.cvtColor(originalImage, originalImage, Imgproc.COLOR_BGR2YCrCb);
		List<Mat> channels= new ArrayList<Mat>();
		for(int i= 0; i< originalImage.channels(); i++)
		{
			Mat channel= new Mat();
			channels.add(channel);
		}
		Core.split(originalImage, channels);
		
		Imgproc.equalizeHist(channels.get(0), channels.get(0));
		
		Mat result = originalImage;
		Core.merge(channels, originalImage);
		Imgproc.cvtColor(originalImage, result, Imgproc.COLOR_YCrCb2BGR);
		
		return result;
	}
	
	public static Mat blendImages(String file1, String file2)
	{
		Mat oImage= LoadImage(file1);
		Mat otherImage= LoadImage(file2);
		if(oImage== null || otherImage == null)
		{
			System.out.println("Unable to perform blend on images");
			return null;
		}
		
		if((oImage.width() != otherImage.width()) || (oImage.height() != otherImage.height()))
		{
			System.out.println("Unable to blend images of unlike sizes");
			return oImage;
		}
		double alpha= 0.5;
		double beta= (1-alpha);
		
		Core.addWeighted(oImage, alpha, otherImage, beta, 0.0, oImage);
		
		return oImage;
	}
	
	public static Mat perspectiveTransform(String filename)
	{
		Mat originalImage= LoadImage(filename);
		
		Imgproc.getPerspectiveTransform(originalImage, originalImage);
		
		return originalImage;
	}
	
	public static Mat scale(String filename, float scaleBy)
	{
		Mat image = LoadImage(filename);
		Mat scaledImage= new Mat((int)(image.rows() * scaleBy), (int)(image.cols() *scaleBy), image.type());
		
		Imgproc.resize(image, scaledImage, scaledImage.size());
		return scaledImage;
	}
	
	public static Mat reflect(String filename, int flipCode)
	{
		Mat image= LoadImage(filename);
		Core.flip(image, image, flipCode);
		return image;
	}
	
	//input should be divisible by 90
	//quick and simple matrix manipulation to rotate by increments of 90
	public static Mat rotateBy90(String filename, int degrees)
	{
		Mat image= LoadImage(filename);
		
		//find the number of rotations for the transformation
		int numRot= degrees / 90;
		//there are only 4 different positions an image can take
		numRot= numRot % 4;
		
		Mat rotateDest= null;
		if(numRot == 1)
		{
			rotateDest= new Mat(image.rows(), image.cols(), image.type());
			rotateDest= image.t();
		}
		else if(numRot == 2)
		{
			rotateDest= new Mat(image.cols(), image.rows(), image.type());
			rotateDest= new Mat();
			Core.flip(image, rotateDest, -1);
		}
		else if(numRot == 3)
		{
			rotateDest= new Mat(image.rows(), image.cols(), image.type());
			rotateDest = image.t();
			Core.flip(rotateDest, rotateDest, -1);
		}
		return rotateDest;
	}
	
	
	public static Mat stitchImages(String file1, String file2)
	{
		
		FeatureDetector fd= FeatureDetector.create(FeatureDetector.SURF);
		DescriptorExtractor fe= DescriptorExtractor.create(DescriptorExtractor.ORB);
		DescriptorMatcher fm= DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		
		Mat image1= LoadImage(file1);
		Imgproc.cvtColor(image1, image1, Imgproc.COLOR_RGBA2GRAY);
		
		Mat image2= LoadImage(file2);
		Imgproc.cvtColor(image2, image2, Imgproc.COLOR_RGBA2GRAY);
		
		
		//structure for keypoints
		MatOfKeyPoint kp1= new MatOfKeyPoint();
		MatOfKeyPoint kp2= new MatOfKeyPoint();
		
		//structures for the computed descriptors
		Mat descriptor1= new Mat();
		Mat descriptor2= new Mat();
		
		//structure for matches
		MatOfDMatch matches= new MatOfDMatch();
		
		//get the keypoints
		fd.detect(image1, kp1);
		fd.detect(image2, kp2);
		
		//get the descriptors from keypoints
		fe.compute(image1, kp1, descriptor1);
		fe.compute(image2, kp2, descriptor2);
		
		//get the matches between the 2 sets
		fm.match(descriptor2, descriptor1, matches);
		
		//make the matches into a list
		List<DMatch> matchesList= matches.toList();
		
		double maxDist= 0.0;
		double minDist= 100.0;
		
		//calculate max & min distances between kps
		for(int i= 0; i<kp2.rows(); i++)
		{
			double dist= (double) matchesList.get(i).distance;
			if(dist < minDist)
				minDist= dist;
			if(dist > maxDist)
				maxDist= dist;
		}
		
		//structure for good matches
		LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
		
		//use only good matches
		for(int i= 0; i<descriptor2.rows(); i++)
		{
			if(matchesList.get(i).distance < 3*minDist)
			{
				goodMatches.addLast(matchesList.get(i));
			}
		}
		
		//structure to hold points of the good matches
		LinkedList<Point> objList = new LinkedList<Point>();
		LinkedList<Point> sceneList= new LinkedList<Point>();
		
		List<KeyPoint> kp_objectList= kp1.toList();
		List<KeyPoint> kp_sceneList= kp2.toList();
		
		//put the points of good matches into the structures
		for(int i= 0; i<goodMatches.size(); i++)
		{
			objList.addLast(kp_objectList.get(goodMatches.get(i).trainIdx).pt);
			sceneList.addLast(kp_sceneList.get(goodMatches.get(i).queryIdx).pt);
		}
		
		MatOfDMatch gm= new MatOfDMatch();
		gm.fromList(goodMatches);
		
		//convert the points into the appropriate data structure
		MatOfPoint2f obj = new MatOfPoint2f();
		obj.fromList(objList);
		
		MatOfPoint2f scene= new MatOfPoint2f();
		scene.fromList(sceneList);
		
		Mat H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 1);
		Mat result= new Mat();
		
		Mat obj_corners= new Mat(4, 1, CvType.CV_32FC2);
		Mat scene_corners= new Mat(4, 1, CvType.CV_32FC2);
		
		obj_corners.put(0, 0, new double[]{0,0});
		obj_corners.put(0, 0, new double[]{image1.cols(),0});
		obj_corners.put(0, 0, new double[]{image1.cols(),image1.rows()});
		obj_corners.put(0, 0, new double[]{0,image1.rows()});
		
		Core.perspectiveTransform(obj_corners, scene_corners, H);
		
		//structure to hold the result of the homography matrix
		
		
		Size size= new Size(image1.cols()+image2.cols(), image1.rows());
		
		Mat cImage1= LoadImage(file1);
		Mat cImage2= LoadImage(file2);
		
		//use homography matrix to warp the two images
		Imgproc.warpPerspective(cImage1, result, H, size);
		int i= cImage1.cols();
		Mat m = new Mat(result, new Rect(i, 0, cImage2.cols(), cImage2.rows()));
		
		cImage2.copyTo(m);
		return result;
	}
	
	public static Mat generateMosaic(String largeImage, String[] smallerImages)
	{
		//load the larger image
		Mat originalImage= LoadImage(largeImage);
		if(originalImage == null)
		{
			System.out.println("Unable to create image mosaic from given image.");
			return null;
		}
		
		//load the image
		Mat[] images= new Mat[smallerImages.length];
		for(int i= 0; i < smallerImages.length; i++)
		{
			images[i]= LoadImage(smallerImages[i]);
		}
		
		//find out the number of rows and columns of smaller images in the larger image
		int rows= (int)(originalImage.height() / images[0].height());
		int cols= (int) (originalImage.width() / images[0].width());
		
		//find the average pixel value for an image
		Map<Float,Mat> colorValues;
		for(int i = 0; i < smallerImages.length; i++)
		{
			float result= 0;
			for(int j= 0; j<smallerImages[i].length(); j++)
			{
				
			}
		}
		return originalImage;
	}
	
	public static Mat LoadImage(String filename)
	{
		Mat image= Highgui.imread(filename);
		
		if(image.empty())
		{
			System.out.println("Image not loaded");
			return null;
		}
		
		return image;
	}
	
	public static Mat convertFromBufferedImage(BufferedImage imageData)
	{
		byte[] pixels= ((DataBufferByte) imageData.getRaster().getDataBuffer()).getData();
		
		Mat image= new Mat(imageData.getHeight(), imageData.getWidth(), CvType.CV_8UC3);
		image.put(0, 0, pixels);
		
		return image;
	}
	
	public static Image convertToBufferedImage(Mat m)
	{
		int type= BufferedImage.TYPE_BYTE_GRAY;
		if(m.channels() > 1)
		{
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize= m.channels()*m.cols()*m.rows();
		byte[] b= new byte[bufferSize];
		
		m.get(0, 0, b);
		BufferedImage image= new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels= ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
}
