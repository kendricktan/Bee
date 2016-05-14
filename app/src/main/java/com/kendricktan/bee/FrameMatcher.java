package com.kendricktan.bee;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FrameMatcher {
    private Mat template, templateGray, templateCanny;
    private Mat frame, frameGray;
    private Mat drawnFrame;

    public FrameMatcher(){

    }

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

        // Resize  and return resized frame
        Mat rFrame = new Mat();
        Imgproc.resize(inFrame, rFrame, dim);

        return rFrame;
    }

    // Detect edges
    public void match(){
        // Maximum correlation matching
        Core.MinMaxLocResult matchMax = new Core.MinMaxLocResult();
        matchMax.maxVal = 0;

        // Frame and template width and height

        Mat edged = new Mat();
        Mat result = new Mat();

        double frameWidth = this.frame.size().width;
        double frameHeight = this.frame.size().height;

        double templateWidth = this.template.size().width;
        double templateHeight = this.template.size().height;

        for (double i = 1.0; i >= 0.1; i-=(1.0-0.1)/20){
            double scale = ((double)Math.round(i * 100d) / 100d); // 2 decimal precision

            // Resize image according to scale
            Mat resized = this.resize(this.frameGray, frameWidth*scale);

            // If resized image is smaller than the template
            // then break from the loop
            if (resized.size().width < templateWidth || resized.size().height < templateHeight){
                break;
            }

            // Detect edges in the resized grayscale image
            // and apply template matching to find the template in the image
            // plus normalization
            Imgproc.Canny(resized, edged, 50, 200);
            Imgproc.matchTemplate(edged, this.templateCanny, result, Imgproc.TM_CCOEFF);
            Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

            // Localizing best match with minMaxLoc
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

            // Save new max correlation
            if (mmr.maxVal > matchMax.maxVal){
                matchMax = mmr;
            }
        }

        Point matchLoc = matchMax.maxLoc;
        Imgproc.rectangle(this.drawnFrame, matchLoc, new Point(matchLoc.x + this.template.cols(), matchLoc.y + this.template.rows()), new Scalar(0, 0, 255));
    }

    // Setters
    public void setTemplate(String filepath){
        this.templateGray = new Mat();
        this.templateCanny = new Mat();

        this.template = Imgcodecs.imread(filepath);
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

    public Mat getDrawnFrame(){
        return this.drawnFrame;
    }


}
