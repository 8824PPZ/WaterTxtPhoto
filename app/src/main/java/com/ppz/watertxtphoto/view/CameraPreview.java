package com.ppz.watertxtphoto.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        //
        startCamera();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    private android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
    private void startCamera() {
        try {
            mCamera = Camera.open(0);
            Camera.getCameraInfo(0, cameraInfo);
            Camera.Parameters parameters = mCamera.getParameters();
            // 设置图片格式
            parameters.setPictureFormat(ImageFormat.JPEG);
            // 设置照片质量
            parameters.setJpegQuality(100);
            // 首先获取系统设备支持的所有颜色特效，如果设备不支持颜色特性将返回一个null， 如果有符合我们的则设置
            List<String> colorEffects = parameters.getSupportedColorEffects();
            Iterator<String> colorItor = colorEffects.iterator();
            while (colorItor.hasNext()) {
                String currColor = colorItor.next();
                if (currColor.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    break;
                }
            }
            // 获取对焦模式
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // 设置自动对焦
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            // 设置闪光灯自动开启
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                // 自动闪光
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
            mCamera.setDisplayOrientation(setCameraDisplayOrientation());
            // 设置显示
            mCamera.setPreviewDisplay(mHolder);

            List<Camera.Size> photoSizes = parameters.getSupportedPictureSizes();//获取系统可支持的图片尺寸
            int width = 0, height = 0;
            for (Camera.Size size : photoSizes) {
                if (size.width > width) width = size.width;
                if (size.height > height) height = size.height;
            }
            parameters.setPictureSize(width, height);
            // 设置完成需要再次调用setParameter方法才能生效
            mCamera.setParameters(parameters);
            // 开始预览
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public int setCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);
        int rotation = 0;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
