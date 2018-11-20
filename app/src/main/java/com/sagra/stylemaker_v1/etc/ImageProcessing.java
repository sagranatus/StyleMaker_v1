package com.sagra.stylemaker_v1.etc;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;

import com.sagra.stylemaker_v1.DB.ClothDBSqlData;
import com.sagra.stylemaker_v1.DB.DBManager;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.server.ImageUpload;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;

public class ImageProcessing {
    static String check;


    static {
        System.loadLibrary("opencv_java3"); // 이게 없으면 안되는구나..
        System.loadLibrary("native-lib");
    }
    public static native void ShadowDetection(long mat, long result);


    public static Bitmap ImageProcess(Bitmap src_Bitmap, String checkcolor, String set2, String filterStatus) {
        check = checkcolor;
               // 필터없는 경우 그대로
        if (checkcolor.equals("필터없음")) {

            if(set2.equals("신발")){
                src_Bitmap = applyshoes(src_Bitmap, filterStatus);
            }
            return src_Bitmap;

        } else {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inDither = false;
            o.inSampleSize = 4;

            int width, height;


            width = src_Bitmap.getWidth();
            height = src_Bitmap.getHeight();

            Mat rgba = new Mat();
            Mat gray_mat = new Mat();
            Mat threeChannel = new Mat();

            Utils.bitmapToMat(src_Bitmap, gray_mat); // 이미지를 Mat 형식으로 변경

            Imgproc.cvtColor(gray_mat, rgba, Imgproc.COLOR_RGBA2RGB); // 일단 rgb형식으로 바꾼다.


            Imgproc.cvtColor(rgba, threeChannel, Imgproc.COLOR_RGB2GRAY); // rgb -> gray로 바꾼다.
            Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_OTSU); // 기존값 thres 100 :


            Mat fg = new Mat(rgba.size(), CvType.CV_8U);
            Imgproc.dilate(threeChannel,fg,new Mat(),new Point(-1,-1),2);
            Imgproc.erode(threeChannel, fg, new Mat(), new Point(-1, -1), 2); // 기존값 2 : 이는 배경나머지 부분을 최대한 없애준다


            Mat bg = new Mat(rgba.size(), CvType.CV_8U);
            Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 3); // 기존값 3 : 이 값이 작으면 가져올 이미지 내부에 모양을 유지시켜준다
            Imgproc.erode(threeChannel, bg, new Mat(), new Point(-1, -1), 2); // 추가! 훨씬 깨끗하게 보입니다
            Imgproc.threshold(bg, bg, 245, 255, Imgproc.THRESH_BINARY_INV); // 기존값 : 1 178


            Mat markers = new Mat(rgba.size(), CvType.CV_8U, new Scalar(0));
            Core.add(fg, bg, markers);

            // create the new image
            // 추가 삽입 내용
            // 위에서 threshold로 잡아놓은 많은 부분이 잘려져 나올 수 있다. 그러나 이미지가 그려져 있는 경우는 식별이 어렵다. contour로 해야하나?
            Mat foreground = new Mat(rgba.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
            rgba.copyTo(foreground, bg);

            Bitmap result_Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(foreground, result_Bitmap); // markers 부분에 원래 foreground 였다
            Bitmap b;
            if(set2.equals("신발") && !filterStatus.equals("ON")){
                b = applyshoes(result_Bitmap, filterStatus);
            }else{
                b = changeColor_colored(result_Bitmap);
            }

            return b;

        }
    }


    public static Bitmap ImageProcess_white(Bitmap src_Bitmap, String checkcolor) { // -> 이 다음에 무조건 removeshadow를 한다.
        check = checkcolor;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        // Matrix를 생성
        Mat rgba = new Mat();
        Mat gray_mat = new Mat();
        Mat threeChannel = new Mat();

        // bitmap -> mat
        Utils.bitmapToMat(src_Bitmap, gray_mat); // 이미지를 Mat 형식으로 변경
        // matrix 컬러 변경 rgba -> rgb
        Imgproc.cvtColor(gray_mat, rgba, Imgproc.COLOR_RGBA2RGB); // 일단 rgb형식으로 바꾼다.

        // matrix 컬러 변경 rgb -> gray
        Imgproc.cvtColor(rgba, threeChannel, Imgproc.COLOR_RGB2GRAY); // rgb -> gray로 바꾼다.

        // 이는 threshold를 줘서 threshold보다 큰 것은 검은색으로 작은 것은 흰색으로 한다
        // 값을 적게하면 왠지는 모르겠지만 이미지가 조금 내려간다. 색상은 딱히 차이가 없는 듯 하다.
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_OTSU); // 기존값 thres 100 :



        // 같은 사이즈로 새로운 mat를 만든다.
        Mat fg = new Mat(rgba.size(), CvType.CV_8U); // CV_8U
        // erode 연산 : 필터 내부의 가장 낮은(어두운) 값으로 변환(and) - 침식연산 -> 즉 좀 더 없애는 부분을 제대로 없앤다 ;; 대비가 훨씬 강하게함
        Imgproc.erode(threeChannel, fg, new Mat(), new Point(-1, -1), 2); // 기존값 2 : 이는 배경나머지 부분을 최대한 없애준다
        Mat bg = new Mat(rgba.size(), CvType.CV_8U);
        // Dilate 연산 : 필터 내부의 가장 높은(밝은) 값으로 변환(or) - 팽창연산
        Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 0); // 기존값 3 : 이 값이 작으면 가져올 이미지 내부에 모양을 유지시켜준다
        Imgproc.erode(threeChannel, bg, new Mat(), new Point(-1, -1), 2); // 추가! 훨씬 깨끗하게 보입니다
        //-> 완전히 반대로 만들어준다. 검은색은 흰색, 흰색은 검은색으로
        Imgproc.threshold(bg, bg, 245, 255, Imgproc.THRESH_BINARY_INV); // 기존값 : 1 178

        // 여기부터는 테두리만 자르기!

        Bitmap b = getcontourimage(rgba, bg, src_Bitmap);
        return b;

    }


    //jaesub
    public static Bitmap removeShadow(Bitmap icon, Bitmap icon3, String set2, String filterStatus){

        int width, height;
        width = icon.getWidth();
        height = icon.getHeight();
        Log.d("saea", width+"and"+height);

        // 추가!!
        Mat rgba0 = new Mat();
        Mat gray_mat = new Mat();
        Mat threeChannel = new Mat();

        // bitmap -> mat
        Utils.bitmapToMat(icon3, gray_mat); //here !! 여기만 icon3로 변경함
        // matrix 컬러 변경 rgba -> rgb
        Imgproc.cvtColor(gray_mat, rgba0, Imgproc.COLOR_RGBA2RGB); // 일단 rgb형식으로 바꾼다.

        Mat rgba = new Mat(width, height, CV_8UC1);
        Mat rgba2 = new Mat(width, height, CV_8UC1);
        Utils.bitmapToMat(icon, rgba);
        ShadowDetection(rgba.getNativeObjAddr(), rgba2.getNativeObjAddr());

        // add
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        // 일단 기존의 mat을 BGR -> gray로 바꿔준다.
        Mat thr = new Mat(width, height, CvType.CV_8U); // CV_8UC1
        Mat dst = new Mat(width, height, CvType.CV_8U, Scalar.all(0));
        // Imgproc.cvtColor(rgba2, thr, Imgproc.COLOR_BGR2GRAY);

        // binary로 threshold 해주니 흰색 빼고 나머지는 검은색이 됨 INV로 바꿔주니 검은색 흰색 반대 나옴
        Imgproc.threshold(rgba2, rgba2, 245, 255, Imgproc.THRESH_BINARY_INV); // 100 255 THRESH_BINARY

        Imgproc.findContours( rgba2, contours, new Mat(),Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0) );
        //    return  thr;

        // 최대 사이즈의 contourarea를 찾는다.
        double maxVal = 0;
        int maxValIdx = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal < contourArea)
            {
                maxVal = contourArea;
                maxValIdx = contourIdx;
            }
        }

        MatOfPoint contour_selected = contours.get(maxValIdx);
        //Imgproc.drawContours(dst, contours, maxValIdx, new Scalar(255,255,255), 5);
        // 새로운 mat에다가 다시 최대테두리를 그린다.
        Imgproc.drawContours(dst, Collections.singletonList(contour_selected), -1, new Scalar(255), Core.FILLED);


        // 추가!!
        Utils.matToBitmap(dst,icon);

        // 추가!!
        Mat rgba3 = new Mat();
        Mat gray_mat2 = new Mat();
        Utils.bitmapToMat(icon, gray_mat2); // 이미지를 Mat 형식으로 변경
        Imgproc.cvtColor(gray_mat2, rgba3, Imgproc.COLOR_RGBA2RGB); // 일단 rgb형식으로 바꾼다.
        rgba0.copyTo(dst, rgba3); // markers 부분에 원래 foreground 였다

        // 배경색 바꾸기
        Mat mask = new Mat(rgba0.size(), CvType.CV_8UC3);
        Core.inRange(dst, new Scalar(0), new Scalar(0), mask);
        Mat white_back = new Mat(dst.size(), CvType.CV_8UC3, new Scalar(94, 204, 255));  // all zeros image
        white_back.copyTo(dst, mask);
        Bitmap result_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // ARGB_8888
        Utils.matToBitmap(dst, result_bitmap);
        Bitmap b;
        if(set2.equals("신발")){
           b = applyshoes(result_bitmap, filterStatus);
        }else{
           b = changeColor(result_bitmap);
        }
        return b;


    }

    static Bitmap changeColor(Bitmap result_bitmap){
        int sW = result_bitmap.getWidth();
        int sH = result_bitmap.getHeight();

        int[] pixels = new int[sW*sH];
        result_bitmap.getPixels(pixels, 0, sW, 0, 0, sW, sH);
        for (int i =0;i < pixels.length; i++) {
            if (pixels[i] == Color.rgb(94, 204, 255))
                pixels[i] = Color.TRANSPARENT;
        }

        Bitmap b  = Bitmap.createBitmap(pixels, 0, sW, sW, sH,
                Bitmap.Config.ARGB_8888);
        return b;
    }

    static Bitmap changeColor_colored(Bitmap result_bitmap){
        int sW = result_bitmap.getWidth();
        int sH = result_bitmap.getHeight();

        int[] pixels = new int[sW*sH];
        result_bitmap.getPixels(pixels, 0, sW, 0, 0, sW, sH);
        for (int i =0;i < pixels.length; i++) {
            if (pixels[i] == Color.rgb(255,255,255))
                pixels[i] = Color.TRANSPARENT;
        }

        Bitmap b  = Bitmap.createBitmap(pixels, 0, sW, sW, sH,
                Bitmap.Config.ARGB_8888);
        return b;
    }

    private static Bitmap getcontourimage(Mat rgba, Mat bg, Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Mat foreground = new Mat(rgba.size(), CvType.CV_8UC3, new Scalar(255,255,255)); // 기존에는 8UC_3 3채널 unsigned char
        // bg를 카피한다. foreground는 배경이 흰색이며 색상이 들어간 mat이다 .
        rgba.copyTo(foreground, bg); //

        // 흰옷의 경우에 bg 이미지를 가져오고 이를 바탕으로 contour를 찾는다. 가장 큰 contour를 바탕으로 mat을 가져온다. (markers2)
        Mat markers2 =  getcontour(foreground, origin);


        Mat rgba2 = new Mat();
        Mat gray_mat2 = new Mat();
        Utils.bitmapToMat(origin, gray_mat2); // 이미지를 Mat 형식으로 변경
        Imgproc.cvtColor(gray_mat2, rgba2, Imgproc.COLOR_RGBA2RGB); // 일단 rgb형식으로 바꾼다.

        rgba.copyTo(markers2, rgba2); // markers 부분에 원래 foreground 였다
        Bitmap result_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // ARGB_8888
        Utils.matToBitmap(markers2, result_bitmap);
        // 새로 다시 이미지를 생성한다.
        return result_bitmap;

    }

    public static Mat getcontour(Mat m, Bitmap bm){

        // mat point list이다.
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        int iCannyLowerThreshold = 60, iCannyUpperThreshold = 100;
        // mat크기에 맞게 mat을 만든다.
        Mat thr = new Mat(m.rows(),m.cols(),CvType.CV_8U); // CV_8UC1
        Mat dst = new Mat(m.rows(), m.cols(), CvType.CV_8U, Scalar.all(0));

        // 일단 기존의 mat을 BGR -> gray로 바꿔준다.
        Imgproc.cvtColor(m, thr, Imgproc.COLOR_BGR2GRAY);

        // binary로 threshold 해주니 흰색 빼고 나머지는 검은색이 됨 INV로 바꿔주니 검은색 흰색 반대 나옴
        Imgproc.threshold(thr, thr, 245, 255, Imgproc.THRESH_BINARY); // 100 255 THRESH_BINARY

        // canny해주니 모양이 있는 라인만 하얀색이 되고 나머지는 검은색
        Imgproc.Canny(thr, thr, iCannyLowerThreshold, iCannyUpperThreshold);
        // canny 테두리가 검은색 흰색이 반전된다.
        //     Imgproc.threshold(thr, thr, 0, 255, Imgproc.THRESH_BINARY_INV); // sobel 영상과 비교하려고 반전
        // 테두리를 찾아서 mat point list 로 저장한다.

        Imgproc.findContours( thr, contours, new Mat(),Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0) );
        //    return  thr;

        // 최대 사이즈의 contourarea를 찾는다.
        double maxVal = 0;
        int maxValIdx = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal < contourArea)
            {
                maxVal = contourArea;
                maxValIdx = contourIdx;
            }
        }

        MatOfPoint contour_selected = contours.get(maxValIdx);
        //Imgproc.drawContours(dst, contours, maxValIdx, new Scalar(255,255,255), 5);
        // 새로운 mat에다가 다시 최대테두리를 그린다.
        Imgproc.drawContours(dst, Collections.singletonList(contour_selected), -1, new Scalar(255), Core.FILLED);
        Utils.matToBitmap(dst,bm);
        return dst;

    }

    // 이미지 저장하기
    public static void SaveImage(final Context context, final Bitmap result_bitmap, final String uid, final String set1, final String set2, final String set3, final String imgname) {

        Thread t = new Thread(new Runnable() {
            String filePlace;
            @Override
            public void run() {
                OutputStream fOut = null;
                Uri outputFileUri;
                File root = null;
                try {
                    // internal storage 저장하기
                    ContextWrapper cw = new ContextWrapper(context);
                    root = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);
                   // File root = new File(Environment.getExternalStoragePublicDirectory(
                           // Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker");

                    root.mkdirs();
                 //   File sdImageMainDirectory = new File(root, imgname);
                 //   sdImageMainDirectory.createNewFile();
                    //    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                 //   fOut = new FileOutputStream(sdImageMainDirectory);
                    Log.d("saea", "Success.");
                } catch (Exception e) {
                    Log.d("saea", "Error occured. Please try again later.");
                }
                // 파일 위치가져와서 DB에 저장하기
           //     filePlace = Environment.getExternalStoragePublicDirectory(
                  //      Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker" + File.separator + imgname;

                filePlace =  new File(root,imgname).toString();
                File fileName = new File(root,imgname);
                Log.d("saea", filePlace);
                ClothData cData = new ClothData(uid, set1, set2, imgname, set3, null);
                Log.d("saea", "insert"+uid+set1+set2+filePlace+set3);
                DBManager dbMgr = new DBManager(context);
                dbMgr.dbOpen();
                dbMgr.insertClothData(ClothDBSqlData.SQL_DB_INSERT_DATA, cData);
                dbMgr.dbClose();

                // 파일을 내보내기
                try {
                    fOut = new FileOutputStream(fileName);
                    Log.d("saea", "this" + result_bitmap.toString());
                    result_bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("saea", "error");
                    Log.d("saea", "Error occured. Please try again later.");
                }
                ImageUpload upload = new ImageUpload();
                upload.ImageUpload(uid, imgname, filePlace);
            }
        });
        t.start();
    }


    static Bitmap applyshoes(Bitmap bmp, String filterStatus){
        Log.d("saea", check);
        // 신발 한쪽 복사해서 붙이기
        float[] mirrorY = {
                -1, 0, 0,
                0, 1, 0,
                0, 0, 1
        };

        Matrix matrix = new Matrix();
        matrix.setValues(mirrorY);
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), matrix, true);
        Bitmap result = combineImage(bmp,newBmp, false);
        Bitmap b = null;
        if (check.equals("필터없음")){
            b = result;
        }else if(check.equals("보통") && !filterStatus.equals("ON")) {
            b = changeColor_colored(result);
        }else {
            b = changeColor(result);
        }

        return b;
    }

    private static Bitmap combineImage(Bitmap first, Bitmap second, boolean isVerticalMode){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true; Bitmap bitmap = null;
        if(isVerticalMode)
            bitmap = Bitmap.createScaledBitmap(first, first.getWidth(), first.getHeight()+second.getHeight(), true);
        else
            bitmap = Bitmap.createScaledBitmap(first, first.getWidth()+second.getWidth(), first.getHeight(), true);
        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);
        c.drawBitmap(first, 0, 0, p);
        if(isVerticalMode)
            c.drawBitmap(second, 0, first.getHeight(), p);
        else
            c.drawBitmap(second, first.getWidth(), 0, p); first.recycle(); second.recycle(); return bitmap;
    }

}
