package com.kendricktan.bee;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FilenameFilter;

public class CameraPreviewMain extends Activity implements CameraBridgeViewBase.CvCameraViewListener2  {
    // Our image matcher class
    private FrameMatcher frameMatcher = new FrameMatcher();

    // Logging Tag
    private static final String TAG = "Bee::Activity";

    // OpenCV Camera instants
    private CameraBridgeViewBase mOpenCvCameraView;

    // Selected images
    private File[] previewImageList = null;
    private int selectedPreviewImageIndex = 0;

    // Get images in file path
    static final String[] EXTENSIONS = new String[]{
            "jpg", "png", "jpeg"
    };
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    // Initialization
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera_preview_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Select folder button
        Button folderbtn = (Button)findViewById(R.id.Button_select_folder);
        folderbtn.setOnClickListener(new View.OnClickListener(){
            private String m_chosenDir = "";
            private boolean m_newFolderEnabled = true;
            @Override
            public void onClick(View v){
                // Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                    new DirectoryChooserDialog(CameraPreviewMain.this,
                        new DirectoryChooserDialog.ChosenDirectoryListener()
                        {
                            @Override
                            public void onChosenDir(String chosenDir)
                            {
                                // Our chosen directory
                                m_chosenDir = chosenDir;

                                // Gets the Files
                                File dir = new File(m_chosenDir);

                                // And filters them (we only want images)
                                previewImageList = dir.listFiles(IMAGE_FILTER);
                                selectedPreviewImageIndex = 0;

                                // Change imageview image
                                ImageView imgViewer = (ImageView) findViewById(R.id.image_preview);
                                Bitmap selectedImage = BitmapFactory.decodeFile(previewImageList[selectedPreviewImageIndex].getAbsolutePath());
                                imgViewer.setImageBitmap(selectedImage);
                                frameMatcher.setTemplate(selectedImage);

                                // User feedback
                                Toast.makeText(CameraPreviewMain.this, "Chosen directory: " + chosenDir, Toast.LENGTH_LONG).show();
                            }
                        });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = ! m_newFolderEnabled;
            }
        });

        // Next button
        Button nextBtn = (Button)findViewById(R.id.Button_next);
        nextBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // Change imageview image
                if (selectedPreviewImageIndex < previewImageList.length){
                    selectedPreviewImageIndex ++;
                }

                ImageView imgViewer = (ImageView) findViewById(R.id.image_preview);
                Bitmap selectedImage = BitmapFactory.decodeFile(previewImageList[selectedPreviewImageIndex].getAbsolutePath());
                imgViewer.setImageBitmap(selectedImage);
                frameMatcher.setTemplate(selectedImage);
            }
        });

        // Prev button
        Button prevBtn = (Button)findViewById(R.id.Button_prev);
        prevBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // Change imageview image
                if (selectedPreviewImageIndex > 0){
                    selectedPreviewImageIndex --;
                }

                ImageView imgViewer = (ImageView) findViewById(R.id.image_preview);
                Bitmap selectedImage = BitmapFactory.decodeFile(previewImageList[selectedPreviewImageIndex].getAbsolutePath());
                imgViewer.setImageBitmap(selectedImage);
                frameMatcher.setTemplate(selectedImage);
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public CameraPreviewMain() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        frameMatcher.setFrame(inputFrame);
        Log.w(TAG, frameMatcher.resize(frameMatcher.getFrameGray(), 0.3*frameMatcher.getFrameGray().size().width).size().width + "");
        return frameMatcher.getFrameGray();
    }
}
