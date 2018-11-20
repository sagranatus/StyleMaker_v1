package com.sagra.stylemaker_v1;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sagra.stylemaker_v1.etc.CustomZoomableImageView_profile;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.server.ImageUpload;
import com.sagra.stylemaker_v1.server.Server_UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfilepicActivity extends AppCompatActivity implements View.OnClickListener {
    String uid;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    String name, email, password, gender, user_id;
    CustomZoomableImageView_profile profileImg;

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    String mCurrentPhotoPath;
    Uri imageUri;
    Uri photoURI, albumURI;
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        mCurrentPhotoPath = null;
        setContentView(R.layout.activity_profilepic);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        TextView mytext = (TextView) findViewById(R.id.mytext);
        Fonttype.setFont("Billabong",ProfilepicActivity.this, mytext);
        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra("info");
        name = info[0];
        email = info[1];
        password = info[2];
        gender = info[3];
        user_id = info[4];
        uid = intent.getStringExtra("uid");
        if(intent.getStringExtra("imgpath") != null){
            path = intent.getStringExtra("imgpath");
        }
        profileImg =  (CustomZoomableImageView_profile) findViewById(R.id.image);

        Log.d("saea", uid+name+email+password+gender+user_id);
          Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_profilepic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_one:
                captureCamera();
                return true;
            case R.id.action_two:
                getAlbum();
                return true;
            default:
                finish();
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return true;
        }
    }
    @Override
    public void onClick(View v) {
        if( mCurrentPhotoPath != null){
            File file = new File(mCurrentPhotoPath);
            Log.d("saea", "file exists");
            file.delete();
            saveProfileImage();
        }else{
            Log.d("saea", "file non! exists");
        }


    }


    private void saveProfileImage(){
        if(path != null){
            File file = new File(path);
            if(file.exists()){
                file.delete();
                ProfilepicActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
            }
        }


        OutputStream fOut = null;

        Uri outputFileUri;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgname = "profileimg_user"+uid+"_"+timeStamp+".png";

        RelativeLayout largeView = (RelativeLayout) findViewById(R.id.myView);
        largeView.setDrawingCacheEnabled(true);
        largeView.buildDrawingCache();
        Bitmap result = largeView.getDrawingCache();

        File root = null;
        try {
            // internal storage 저장하기
            ContextWrapper cw = new ContextWrapper(ProfilepicActivity.this);
            root = cw.getDir("imageDir_user"+uid, Context.MODE_PRIVATE);

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

        String filePlace =  new File(root,imgname).toString();
        File fileName = new File(root,imgname);
        Log.d("saea", filePlace);

        // 파일을 내보내기
        try {
            fOut = new FileOutputStream(fileName);
            Log.d("saea", "this" + result.toString());
            result.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("saea", "error");
            Log.d("saea", "Error occured. Please try again later.");
        }

        // server-DB 이미지 삽입 및 테이블 수정
        ImageUpload upload = new ImageUpload();
        upload.ImageUpload(uid, imgname, filePlace);
        Log.d("saea", uid+name+email+password+gender+user_id+imgname);
        Server_UserData.updateUser(ProfilepicActivity.this, uid, name, email, password, gender, user_id, imgname);

    }




    private void captureCamera(){

        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;
                    Log.e("saea", providerURI.toString());
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/pictures", "stylemaker");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        Log.d("saea", mCurrentPhotoPath);
        return imageFile;
    }


    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    albumURI = null;

                    Bitmap bm = null;
                    try {
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int width = bm.getWidth();
                    int height = bm.getHeight();
                    int newWidth = 600;
                    int newHeight = newWidth * height / width;
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);

                    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
                    profileImg.setImageBitmap(resizedBitmap);
                } else {
                    Toast.makeText(ProfilepicActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                           // cropImage();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                        Bitmap bm = null;
                        try {
                            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int width = bm.getWidth();
                        int height = bm.getHeight();
                        int newWidth = 600;
                        int newHeight = newWidth * height / width;
                        float scaleWidth = ((float) newWidth) / width;
                        float scaleHeight = ((float) newHeight) / height;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);

                        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
                        profileImg.setImageBitmap(resizedBitmap);
                    }
                }
                break;

        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(ProfilepicActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

}
