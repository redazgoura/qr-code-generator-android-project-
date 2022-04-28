package com.example.qrcodegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodegenerator.Model.QRGeoModel;
import com.example.qrcodegenerator.Model.QRURLModel;
import com.example.qrcodegenerator.Model.QRVCARDmodel;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scann_your_QR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtresult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scann_your__qr);

        scannerView =(ZXingScannerView)findViewById(R.id.zxscan);
        txtresult =(TextView)findViewById(R.id.txtresult);

        //request permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(Scann_your_QR.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(Scann_your_QR.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Here we can receive raw result
       // processrawResult(rawResult.getText());
       // scannerView.startCamera(Scann_your_QR.this);
        txtresult.setText(rawResult.getText());
        scannerView.startCamera();


    }

   private void  processrawResult(String text) {
       if (text.startsWith("")) {
           String[] tokens = text.split("\n");
           QRVCARDmodel qrvcarDmodel = new QRVCARDmodel();
           for (int i = 0; i < tokens.length; i++) {
               if (tokens[i].startsWith(":")) {
                   qrvcarDmodel.setType(tokens[i].substring("".length()));//remove begin : to get type
               } else if (tokens[i].startsWith("Name:")) {
                   qrvcarDmodel.setName(tokens[i].substring("Name:".length()));
               } else if (tokens[i].startsWith("ORG:")) {
                   qrvcarDmodel.setOrg(tokens[i].substring("ORG:".length()));
               } else if (tokens[i].startsWith("TEL:")) {
                   qrvcarDmodel.setTel(tokens[i].substring("TEL:".length()));
               } else if (tokens[i].startsWith("URL:")) {
                   qrvcarDmodel.setUrl(tokens[i].substring("URL:".length()));
               } else if (tokens[i].startsWith("EMAIL:")) {
                   qrvcarDmodel.setEmail(tokens[i].substring("EMAIL:".length()));
               } else if (tokens[i].startsWith("Adresse:")) {
                   qrvcarDmodel.setAdresse(tokens[i].substring("Adresse:".length()));
               } else if (tokens[i].startsWith("NOTE:")) {
                   qrvcarDmodel.setNote(tokens[i].substring("NOTE:".length()));
               } else if (tokens[i].startsWith("SUMMARY:")) {
                   qrvcarDmodel.setSummary(tokens[i].substring("SUMMARY:".length()));
               } else if (tokens[i].startsWith("DSTART:")) {
                   qrvcarDmodel.setDtstart(tokens[i].substring("DSTART:".length()));
               } else if (tokens[i].startsWith("DTEND:")) {
                   qrvcarDmodel.setDtend(tokens[i].substring("DTEND:".length()));
               }
               //try to show
               txtresult.setText(qrvcarDmodel.getType());
           }
       } else if (text.startsWith("http://") ||
               text.startsWith("https://") ||
               text.startsWith("www.")) {
           QRURLModel qrurlModel = new QRURLModel(text);
           txtresult.setText(qrurlModel.getUrl());

       } else if (text.startsWith("geo:")) {
           QRGeoModel qrGeoModel = new QRGeoModel();
           String delmis = "[  ,  ?qw ]+";
           String tokens[] = text.split(delmis);

           for (int i = 0; i < tokens.length; i++) {
               if (tokens[i].startsWith(" geo:")) {
                   qrGeoModel.setLat(tokens[i].substring("geo:".length()));
               }
           }
           qrGeoModel.setLat(tokens[0].substring("geo :".length()));
           qrGeoModel.setLng(tokens[1]);
           qrGeoModel.setGeo_place(tokens[2]);
           /////qrGeoModel.setLng(tokens[3]);

           txtresult.setText(qrGeoModel.getLat() + "/" + qrGeoModel.getLng());
       } else {

           txtresult.setText(text);
       }


   }
    }

