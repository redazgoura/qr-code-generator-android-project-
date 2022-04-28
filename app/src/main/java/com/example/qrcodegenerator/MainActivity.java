 package com.example.qrcodegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintDocumentAdapter;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

 public class MainActivity extends AppCompatActivity {
     private EditText qrValue;
     private ImageView qrImage;
     private String inputValue;
     private String savePath = Environment.getExternalStorageDirectory().getPath()+"/QRCode/";
     private Bitmap bitmap;
     private QRGEncoder qrgEncoder;
     private AppCompatActivity activity;
     private Button btn_generate;
     private Button btn_save;
     private Button btn_scan;


     @Override
     protected void onCreate(Bundle savedIntanceState){
         super.onCreate(savedIntanceState);
         setContentView(R.layout.activity_main);
         btn_scan=(Button)findViewById(R.id.btn_scan);
         btn_scan.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,Scann_your_QR.class);
                 startActivity(intent);
             }
         });

         qrImage=findViewById(R.id.imageView);
         qrValue=findViewById(R.id.editText);
         activity=this;
         btn_generate=findViewById(R.id.btn_generate);
         btn_generate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 inputValue=qrValue.getText().toString().trim();
                 if (inputValue.length()>0){
                     WindowManager manager= (WindowManager) getSystemService(WINDOW_SERVICE);
                     Display display= manager.getDefaultDisplay();
                     Point point= new Point();
                     display.getSize(point);
                     int width = point.x;
                     int height = point.y;
                     int smallerDimension = width < height ? width : height;
                     smallerDimension = smallerDimension * 3 / 4;

                     qrgEncoder = new QRGEncoder(
                             inputValue,null,QRGContents.Type.TEXT,smallerDimension);

                     try{
                         bitmap = qrgEncoder.encodeAsBitmap();
                         qrImage.setImageBitmap(bitmap);
                     }
                     catch (Exception e){
                         e.printStackTrace();
                     }
                 }else{
                     qrValue.setError("value required");
                 }
             }
         });
         btn_save=findViewById(R.id.btn_save);
         btn_save.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                     try {
                         boolean save=new QRGSaver().save(savePath,qrValue.getText().toString().trim(),bitmap,QRGContents.ImageType.IMAGE_JPEG);
                         String result = save ? "Image Saved" : "Image Not Saved";
                         Toast.makeText(activity,result,Toast.LENGTH_LONG).show();
                         qrValue.setText(null);
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                 }else{
                     ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                 }
             }
         });
     }
 }