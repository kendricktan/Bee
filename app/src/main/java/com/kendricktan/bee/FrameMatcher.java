package com.kendricktan.bee;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FrameMatcher {
    // Temp ROI
    public Rect tempROI = new Rect(50, 50, 200, 200);

    private Mat template, templateRGBA, templateGray, templateCanny;
    private Mat frame, frameGray, frameCanny;
    private Mat drawnFrame;

    public FrameMatcher(){
        this.template = null;
        this.frameCanny = new Mat();
    }

    // Resize frame according to a scale
    public Mat resizeFrameWidth(Mat inFrame, double newWidth){
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

        // Resize  and return resized frame
        Mat rFrame = new Mat();
        Imgproc.resize(inFrame, rFrame, dim, 0, 0, Imgproc.INTER_AREA);

        return rFrame;
    }


    // Detect edges
    public void match(){
        // If we don't have template, just don't bother matching
        if (this.template == null){
            Imgproc.rectangle(this.drawnFrame, new Point(tempROI.x, tempROI.y), new Point(tempROI.x + tempROI.width, tempROI.y + tempROI.height-2), new Scalar(255, 0, 0, 255), 2);
            return;
        }

        // Result matrix
        int result_cols = this.frame.cols() - this.templateCanny.cols() + 1;
        int result_rows = this.frame.rows() - this.templateCanny.rows() + 1;
        Mat result = new Mat();

        // Matching and normalize
        Imgproc.matchTemplate(this.frameCanny, this.templateCanny, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Localizing best match
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;

        Rect roi = new Rect((int)matchLoc.x, (int)matchLoc.y, this.templateCanny.cols(), this.templateCanny.rows());
        Imgproc.rectangle(this.drawnFrame, new Point(roi.x, roi.y), new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(255, 0, 0, 255), 2);

    }

    // Setters
    public void setTemplate(String filepath){
        Mat m = new Mat();

        m = Imgcodecs.imread(filepath, Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        this.setTemplate(m);
    }

    public void setTemplate(Mat roi){
        this.templateGray = new Mat();
        this.templateCanny = new Mat();

        this.template = roi;
        this.templateRGBA = new Mat(this.template.size(), CvType.CV_32F);

        Imgproc.cvtColor(this.template, this.templateRGBA, Imgproc.COLOR_BGR2RGBA);
        Imgproc.cvtColor(this.template, this.templateGray, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale

        // Canny
        Mat blur = new Mat();
        Imgproc.blur(this.template, blur, new Size(3, 3));
        Imgproc.Canny(blur, this.templateCanny, 50, 200);
    }

    public void setFrame(CvCameraViewFrame frame){
        this.frame = frame.rgba();
        this.drawnFrame = frame.rgba();
        this.frameGray = frame.gray();

        // Canny
        Mat blur = new Mat();
        Imgproc.blur(this.frame, blur, new Size(3, 3));
        Imgproc.Canny(blur, this.frameCanny, 30, 200);
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

    public Mat getFrameCanny(){
        return this.frameCanny;
    }

    public Mat getDrawnFrame(){
        return this.drawnFrame;
    }

}
