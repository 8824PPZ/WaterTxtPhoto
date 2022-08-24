# Android 水印相机
**贴图控件：** [EasyPhotos](https://github.com/HuanTanSheng/EasyPhotos "EasyPhotos")

**SurfaceView截图**:
```java
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

                    Bitmap saveBitmap = toConformBitmap(mScreenBitmap, bitmap1);


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


```

**位图合并**：
```java
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
```
