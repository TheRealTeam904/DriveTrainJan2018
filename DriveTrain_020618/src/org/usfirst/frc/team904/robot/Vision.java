package org.usfirst.frc.team904.robot;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class Vision {
	
	public class TargetLocation {
		
		public TargetLocation(Point position, double area) {
			this.position = position;
			this.area = area;
		}
		
		public Point position;
		public double area;
	}
	
	public TargetLocation getTargetPositionInView(Mat view, Scalar targetColorBound1, Scalar targetColorBound2) {
		
		//////////////////////////////////////////////////////////////////////////////////////
		// To get a lot of useful information out of an image, we normally follow these steps:
		//
		// * Segment the image into a small number of different colors
		//     (in this case 2 colors, white and black, based on a fixed threshold)
		//
		// * (optionally) process the segmented image to remove small islands of each color
		//     (this can save significant time in the next step)
		//
		// * Find contours. Each contour is just a list of pixel locations around the
		//     edge of a color region
		//
		// * Calculate attributes, such as position, area, perimeter, of the contours.
		//     These attributes can easily be used directly in further calculations and
		//     decision-making.
		//
		///////////////////////////////////////////////////////////////////////////////////////
		
		Mat targetImg = new Mat();
		Core.inRange(              // wherever a pixel in: 
				view,              //   "view"
				targetColorBound1, //   is between this value
				targetColorBound2, //   and this value
				targetImg);        // the corresponding pixel in "targetImg" will be white
		
		// If the binary images are very noisy, uncomment these lines
		// and provide some _small_ values for erosion_amt and dilation_amt
		//Imgproc.erode(targetImg, targetImg, ImgProc.getStructuringElement(Imgproc.MORPH_RECT, erosion_amt));
		//Imgproc.dilate(targetImg, targetImg, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, dilation_amt));
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(               // for each contiguous region of white pixels in:
				targetImg,                  //   "targetImg"
				contours,                   //   put a corresponding contour in "contours",
				null,                       //   ignore information about which contours contain others,
				Imgproc.RETR_EXTERNAL,      //   only pay attention to the outermost contour of each region,
				Imgproc.CHAIN_APPROX_NONE); //   and this last parameter usually doesn't matter
		
		///////////////////////////////////////////////////////////////////////////////////////////////
		// Everything after here is extracting information from the contours we just found.
		//
		// Imgproc.contourArea gets us the area of a contour.
		// Imgproc.moments gets us the moments of a contour, from which we can calculate the position.
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		int indexOfLargestContour = 0;
		// Find the index of the contour with the largest area.
		double areaOfLargestContour = 0;
		for(int i = 0; i < contours.size(); i++) {
			if(Imgproc.contourArea(contours.get(i)) > areaOfLargestContour) {
				areaOfLargestContour = Imgproc.contourArea(contours.get(i));
				indexOfLargestContour = i;
			}
		}
		
		// Calculate mean X and Y from moments of the largest contour
		Moments moments = Imgproc.moments(contours.get(indexOfLargestContour));
		double meanX = moments.m10 / moments.m00;
		double meanY = moments.m01 / moments.m00;
		
		return new TargetLocation(
				new Point(meanX, meanY),
				areaOfLargestContour);
	}
}
