package com.sagra.stylemaker_v1.server;

import android.content.Intent;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ImageUpload {
    String upLoadServerUri = null;
    String uploadFilePath;//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보

    int serverResponseCode = 0;

    public void ImageUpload(final String uid, final String sourceFileUri, final String path) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                upLoadServerUri = "https://ssagranatus.cafe24.com/upload_image.php";//서버컴퓨터의 ip주소
                uploadFilePath = path;
                String fileName = sourceFileUri;
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;

                int maxBufferSize = 1 * 1024 * 1024;

                File sourceFile = new File(uploadFilePath);
                if (!sourceFile.isFile()) {
                    Log.e("uploadFile", "Source File not exist :" + uploadFilePath);
                    //     return 0;
                } else {
                    try {
                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUri+"?uid="+uid);
                        // Open a HTTP  connection to  the URL

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                      /*  try{
                            fileName = URLEncoder.encode(fileName, "utf-8");
                        }catch (Exception e){
                            Log.d("saea", "error!!");
                        }   */
                        //URLEncoder.encode(fileName, "utf-8");
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary); //+";charset=utf-8"
                        conn.setRequestProperty("uploaded_file", fileName);

                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                       // dos.writeUTF(fileName);
                        // create a buffer of  maximum size

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }
                        // send multipart form data necesssary after file data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        // Responses from the server (code and message)

                        serverResponseCode = conn.getResponseCode();

                        String serverResponseMessage = conn.getResponseMessage();


                        Log.i("uploadFile", "HTTP Response is : "

                                + serverResponseMessage + ": " + serverResponseCode);


                        if (serverResponseCode == 200) {
                            Log.d("saea", "200");

                        }

                        //close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                        Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

                    } catch (Exception e) {

                        e.printStackTrace();
                        Log.e("Upload file to server Exception", "Exception : "

                                + e.getMessage(), e);
                    }

                    //    return serverResponseCode;


                } // End else block

            }
        });
        t.start();


    }
}


