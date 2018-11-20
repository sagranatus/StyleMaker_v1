package com.sagra.stylemaker_v1;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.sagra.stylemaker_v1.data.ClothData;
import com.sagra.stylemaker_v1.data.CodiData;
import com.sagra.stylemaker_v1.data.StyleData;
import com.sagra.stylemaker_v1.dialog.AddtagDialog;
import com.sagra.stylemaker_v1.dialog.ListSelectorDialog;
import com.sagra.stylemaker_v1.etc.Fonttype;
import com.sagra.stylemaker_v1.etc.SessionManager;
import com.sagra.stylemaker_v1.filtering.FilterActivity;
import com.sagra.stylemaker_v1.server.AppConfig;
import com.sagra.stylemaker_v1.server.AppController;
import com.sagra.stylemaker_v1.server.DownloadImageTask;
import com.sagra.stylemaker_v1.server.ImageUpload;
import com.sagra.stylemaker_v1.server.Server_StyleData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddstyleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    String uid;
    ImageView iv_view;
    String mCurrentPhotoPath;

    Uri imageUri;
    Uri photoURI, albumURI;
    String path;
    CodiData info;
    String from, date;
    TextView codititle;
    ListSelectorDialog dlg;
    String[] listk, listv;

    Integer contents;
    String titlefont;
    String clothtag;

    TextView tag1,tag2,tag3,tag4,tag5,tag6;
    String frommain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // custom dialog setting
        dlg  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정하기
        listk = new String[] {"a", "b", "c", "d"};
        listv = new String[] {"코디스타일태그","테마선택","제목글씨체선택","옷태그여부"};

    }



    @Override
    public void onResume() {

        super.onResume();
        SharedPreferences setPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
        contents = setPreference.getInt("contents", R.layout.activity_addstyle);
        titlefont = setPreference.getString("titlefont", "Papyrus");
        clothtag = setPreference.getString("clothtag", "yes");
        setContentView(contents);

        // actionbar setting
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        TextView mytext = (TextView) findViewById(R.id.mytext);
        TextView logo = (TextView) findViewById(R.id.logo);
        codititle = (TextView) findViewById(R.id.codititle);
        Fonttype.setFont("Billabong",AddstyleActivity.this, mytext);
        Fonttype.setFont("Billabong",AddstyleActivity.this, logo);
        Fonttype.setFont(titlefont,AddstyleActivity.this, codititle);
        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        Intent intent = getIntent();
        info = (CodiData) intent.getSerializableExtra("info");
        date = intent.getStringExtra("date");
        frommain = intent.getStringExtra("frommain");

        ArrayList<CodiData> cDataList = new ArrayList<CodiData>();
        // 여기 추가
        if(date != null){
          //  selectDailybyInfo(AddstyleActivity.this, uid, date);
            afterSelect(info);
        }else{
         //   selectCodibyInfo(AddstyleActivity.this, uid, info.getNumber());
            afterSelect(info);
        }


        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }


    private void getAllimage() {
        ImageView icon1 = (ImageView) findViewById(R.id.appicon);
        ImageView icon2 = (ImageView) findViewById(R.id.appicon2);
        ImageView icon3 = (ImageView) findViewById(R.id.appicon3);
        ImageView icon4 = (ImageView) findViewById(R.id.appicon4);
        ImageView icon5 = (ImageView) findViewById(R.id.appicon5);
        ImageView icon6 = (ImageView) findViewById(R.id.appicon6);

        if(!info.getTop().isEmpty()){
            new DownloadImageTask(icon1).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getTop());
        }

        if(!info.getBottom().isEmpty()){
            new DownloadImageTask(icon2).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getBottom());
        }
        if(!info.getShoes().isEmpty()){
            new DownloadImageTask(icon3).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getShoes());
        }
        if(!info.getOuter().isEmpty()){
            new DownloadImageTask(icon4).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getOuter());
        }
        if(!info.getBag().isEmpty()){
            new DownloadImageTask(icon5).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getBag());
        }
        if(!info.getAccessories().isEmpty()){
            new DownloadImageTask(icon6).execute("https://ssagranatus.cafe24.com/files/user" + info.getUid() + "/" + info.getAccessories());
        }

        icon1.setOnClickListener(this);
        icon2.setOnClickListener(this);
        icon3.setOnClickListener(this);
        icon4.setOnClickListener(this);
        icon5.setOnClickListener(this);
        icon6.setOnClickListener(this);
    }

    private void getAlltag() {
        tag1 = (TextView) findViewById(R.id.tag1);
        if(!info.getTop().isEmpty()){
            getTag(info.getTop(), tag1);
        }

        tag2 = (TextView) findViewById(R.id.tag2);
        if(!info.getBottom().isEmpty()){
            getTag(info.getBottom(), tag2);
        }

        tag3 = (TextView) findViewById(R.id.tag3);
        if(!info.getShoes().isEmpty()) {
            getTag(info.getShoes(), tag3);
        }
        tag4 = (TextView) findViewById(R.id.tag4);
        if(!info.getOuter().isEmpty()){
            getTag(info.getOuter(),tag4);
        }
        tag5 = (TextView) findViewById(R.id.tag5);
        if(!info.getBag().isEmpty()){
            getTag(info.getBag(), tag5);
        }
        tag6 = (TextView) findViewById(R.id.tag6);
        if(!info.getAccessories().isEmpty()){
            getTag(info.getAccessories(), tag6);
        }

    }

    public void getTag(String imagepath, TextView tag){
        selectClothbyInfo(AddstyleActivity.this, uid, imagepath, tag);
    }

    public void afterAddTag(CodiData info){
        codititle.setText(info.getTag());
    }

    public void afterChangeTextOrTag(){
        SharedPreferences setPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
        titlefont = setPreference.getString("titlefont", "Papyrus");
        Fonttype.setFont(titlefont,AddstyleActivity.this, codititle);

        clothtag = setPreference.getString("clothtag", "yes");
        if(clothtag.equals("no")){
            tag1.setVisibility(View.GONE);
            tag2.setVisibility(View.GONE);
            tag3.setVisibility(View.GONE);
            tag4.setVisibility(View.GONE);
            tag5.setVisibility(View.GONE);
            tag6.setVisibility(View.GONE);
        }else{
            tag1.setVisibility(View.VISIBLE);
            tag2.setVisibility(View.VISIBLE);
            tag3.setVisibility(View.VISIBLE);
            tag4.setVisibility(View.VISIBLE);
            tag5.setVisibility(View.VISIBLE);
            tag6.setVisibility(View.VISIBLE);
        }
    }
    public void afterSelect(CodiData data){
        String number = data.getNumber();
        String season = data.getSeason();
        String type = data.getType();
        String top = data.getTop();
        String pants = data.getBottom();
        String shoes = data.getShoes();
        String outer = data.getOuter();
        String bag = data.getBag();
        String accessories = data.getAccessories();
        String tag = data.getTag();

        info = new CodiData(uid, number, season, type, top, pants, shoes, outer, bag, accessories, tag);


        codititle.setText(info.getTag());

        getAllimage();
        getAlltag();

        iv_view = (ImageView) findViewById(R.id.iv_view);
        Intent i = getIntent();
        path =  i.getStringExtra("imagepath");
        if(path != null){
            //   Bitmap myBitmap = BitmapFactory.decodeFile(path);
            //  iv_view.setImageBitmap(myBitmap);
            Glide.with(this).load(path).into(iv_view);
            //   btn_gofilter.setVisibility(View.GONE);
            Log.d("saea", "path exist");
        }
        TextView tag1 = (TextView) findViewById(R.id.tag1);
        TextView tag2 = (TextView) findViewById(R.id.tag2);
        TextView tag3 = (TextView) findViewById(R.id.tag3);
        TextView tag4 = (TextView) findViewById(R.id.tag4);
        TextView tag5 = (TextView) findViewById(R.id.tag5);
        TextView tag6 = (TextView) findViewById(R.id.tag6);
        if(clothtag.equals("no")){
            tag1.setVisibility(View.GONE);
            tag2.setVisibility(View.GONE);
            tag3.setVisibility(View.GONE);
            tag4.setVisibility(View.GONE);
            tag5.setVisibility(View.GONE);
            tag6.setVisibility(View.GONE);
        }else{
            tag1.setVisibility(View.VISIBLE);
            tag2.setVisibility(View.VISIBLE);
            tag3.setVisibility(View.VISIBLE);
            tag4.setVisibility(View.VISIBLE);
            tag5.setVisibility(View.VISIBLE);
            tag6.setVisibility(View.VISIBLE);
        }

    }

    //actionbar 위에 버튼 넣고 select 이벤트 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_addstyle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:
                captureCamera();
                return true;
            case R.id.action_two:
                getAlbum();
                return true;
            case R.id.action_three:
                // show the list dialog.
                dlg.show(listv, listk, new ListSelectorDialog.listSelectorInterface() {

                    // procedure if user cancels the dialog.
                    public void selectorCanceled() {
                    }
                    // procedure for when a user selects an item in the dialog.
                    public void selectedItem(String key, String item) {
                        if(item.equals("코디스타일태그")){
                            AddtagDialog dialog = new AddtagDialog(AddstyleActivity.this, uid, info, "style", date);
                            dialog.show();
                        }else if(item.equals("테마선택")){
                            ListSelectorDialog dlg_inside = null;
                            String[] listk_inside, listv_inside;
                            dlg_inside = new ListSelectorDialog(AddstyleActivity.this, "inside");
                            listk_inside = new String[] {"a", "b"};
                            listv_inside = new String[] {"A", "B"};
                            dlg_inside.show(listv_inside, listk_inside, new ListSelectorDialog.listSelectorInterface() {

                                // procedure if user cancels the dialog.
                                public void selectorCanceled() {
                                }

                                // procedure for when a user selects an item in the dialog.
                                public void selectedItem(String key, String item) {
                                    if (item.equals("A")) {
                                     //   Toast.makeText(AddstyleActivity.this, "A", Toast.LENGTH_SHORT).show();
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putInt("contents", R.layout.activity_addstyle);
                                        wEditPreference.commit();
                                        AddstyleActivity.this.onResume();
                                    } else if (item.equals("B")) {
                                    //    Toast.makeText(AddstyleActivity.this, "B", Toast.LENGTH_SHORT).show();
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putInt("contents",R.layout.activity_addstyle2);
                                        wEditPreference.commit();
                                        AddstyleActivity.this.onResume();
                                    }
                                }
                            });
                        }else if(item.equals("제목글씨체선택")){
                            ListSelectorDialog dlg_inside = null;
                            String[] listk_inside, listv_inside;
                            dlg_inside = new ListSelectorDialog(AddstyleActivity.this, "inside");
                            listk_inside = new String[] {"a", "b", "c"};
                            listv_inside = new String[] {"tvN", "Papyrus", "Ppikke"};
                            dlg_inside.show(listv_inside, listk_inside, new ListSelectorDialog.listSelectorInterface() {

                                // procedure if user cancels the dialog.
                                public void selectorCanceled() {
                                }

                                // procedure for when a user selects an item in the dialog.
                                public void selectedItem(String key, String item) {
                                    if (item.equals("tvN")) {
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putString("titlefont","tvN");
                                        wEditPreference.commit();
                                        afterChangeTextOrTag();
                                    } else if (item.equals("Papyrus")) {
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putString("titlefont","Papyrus");
                                        wEditPreference.commit();
                                        afterChangeTextOrTag();
                                    }else if (item.equals("Ppikke")) {
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putString("titlefont","Ppikke");
                                        wEditPreference.commit();
                                        afterChangeTextOrTag();
                                    }
                                }
                            });
                        }else if(item.equals("옷태그여부")){
                            ListSelectorDialog dlg_inside = null;
                            String[] listk_inside, listv_inside;
                            dlg_inside = new ListSelectorDialog(AddstyleActivity.this, "inside");
                            listk_inside = new String[] {"a", "b"};
                            listv_inside = new String[] {"보이기", "안보이기"};
                            dlg_inside.show(listv_inside, listk_inside, new ListSelectorDialog.listSelectorInterface() {
                                // procedure if user cancels the dialog.
                                public void selectorCanceled() {
                                }

                                // procedure for when a user selects an item in the dialog.
                                public void selectedItem(String key, String item) {
                                    if (item.equals("보이기")) {
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putString("clothtag","yes");
                                        wEditPreference.commit();
                                        afterChangeTextOrTag();
                                    } else if (item.equals("안보이기")) {
                                        SharedPreferences wPreference = getSharedPreferences("Stylebook", MODE_PRIVATE);
                                        SharedPreferences.Editor wEditPreference = wPreference.edit();
                                        wEditPreference.putString("clothtag","no");
                                        wEditPreference.commit();
                                        afterChangeTextOrTag();
                                    }
                                }
                            });
                        }
                    }
                });
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                if(frommain != null){
                    Intent i = new Intent(AddstyleActivity.this, MainActivity.class);
                    i.putExtra("info", info);
                    i.putExtra("date", date);
                    startActivity(i);
                }else{
                    Intent i = new Intent(AddstyleActivity.this, CodidetailActivity.class);
                    i.putExtra("info", info);
                    i.putExtra("date", date);
                    startActivity(i);
                }

               return true;
        }
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

            return imageFile;
        }


        private void getAlbum(){
            Log.i("getAlbum", "Call");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, REQUEST_TAKE_ALBUM);
        }

        private void galleryAddPic(){
            Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(AddstyleActivity.this, FilterActivity.class);
        i.putExtra("info", info);
        i.putExtra("date", date);
        i.putExtra("frommain", frommain);
        i.putExtra("imagepath", mCurrentPhotoPath);
        startActivity(i);
    }

    // 카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", (int) getResources().getDimension(R.dimen.xsize)); // crop한 이미지의 x축 크기, 결과물의 크기
        cropIntent.putExtra("outputY", (int) getResources().getDimension(R.dimen.ysize)); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 2); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    // 카메라 전용 크랍(앨범엔 크롭된 이미지만 저장시키기 위해)
    public void cropSingleImage(Uri photoUriPath){
        Log.i("cropSingleImage", "Call");
        Log.i("cropSingleImage", "photoUriPath : " + photoUriPath);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법, addFlags로도 에러 나서 setFlags
        // 누가 버전 처리방법임
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cropIntent.setDataAndType(photoUriPath, "image/*");
        cropIntent.putExtra("outputX", (int) getResources().getDimension(R.dimen.xsize)); // crop한 이미지의 x축 크기, 결과물의 크기
        cropIntent.putExtra("outputY", (int) getResources().getDimension(R.dimen.ysize)); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 2); // crop 박스의 y축 비율

        Log.i("cropSingleImage", "photoUriPath22 : " + photoUriPath);

        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUriPath); // 크랍된 이미지를 해당 경로에 저장

        // 같은 photoUriPath에 저장하려면 아래가 있어야함(왜지)
        List list = getPackageManager().queryIntentActivities(cropIntent, 0);
      //  grantUriPermission(list.get(0).activityInfo.packageName, photoUriPath,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = (ResolveInfo) list.get(0);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    //    grantUriPermission(res.activityInfo.packageName, photoUriPath,
                //Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    albumURI = null;
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                    // for emulator
                     //   galleryAddPic(); //추가
                    //    iv_view.setImageURI(imageUri); //추가

                        cropSingleImage(imageUri); //이게 있어야하지만 테스트를 위해서!! //추가 를 삽입했다.
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(AddstyleActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
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
                            cropImage();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();
                    if(albumURI != null){
                    }else{
                    }
                    path = null;
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
                        Toast.makeText(AddstyleActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(AddstyleActivity.this, ClothdetailActivity.class);
        switch(v.getId())
        {
            case R.id.appicon:
                i.putExtra("info_fromcodi", info.getTop());
                startActivity(i);
                break;
            case R.id.appicon2:
                i.putExtra("info_fromcodi", info.getBottom());
                startActivity(i);
                break;
            case R.id.appicon3:
                i.putExtra("info_fromcodi", info.getShoes());
                startActivity(i);
                break;
            case R.id.appicon4:
                i.putExtra("info_fromcodi", info.getOuter());
                startActivity(i);
                break;
            case R.id.appicon5:
                i.putExtra("info_fromcodi", info.getBag());
                startActivity(i);
                break;
            case R.id.appicon6:
                i.putExtra("info_fromcodi", info.getAccessories());
                startActivity(i);
                break;
            case R.id.save:
                // 저장시각
                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String numberStr = dayTime.format(new Date(time)); // number에는 코디저장한 시간으로 저장한다!

                String titletag = (String) codititle.getText();
                String tag_1 = tag1.getText().toString();
                String tag_2 = tag2.getText().toString();
                String tag_3 = tag3.getText().toString();
                String tag_4 = tag4.getText().toString();
                String tag_5 = tag5.getText().toString();
                String tag_6 = tag6.getText().toString();
                String clothtags = "";
                if(!tag_1.equals("")){
                    clothtags += tag_1+" ";
                }
                if(!tag_2.equals("")){
                    clothtags += tag_2+" ";
                }
                if(!tag_3.equals("")){
                    clothtags += tag_3+" ";
                }
                if(!tag_4.equals("")){
                    clothtags += tag_4+" ";
                }
                if(!tag_5.equals("")){
                    clothtags += tag_5+" ";
                }
                if(!tag_6.equals("")){
                    clothtags += tag_6+" ";
                }

                Log.d("saea",clothtags+titletag);

                LinearLayout view =  (LinearLayout)findViewById(R.id.all);
                ImageView bmImage = (ImageView)findViewById(R.id.image);

                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap saveBm = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);

                FileOutputStream fOut = null;
                try {
                    File root = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker");
                    root.mkdirs();
                      File sdImageMainDirectory = new File(root, numberStr+".png");
                      sdImageMainDirectory.createNewFile();
                      //    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                      fOut = new FileOutputStream(sdImageMainDirectory);

                } catch (Exception e) {
                    Toast.makeText(this, "Error occured. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
                // 파일 위치가져와서 DB에 저장하기
                String filePlace = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES) + File.separator + "Stylemaker" + File.separator + numberStr+".png";

                // 파일을 내보내기
                try {
                    Log.d("saea",  "this"+filePlace);

                    saveBm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("saea", "error");
                    Toast.makeText(this, "Error occured. Please try again later.",
                            Toast.LENGTH_SHORT).show();
              }
                clothtags = clothtags.trim().replaceAll("\\s{2,}", " "); // 공백 여러개인거 제거하기

              //  Toast.makeText(this, clothtags, Toast.LENGTH_SHORT).show();
               StyleData sData = new StyleData(uid, numberStr, info.getNumber(), info.getSeason(), titletag, clothtags);

                ImageUpload upload = new ImageUpload();
                upload.ImageUpload(uid, numberStr+".png", filePlace);
                Server_StyleData.insertStyle(AddstyleActivity.this, uid, sData);
                Toast.makeText(this, "코디가 저장되었습니다", Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(AddstyleActivity.this, FourthActivity.class);
                startActivity(i2);

                break;

        }
    }
/*
    public void selectCodibyInfo(final Context context, final String uid, final String number) {

        Log.i("saea", "Starting Upload...");

        selectCodibyInfo_Connect(context, uid, number);
    }




    public void selectCodibyInfo_Connect(final Context context, final String uid, final String number) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CODIDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("codi");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("numberval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));
                        info = codiData;
                        afterSelect(codiData);

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "insert22: " + uid+number);
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("number", number);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

*/

    public void selectClothbyInfo(final Context context, final String uid, final String info_fromcodi, TextView tag) {


        Log.i("saea", "Starting Upload...");

        selectClothbyInfo_Connect(context, uid, info_fromcodi, tag);
    }




    public void selectClothbyInfo_Connect(final Context context, final String uid, final String info_fromcodi, final TextView tag) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_CLOTHDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject cloth = jObj.getJSONObject("cloth");

                        ClothData clothData = new ClothData(cloth.getString("uid"), cloth.getString("season"), cloth.getString("clothtype"), cloth.getString("info")
                                ,cloth.getString("detail1"), cloth.getString("detail2"));
                        tag.setText(cloth.getString("detail2"));


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "insert22: " + uid+info_fromcodi);
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("info", info_fromcodi);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

  /*  public void selectDailybyInfo(final Context context, final String uid, final String date) {


        Log.i("saea", "Starting Upload...");

        selectDailybyInfo_Connect(context, uid, date);

    }




    public void selectDailybyInfo_Connect(final Context context, final String uid, final String date) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_DAILYDATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        String uid = jObj.getString("uid");
                        JSONObject codi = jObj.getJSONObject("daily");

                        CodiData codiData = new CodiData(codi.getString("uid"), codi.getString("dateval"), codi.getString("season"),
                                codi.getString("type"), codi.getString("top"), codi.getString("bottom"),
                                codi.getString("shoes"), codi.getString("outdoor"),codi.getString("bag"),codi.getString("acc"), codi.getString("tag"));

                        afterSelect(codiData);

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "insert22: " + uid+date);
                params.put("status", "selectbyInfo");
                params.put("uid", uid);
                params.put("date", date);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
*/



}