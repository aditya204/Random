package com.quantum.random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.onesignal.OneSignal;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView mSpeechTxt;
    private ImageView mMicImage;
    private Button mDownlod;
    private static  String txt="";
    private final String CHANNEL_ID = "ai";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );




        mSpeechTxt=findViewById( R.id.speech_txt );
        mMicImage=findViewById( R.id.test_mic );
        mDownlod=findViewById( R.id.download_txt );


        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        ///speech_to_text
        mMicImage.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
                intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
                intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault() );
                if(intent.resolveActivity( getPackageManager())!=null ){
                    startActivityForResult( intent,1 );


                }else {
                    Toast.makeText( MainActivity.this, "Your Device Doesn't Support Speech", Toast.LENGTH_SHORT ).show( );
                }



            }
        } );



        ///Download_txt
        mDownlod.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
             String path =getExternalFilesDir( null ).toString()+"/"+ "DOC"+System.currentTimeMillis() +".pdf";
                File file=new File( path );
                if(file.exists()){
                    try {
                        file.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();

                    }
                }
                Document document=new Document( PageSize.A4 );
                try{
                    PdfWriter.getInstance( document,new FileOutputStream( file.getAbsoluteFile() ) );
                }catch (DocumentException e){
                    e.getMessage();
                    Toast.makeText( MainActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT ).show();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText( MainActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT ).show();

                }
                document.open();

                try {
                    document.add( new Paragraph( mSpeechTxt.getText().toString() ) );
                }catch (DocumentException e){
                    e.printStackTrace();
                    Toast.makeText( MainActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT ).show();

                }
                Toast.makeText( MainActivity.this, "successful Downloaded", Toast.LENGTH_SHORT ).show( );


                document.close();
            }
        } );



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        switch (requestCode){
            case 1:
                if(resultCode ==RESULT_OK && data!=null){

                    ArrayList<String> result_txt=data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );

                    txt=result_txt.get( 0 );
                    mDownlod.setVisibility( View.VISIBLE );
                    mSpeechTxt.setText( result_txt.get( 0 ) );

                }


                break;

        }


    }





}
