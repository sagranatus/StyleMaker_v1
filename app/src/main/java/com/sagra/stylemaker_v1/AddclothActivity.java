package com.sagra.stylemaker_v1;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.sagra.stylemaker_v1.adapter.CameraspinnerAdapter;
import com.sagra.stylemaker_v1.data.Cameraspinner;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.etc.CustomZoomableImageView;
import com.sagra.stylemaker_v1.etc.ImageProcessing;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.server.Server_ClothData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 옷 사진찍기 화면 -> 두번째 tab에서 add할때
public class AddclothActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, AdapterView.OnItemSelectedListener {
    String uid;
    private SurfaceView mSurfaceView = null;
    RelativeLayout overlay;
    private SurfaceHolder mSurfaceHolder = null;
    private android.hardware.Camera mCamera = null;

    Bitmap result;
    CustomZoomableImageView afterpic;

    String checkcolor = "보통";
    String set1, set2, set3;
    Spinner spinner3;
    Spinner spinner2;

    CameraspinnerAdapter adapter = null;
    ArrayList<Cameraspinner> cameraspinners = null;
    Spinner mySpinner;
    Button filter;
    String filterStatus;

    // 이는 opencv 라이브러리를 불러오는 부분
    static {
        System.loadLibrary("opencv_java3"); // 이게 없으면 안 됨
        // System.loadLibrary("native-lib");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // camerarequest를 가져와서 ok인 경우에만 사진촬영을 할 수 있도록 한다.
        SharedPreferences mPreference = getSharedPreferences("Camera", MODE_PRIVATE);
        String name = mPreference.getString("camerarequest", "");
        Log.d("saea", name + "request status");

        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // actionbar setting
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("뒤로가기");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcloth);

        // spinner setting
        Spinner spinner1 = (Spinner) this.findViewById(R.id.set1);
        spinner1.setOnItemSelectedListener(this);

        spinner2 = (Spinner) findViewById(R.id.set2);
        spinner3 = (Spinner) findViewById(R.id.set3);
        spinner3.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        populateSpinners();
       // spinner2.setOnItemSelectedListener(spinSelectedlistener);

        // camera관련 button setting
        afterpic = (CustomZoomableImageView ) findViewById(R.id.afterpic);
        ImageButton btn_camera = (ImageButton) this.findViewById(R.id.camera);
        btn_camera.setOnClickListener(this);
        ImageButton btn_save = (ImageButton) this.findViewById(R.id.save);
        btn_save.setOnClickListener(this);
        ImageButton btn_retaken = (ImageButton) this.findViewById(R.id.retaken);
        btn_retaken.setOnClickListener(this);

        filter = (Button) findViewById(R.id.filter);
        filter.setOnClickListener(this);
        filterStatus = filter.getText().toString();

        // 계절정보 가져와서 spinner에 반영
        SharedPreferences wPreference = getSharedPreferences("Weather", MODE_PRIVATE);
        String season = wPreference.getString("season", "");
        spinner1.setSelection(((ArrayAdapter<String>) spinner1.getAdapter()).getPosition(season));


        // 카메라사용 가능한 경우에 surfaceview를 설정해준다
        if (name.equals("ok")) {
            overlay = (RelativeLayout) findViewById(R.id.overlay);
            mSurfaceView = (SurfaceView) findViewById(R.id.cameraview);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        // 사진찍는 종류 spinner 설정
        cameraspinners = new ArrayList<>();
        cameraspinners = populateCameraData(cameraspinners);
        mySpinner = (Spinner) findViewById(R.id.mySpinner);
        adapter = new CameraspinnerAdapter(this, cameraspinners);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position == 0) {
                    filter.setVisibility(View.VISIBLE);
                    checkcolor = "보통";
                } else if (position == 1) {
                    checkcolor = "반전";
                    filter.setVisibility(View.GONE);
                } else if (position == 2) {
                    checkcolor = "필터없음";
                    filter.setVisibility(View.GONE);
                }
                //    String text = mySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    // cameraspinner 정보 삽입
    private ArrayList<Cameraspinner> populateCameraData(ArrayList<Cameraspinner> spinners) {
        spinners.add(new Cameraspinner("색상옷인 경우 / 배경은 반드시 하얀색", R.drawable.check1));
        spinners.add(new Cameraspinner("흰옷인 경우 / 배경은 반드시 어두운색", R.drawable.check2));
        spinners.add(new Cameraspinner("필터없음", R.drawable.check4));

        return spinners;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    //spinner 관련된 코드
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.set1:
                Spinner spinner1 = (Spinner)findViewById(R.id.set1);
                set1 = spinner1.getSelectedItem().toString();
                break;
            case R.id.set2:
                Spinner spinner2 = (Spinner)findViewById(R.id.set2);
                set2 = spinner2.getSelectedItem().toString();
                break;
            case R.id.set3:
                Spinner spinner3 = (Spinner)findViewById(R.id.set3);
                set3 = spinner3.getSelectedItem().toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // spinner2, 3의 값을 설정
    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;

        SharedPreferences setPreference = getSharedPreferences("Setting", MODE_PRIVATE);
        String gender = setPreference.getString("gender", "");
        if (gender.equals("여자")) {
            fAdapter = ArrayAdapter.createFromResource(this,
                    R.array.set2_array_women,
                    android.R.layout.simple_spinner_item);
        } else {
            fAdapter = ArrayAdapter.createFromResource(this,
                    R.array.set2_array,
                    android.R.layout.simple_spinner_item);
        }


        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(fAdapter);

        populateSubSpinners(R.array.set3_0);
    }

    private void populateSubSpinners(int itemNum) {
        ArrayAdapter<CharSequence> fAdapter;
        fAdapter = ArrayAdapter.createFromResource(this,
                itemNum,
                android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(fAdapter);
        spinner3.setVisibility(View.VISIBLE);
    }

    // spinner2선택시 spinner3의 값을 설정한다.
    private AdapterView.OnItemSelectedListener spinSelectedlistener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    //    String[] set2Array = getResources().getStringArray(R.array.set2_array);
                    SharedPreferences setPreference = getSharedPreferences("Setting", MODE_PRIVATE);
                    String gender = setPreference.getString("gender", "");
                    String[] set2Array;
                    if (gender.equals("여자")) {
                        set2Array = getResources().getStringArray(R.array.set2_array_women);
                    } else {
                        set2Array = getResources().getStringArray(R.array.set2_array);
                    }
                    set2 = set2Array[position];
                    String[] set3Array;
                    switch (position) {
                        case (0):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        case (1):
                            spinner3.setVisibility(View.VISIBLE);
                            populateSubSpinners(R.array.set3_0);
                            set3Array = getResources().getStringArray(R.array.set3_0);
                            set3 = set3Array[position];
                            break;
                        default:
                            spinner3.setVisibility(View.GONE);
                            set3 = null;
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    // camera 관련 버튼 선택시 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if (mCamera != null) {
                    mCamera.takePicture(null, null, this);
                }
                break;

            case R.id.retaken:
                RelativeLayout largeView_ = (RelativeLayout) findViewById(R.id.myView);
                largeView_.setDrawingCacheEnabled(false);
                afterpic.setVisibility(View.GONE);
                mSurfaceView.setVisibility(View.VISIBLE);

                break;
            case R.id.save:
                if (afterpic.getDrawable() == null) {
                    Toast.makeText(this, "no image", Toast.LENGTH_SHORT).show();
                } else {
                    RelativeLayout largeView = (RelativeLayout) findViewById(R.id.myView);
                    largeView.setDrawingCacheEnabled(true);
                    largeView.buildDrawingCache();
                    result = largeView.getDrawingCache();
                 //   result =((BitmapDrawable)afterpic.getDrawable()).getBitmap();
                    Log.d("saea", result.toString());

                    //서버에 값 전송
                    long time = System.currentTimeMillis();
                    SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String str = dayTime.format(new Date(time));
                    String imgname;
                    String set1_en = null;
                    String set2_en = null;
                    String set3_en = "-";

                    if(set1.equals("계절무관")){
                        set1_en = "all";
                    }else if(set1.equals("봄,가을")){
                        set1_en = "s,f";
                    }else if(set1.equals("여름")){
                        set1_en = "summer";
                    }else if(set1.equals("겨울")){
                        set1_en = "winter";
                    }

                    if(set2.equals("상의")){
                        set2_en = "top";
                    }else if(set2.equals("하의")){
                        set2_en = "bottom";
                    }else if(set2.equals("원피스")){
                        set2_en = "dress";
                    }else if(set2.equals("가방")){
                        set2_en = "bag";
                    }else if(set2.equals("신발")){
                        set2_en = "shoes";
                    }else if(set2.equals("악세서리")){
                        set2_en = "acc";
                    }else if(set2.equals("아우터")){
                        set2_en = "outer";
                    }

                    if(set3 != null && !set3.equals("-")){
                        if(set3.equals("외출복")){
                            set3_en = "outdoor";
                        }else if(set3.equals("실내복")){
                            set3_en = "indoor";
                        }
                        imgname = set1_en+"-"+set2_en+"-"+set3_en+"-"+str+".png";
                    }else{
                        imgname = set1_en+"-"+set2_en+"-"+str+".png";
                    }
                    Log.d("now", set1_en+set2_en+set3_en);
                    ClothData cData = new ClothData(uid, set1_en, set2_en, imgname, set3_en, "");
                    Server_ClothData.insertCloth(this, uid, cData);
                    //서버에 데이터 전송
                  //  ServerConnection.insertCloth(this, uid, cData);
                    // 핸드폰 내에 데이터 및 이미지 저장 && 서버에 이미지 업로드
                    ImageProcessing.SaveImage(this, result, uid, set1_en, set2_en, set3_en, imgname);
                    Toast.makeText(this, set1+"/"+set2+" 아이템이 추가 되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.filter:
                if (filterStatus.equals("ON")) {
                    filter.setText("OFF");
                    filter.setBackgroundColor(Color.parseColor("#999999"));
                    filterStatus = "OFF";
                } else {
                    filter.setText("ON");
                    filterStatus = "ON";
                    filter.setBackgroundColor(Color.parseColor("#ffcc00"));
                }
                //   mCamera.startPreview();


                break;
        }
    }

    // 이는 surfaceview 크기를 가져와서 남은 높이는 overlay로 주어 surfaceview를 정사각형으로 만든다.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = mSurfaceView.getMeasuredWidth(),
                previewHeight = mSurfaceView.getMeasuredHeight();

        afterpic.getLayoutParams().height = previewWidth;
        afterpic.getLayoutParams().width = previewWidth;
        afterpic.setScaleType(ImageView.ScaleType.FIT_XY);

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }

    //카메라 기능 가져올때 소스

    // surfaceview 생성
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            releaseCameraAndPreview();
            mCamera = android.hardware.Camera.open();
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
        // 추가 카메라 방향 전환
        mCamera.setDisplayOrientation(90);

        // 카메라를 preview에 보여주기
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    // 여기서 surfaceview 형태를 설정한다. 사이즈 방향 등 그후 프리뷰 시작한다.
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Camera.Parameters camParams = mCamera.getParameters();

        // Find a preview size that is at least the size of our IMAGE_SIZE
        Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);

        // 최대사이즈가 디폴트값이라고 함.
        previewSize = getOptimalPreviewSize(camParams.getSupportedPreviewSizes(), width, height);

        camParams.setPreviewSize(previewSize.width, previewSize.height);

        // 프리뷰사이즈랑 가장 가까운 사진 크기를 설정한다.
        Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
        for (Camera.Size size : camParams.getSupportedPictureSizes()) {
            if (size.width == previewSize.width && size.height == previewSize.height) {
                pictureSize = size;
                break;
            }
        }
        camParams.setPictureSize(pictureSize.width, pictureSize.height);

        mCamera.setParameters(camParams);
        mCamera.startPreview();
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = height;
        int targetWidth = width;

        int minWidthDiff = 0;
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.width - targetWidth) < minDiff) {
                    if (size.width > width) {
                        if (minWidthDiff == 0) {
                            minWidthDiff = size.width - width;
                            optimalSize = size;
                        } else if (Math.abs(size.width - targetWidth) < minWidthDiff) {
                            minWidthDiff = size.width - width;
                            optimalSize = size;

                        }
                        minDiff = Math.abs(size.width - targetWidth);
                    }
                }
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.release();
        mCamera = null;
    }

    // 사진 찍은 뒤에 이벤트
    @Override
    public void onPictureTaken(byte[] aData, android.hardware.Camera aCamera) {
        result = null;
        Bitmap bitmap = null;
        try {
            // 찍은 사진에 맞는 이미지를 가져오기 위해 processing 해준다.
            bitmap = processImage(aData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 신발의 경우는 왼쪽만 찍으면 양쪽다 보일 수 있게
        if (checkcolor.equals("보통")) {
            result = ImageProcessing.ImageProcess(bitmap, checkcolor, set2, filterStatus);
            if (filterStatus.equals("ON")) {
                result = ImageProcessing.removeShadow(bitmap, result, set2, filterStatus);
            }

        } else if (checkcolor.equals("반전")) {
            result = ImageProcessing.ImageProcess_white(bitmap, checkcolor);
            result = ImageProcessing.removeShadow(bitmap, result, set2, filterStatus);
        } else {
            result = ImageProcessing.ImageProcess(bitmap, checkcolor, set2, filterStatus);
        }


        RelativeLayout largeView = (RelativeLayout) findViewById(R.id.myView);
        if(set2.equals("원피스")) {
            final float scale = AddclothActivity.this.getResources().getDisplayMetrics().density;
            int px = (int) (200 * scale + 0.5f);  // replace 100 with your dimensions
            largeView.getLayoutParams().width = px;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) largeView.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        }else{
            final float scale = AddclothActivity.this.getResources().getDisplayMetrics().density;
            int px = (int) (500 * scale + 0.5f);  // replace 100 with your dimensions
            largeView.getLayoutParams().width = px;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) largeView.getLayoutParams();

        }
        afterpic.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.GONE);
        afterpic.setImageBitmap(result);

    }

    // 찍은이미지 바로 processing(길이조절 및 찍은대로 저장할 수 있게 하기)
    private Bitmap processImage(byte[] data) throws IOException {
        // Determine the width/height of the image
        int width = mCamera.getParameters().getPictureSize().width;
        int height = mCamera.getParameters().getPictureSize().height;

        // byte를 bitmap으로 변형
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // 이 부분을 삽입하면 약간 아래가 짤려서 나온다
        // Rotate and crop the image into a square
        // int croppedWidth = (width > height) ? height : width;
        // int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();

        // 높이 아랫부분은 70%까지 잘라준다.
        Matrix matrix2 = new Matrix();
        int fromHere = (int) (cropped.getHeight() * 0.7);
        Bitmap croppedBitmap = Bitmap.createBitmap(cropped, 0, 0, cropped.getWidth(), fromHere, matrix2, true);
        cropped.recycle();

        // 결과 bitmap을 900,900으로 리사이징 해준다.
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 900, 900, true);
        croppedBitmap.recycle();

        return scaledBitmap;
    }


}