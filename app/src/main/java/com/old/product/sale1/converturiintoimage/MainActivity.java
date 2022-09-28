package com.old.product.sale1.converturiintoimage;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
       ImageView image1,btndownload;

    Button btnclear,btnconvert,clicktonext;

    EditText etid1;
    Handler mainhandler= new Handler();
    ProgressDialog progressDialog;
   private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btndownload= findViewById(R.id.btndownload);
        PRDownloader.initialize(getApplicationContext());

        url = getIntent().getStringExtra("image");


        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Checkpermission();
            }
        });


        image1 = findViewById(R.id.image1);
        btnclear = findViewById(R.id.btnclear);
        btnconvert = findViewById(R.id.btnconvert);
        etid1 = findViewById(R.id.etid1);
        clicktonext = findViewById(R.id.clicktonext);
        clicktonext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(getApplicationContext(),NextActivity.class);
                startActivity(intnt);

            }
        });

        btnconvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String url = etid1.getText().toString();
            new fetchImage(url).start();

            etid1.setText("paste url here");
            image1.setImageResource(R.drawable.image12);
            }
        });




        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etid1.setText("");
                image1.setImageResource(R.drawable.image12);

            }
        });
    }

    private void Checkpermission() {
        Dexter.withContext(this)
                .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE


//                        Manifest.permission.CAMERA,
//                        Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    downloadImage();
                }
                else {
                    Toast.makeText(MainActivity.this, "allow the all permission", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {


            }


        }).check();


    }

    private void downloadImage() {
        ProgressDialog pd1 = new ProgressDialog(this);
        pd1.setMessage("Downloading.............");
        pd1.setCancelable(false);
        pd1.show();




        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PRDownloader.download(url, file.getPath(), URLUtil.guessFileName(url,null,null))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long per = progress.currentBytes*100/ progress.totalBytes;

                        pd1.setMessage("downloading : "+per+" %");

                    }


                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(MainActivity.this, "download dompleted", Toast.LENGTH_SHORT).show();
                        pd1.dismiss();

                    }

                    @Override
                    public void onError(Error error) {

                        Toast.makeText(MainActivity.this, "download failed"+ error.isServerError()+  "--"+error.getResponseCode(), Toast.LENGTH_SHORT).show();
                       pd1.dismiss();


                    }


                });



    }




    class fetchImage extends Thread{
        String URL;
        Bitmap bitmap;

            fetchImage(String URL){
                this.URL = URL;

            }
            @Override
            public void run(){
//                super.run();
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("gatting your pic");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    }
                });
                InputStream inputStream = null;
                try {
                    inputStream = new java.net.URL(URL).openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mainhandler.post(new Runnable() {

                    @Override
                    public void run() {
                       if (progressDialog.isShowing())
                           progressDialog.dismiss();
                       image1.setImageBitmap(bitmap);
                    }
                });




            }

    }
}