package com.example.customcamera;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.android.camera.MosaicFrameProcessor;
import com.android.camera.MosaicPreviewRenderer;
import com.android.camera.PanoProgressBar;

public class ProgressDemoActivity extends Activity implements OnFrameAvailableListener {

	private static final String TAG = ProgressDemoActivity.class.getName();
	private MosaicPreviewRenderer mMosaicPreviewRenderer;
	private MosaicFrameProcessor mMosaicFrameProcessor;
	private SurfaceTexture mTexture;
	  private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
	  
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_demo);
		
		Log.i(TAG, "setup!");
		int[] textures = new int[1];
		// generate one texture pointer and bind it as an external texture.
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		// No mip-mapping with camera source.
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
		        GL10.GL_TEXTURE_MIN_FILTER,
		                        GL10.GL_LINEAR);        
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
		        GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		// Clamp to edge is only option.
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
		        GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
		        GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);


		int texture_id = textures[0];
		
		mMosaicPreviewRenderer = new MosaicPreviewRenderer(
				new SurfaceTexture(GL_TEXTURE_EXTERNAL_OES), 1024, 768, true);
		mTexture = mMosaicPreviewRenderer.getInputSurfaceTexture();

        mMosaicFrameProcessor = MosaicFrameProcessor.getInstance();
		
//		PanoProgressBar progressBar = (PanoProgressBar)findViewById(R.id.pano_pan_progress_bar);
//		progressBar.setProgress(10);
        

        // do we have a camera?
        if (!getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
          Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
              .show();
        } else {
          int cameraId = findFrontFacingCamera();
          if (cameraId < 0) {
            Toast.makeText(this, "No front facing camera found.",
                Toast.LENGTH_LONG).show();
          } else {
            final Camera camera = Camera.open(1);
            SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView1);
			final SurfaceHolder holder = view.getHolder();
			holder.addCallback(new Callback() {
				
				@Override
				public void surfaceDestroyed(SurfaceHolder arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder arg0) {
					if (camera != null)
						try {
//							camera.setPreviewDisplay(holder);
							camera.setPreviewTexture(mTexture);
 				            camera.startPreview();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
				}
			});
//            	view.getHolder();
				
    		mTexture.setOnFrameAvailableListener(this);
    		
          }
        }
	}
	 private int findFrontFacingCamera() {
		    int cameraId = -1;
		    // Search for the front facing camera
		    int numberOfCameras = Camera.getNumberOfCameras();
		    for (int i = 0; i < numberOfCameras; i++) {
		      CameraInfo info = new CameraInfo();
		      Camera.getCameraInfo(i, info);
		      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
		        Log.d(TAG, "Camera found");
		        cameraId = i;
		        break;
		      }
		    }
		    return cameraId;
		  }
	@Override
	public void onFrameAvailable(SurfaceTexture arg0) {
		System.out.println("piu");
	}


}
