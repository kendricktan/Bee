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
