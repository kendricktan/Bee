package com.kendricktan.bee;

import android.graphics.Bitmap;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FrameMatcher {
    private Mat template, templateGray, templateCanny;
    private Mat frame, frameGray;

    public FrameMatcher(){}

    // Resize frame according to a scale
    public Mat resize(Mat inFrame, double newWidth){
        double w = inFrame.size().width;
        double h = inFrame.size().height;

        // Can't resize empty image
        if (w <= 0 || h <= 0){
            return inFrame;
        }

        // Ratio of scaling
        double r = newWidth/w;

        // Our new dimension
        Size dim = new Size(newWidth, h*r);

        // Resize frame
        Mat rFrame = new Mat();

        Imgproc.resize(inFrame, rFrame, dim);

        return rFrame;
    }

    // Detect edges
    public Mat match(){
        return null;
    }

    // Setters
    public void setTemplate(Bitmap template){
        Utils.bitmapToMat(template, this.template); // Convert Bitmap to bitmap
        Imgproc.cvtColor(this.template, this.templateGray, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale

        // Canny
        Mat blur = new Mat();
        Imgproc.blur(this.template, blur, new Size(3, 3));
        Imgproc.Canny(blur, this.templateCanny, 10, 100, 3, true);
    }

    public void setFrame(CvCameraViewFrame frame){
        this.frame = frame.rgba();
        this.frameGray = frame.gray();
    }

    // Getters
    public Mat getTemplate(){
        return this.template;
    }

    public Mat getTemplateGray(){
        return this.templateGray;
    }

    public Mat getTemplateCanny(){
        return this.templateCanny;
    }

    public Mat getFrame(){
        return this.frame;
    }

    public Mat getFrameGray(){
        return this.frameGray;
    }


}
