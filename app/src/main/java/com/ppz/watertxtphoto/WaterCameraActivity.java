package com.ppz.watertxtphoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.sticker.StickerModel;
import com.huantansheng.easyphotos.ui.adapter.TextStickerAdapter;
import com.huantansheng.easyphotos.utils.bitmap.SaveBitmapCallBack;
import com.ppz.watertxtphoto.view.CameraPreview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WaterCameraActivity extends AppCompatActivity implements TextStickerAdapter.OnItemClickListener,
        View.OnClickListener, SurfaceHolder.Callback {


    //贴图相关
    private RecyclerView rvPuzzleTemplet;
    private TextStickerAdapter textStickerAdapter;

    private RelativeLayout mRootView;

    private StickerModel stickerModel;

    //相机
    private SurfaceView mSv;
    private SurfaceHolder mSurfaceHolder;
    private android.hardware.Camera mCamera;
    private Bitmap bitmap;
    private android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_water_camera);
        initUI();
    }

    private void initUI() {

        stickerModel = new StickerModel();

        mRootView = findViewById(R.id.m_root_view);

        rvPuzzleTemplet = (RecyclerView) findViewById(R.id.rv_puzzle_template);
        rvPuzzleTemplet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        textStickerAdapter = new TextStickerAdapter(this, this);

        rvPuzzleTemplet.setAdapter(textStickerAdapter);


        findViewById(R.id.okBtn).setOnClickListener(this);


        // Create an instance of Camera
        mSv = (SurfaceView) findViewById(R.id.mySv);

        mSv.setFocusable(true);
        mSurfaceHolder = mSv.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
        // 为了实现照片预览功能，需要将SurfaceHolder的类型设置为PUSH,这样画图缓存就由Camera类来管理，画图缓存是独立于Surface的
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void onItemClick(String stickerValue) {

        Log.e("AAAA", stickerValue);
        stickerModel.addTextSticker(this, getSupportFragmentManager(), stickerValue, mRootView);
        //stickerModel.addTextSticker(this, getSupportFragmentManager(), stickerValue, mRootView);
    }







    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.okBtn:

                screenshot(mSv);
                break;
        }
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 98;
        while (baos.toByteArray().length / 1024 > 3072) { // 循环判断如果压缩后图片是否大于 3Mb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 2;// 每次都减少2
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public  Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        cv.save();//保存
        //store
        cv.restore();//存储
        return newbmp;
    }


    Bitmap mScreenBitmap = null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void screenshot(SurfaceView view){



        //需要截取的长和宽
        int outWidth = view.getWidth();
        int outHeight = view.getHeight();

        mScreenBitmap = Bitmap.createBitmap(outWidth, outHeight,Bitmap.Config.ARGB_8888);
        PixelCopy.request(view, mScreenBitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult){
                if (PixelCopy.SUCCESS == copyResult) {

                    Log.i("gyx","SUCCESS ");


                    Bitmap bitmap1 = stickerModel.saveBitMap(WaterCameraActivity.this, mRootView);

                    Log.e("AA", "AAA");

                    Bitmap saveBitmap = toConformBitmap(mScreenBitmap, bitmap1);

                    Log.e("AAA", "22");

                    EasyPhotos.saveBitmapToDir(WaterCameraActivity.this, saveBitmap, new SaveBitmapCallBack() {
                        @Override
                        public void onSuccess(String path) {

                            Toast.makeText(WaterCameraActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailed(Exception exception) {

                            Toast.makeText(WaterCameraActivity.this, "保存失败！", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCreateDirFailed() {

                        }
                    });


                } else {
                    Log.i("gyx","FAILED");
                    // onErrorCallback()
                }
            }
        }, new Handler());



    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        startCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        releaseCamera();
    }


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
            mCamera.setPreviewDisplay(mSurfaceHolder);

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
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
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
