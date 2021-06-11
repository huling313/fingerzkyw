package cn.org.bjca.finger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ImageUtils {

    public ImageUtils() {

    }


    public static File compressImage(Bitmap bitmap, Context context) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        Date date = new Date();
        //图片名
        String filename = date.getTime() + "";

        //保存到当前app缓存目录下
        File file = new File(context.getExternalCacheDir(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        return file;
    }


    /**
     * 将两张位图拼接成一张(横向拼接)
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap combineImage(Bitmap first, Bitmap second) {
        int width = first.getWidth() + second.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        //靠右居中显示
        canvas.drawBitmap(second, first.getWidth(), 0, null);
        return result;
    }


    /**
     * 替换图片颜色
     *
     * @param srcBitmap
     * @param color
     * @return
     */
    public static Bitmap replaceBitmapColor(Bitmap srcBitmap, int color) {
        Bitmap result = srcBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int nWidth = result.getWidth();
        int nHeight = result.getHeight();
        for (int y = 0; y < nHeight; ++y)
            for (int x = 0; x < nWidth; ++x) {
                int nPixelColor = result.getPixel(x, y);

                int r = Color.red(nPixelColor);
                int g = Color.green(nPixelColor);
                int b = Color.blue(nPixelColor);
                int a = Color.alpha(nPixelColor);

                if ((0 <= r && r < 180)
                        && (0 <= g && g < 180)
                        && (0 <= b && b < 180)) {

                    int newColor = Color.argb(a, Color.red(color) + (int) (Math.random() * 10), Color.green(color), Color.blue(color));
                    result.setPixel(x, y, newColor);

                } else {
                    result.setPixel(x, y, Color.TRANSPARENT);
                }
//                if (nPixelColor != bgColor)
            }
        return result;
    }

    /**
     * 图片压缩
     *
     * @param position
     * @param bitMap
     * @return
     */
    public static Bitmap imageZoom(int position, Bitmap bitMap) {
        //图片允许最大空间
        double maxSize = 200;
        double bitmapSize = bitMap.getByteCount() / 1024;
        Log.e("derrick", "bitmap 0 = " + bitmapSize + " , index = " + position);
        if (bitmapSize < maxSize) {
            return bitMap;
        }
        if (bitmapSize > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double scale = bitmapSize / maxSize;
            //开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            Bitmap result = getNewImage(bitMap, bitMap.getWidth() / Math.sqrt(scale), bitMap.getHeight() / Math.sqrt(scale));
            Log.e("derrick", "bitmap 1 = " + result.getByteCount() / 1024 + " , index = " + position);
            return result;
        }
        return bitMap;
    }


    /**
     * 缩放成指定大小
     *
     * @param bgimage
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap getNewImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        Log.e("test", scaleWidth + "_____" + scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 获取照片角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static void setPicToView(Bitmap mBitmap, String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(fileName);
            // quality 图像压缩率，0-100。 0 压缩100%，100意味着不压缩；
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (b != null) {
                    b.flush();
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取固定尺寸的图片（比如400*160，则不管bitmap的尺寸是多少，最终都返回这个尺寸的图片）
     *
     * @param bitmap      原图片
     * @param resetWidth  固定尺寸的宽
     * @param resetHeight 固定尺寸的高
     * @return Bitmap  固定尺寸的图片
     */
    public static Bitmap fixSizeBitmap(Bitmap bitmap, int resetWidth, int resetHeight) {
        if (bitmap == null) {
            return null;
        }
        Bitmap ratioBitmap = resizeBitmapRatioSame(bitmap, resetWidth, resetHeight);
        // 先创建一个符合期望大小的空bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(resetWidth, resetHeight, Bitmap.Config.ARGB_8888);
        // 获取这个图片的宽和高
        float width = ratioBitmap.getWidth();
        float height = ratioBitmap.getHeight();
        float startX = ((float) (resetWidth - width)) / 2;
        float startY = ((float) (resetHeight - height)) / 2;
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(ratioBitmap, startX, startY, null);
        return resultBitmap;
    }

    /**
     * 获取等比例缩放后的图片（当图片小于预计尺寸时，不放大）
     *
     * @param bitmap    原图片
     * @param maxWidth  最大的宽
     * @param maxHeight 最大的高
     * @return Bitmap  等比缩放后的图片（图片更小不会放大）
     */
    public static Bitmap resizeBitmapRatioSame(Bitmap bitmap, int maxWidth, int maxHeight) {
        return resizeBitmapRatioSame(bitmap, maxWidth, maxHeight, false);
    }

    /**
     * 等比例缩放图片(缩放后的图片会的宽和高会比reset中的相等或者更小)
     *
     * @param bitmap    原图片
     * @param maxWidth  最大的宽
     * @param maxHeight 最大的高
     * @param zoomOut   当图片小于预计尺寸，是否对其放大
     * @return Bitmap
     */
    public static Bitmap resizeBitmapRatioSame(Bitmap bitmap, int maxWidth, int maxHeight, boolean zoomOut) {

        if (bitmap == null) {
            return null;
        }
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 计算宽高缩放率
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        // 当图片的长和宽都小于需要的尺寸，则不进行放大
        float scale = Math.min(scaleWidth, scaleHeight);
        if (!zoomOut) {
            scale = Math.min(1.0F, scale);
        }

        // 等比缩放图片
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

    public static Bitmap createWatermark(Bitmap bitmap, String mark, boolean isHaveFingerprint) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int x = 0;
        Bitmap bmp = Bitmap.createBitmap(w, h + 30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint p = new Paint();
        // 水印颜色
        p.setColor(Color.parseColor("#333333"));
        if (isHaveFingerprint) {
            x = 72;
            // 水印字体大小
            p.setTextSize(34);
        } else {
            x = 54;
            // 水印字体大小
            p.setTextSize(34);
        }
        //抗锯齿
        p.setAntiAlias(true);
        //绘制图像
        canvas.drawBitmap(bitmap, 0, 0, p);
        //绘制文字
        canvas.drawText(mark, x, h + 30, p);
        canvas.save();
        canvas.restore();
        return bmp;
    }

    //将图片转化为红色
    public static Bitmap changePng(Bitmap img){
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高
        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i = 0; i < height; i++)  {
            for(int j = 0; j < width; j++) {
                int  color = pixels[width * i + j];
                if (color!=Color.TRANSPARENT) {//过滤背景色
                    //然后将指纹对应的颜色修改为红色
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int al = Color.alpha(color);
                    //设置指纹的颜色为红色
                    pixels[width * i + j] = Color.argb(al, Color.red(Color.RED), 0, 0);
                }
            }
        }
        //重新生产图片，然后返回新的图片
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }


}
