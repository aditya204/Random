package com.quantum.random;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView mSpeechTxt;
    private ImageView mMicImage;
    private Button mDownlod;
    private Button mMail;
    private Button mSMS;
    private Button mShare;

    private static String txt = "";
    private final String CHANNEL_ID = "ai";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        mSpeechTxt = findViewById( R.id.speech_txt );
        mMicImage = findViewById( R.id.test_mic );
        mDownlod = findViewById( R.id.download_txt );
        mMail=findViewById( R.id.mail );
        mSMS=findViewById( R.id.send_sms );
        mShare=findViewById( R.id.share );



        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel( OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE );

        // OneSignal Initialization
        OneSignal.startInit( this )
                .inFocusDisplaying( OneSignal.OSInFocusDisplayOption.Notification )
                .unsubscribeWhenNotificationsAreDisabled( true )
                .init( );


        ///speech_to_text
        mMicImage.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
                intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
                intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault( ) );
                if (intent.resolveActivity( getPackageManager( ) ) != null) {
                    startActivityForResult( intent, 1 );


                } else {
                    Toast.makeText( MainActivity.this, "Your Device Doesn't Support Speech", Toast.LENGTH_SHORT ).show( );
                }


            }
        } );



        ///send mail.

        mMail.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                showEmailDialog();
//
            }
        } );



        //send SMS

        mSMS.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
               showSmsDialog();
            }
        } );



        ///share
        mShare.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                share(mSpeechTxt.getText().toString());
            }
        } );




        ///Download_txt
        mDownlod.setOnClickListener( new View.OnClickListener( ) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                writeFiles( mSpeechTxt.getText( ).toString( ) );
                String path = getExternalFilesDir( Environment.DIRECTORY_DOCUMENTS ).toString( ) + "/" + "DOC_aditya" + System.currentTimeMillis( ) + ".pdf";
                File file = new File( path );
                if (file.exists( )) {
                    try {
                        file.createNewFile( );
                    } catch (IOException e) {
                        e.printStackTrace( );

                    }
                }
                Document document = new Document( PageSize.A4 );
                try {
                    PdfWriter.getInstance( document, new FileOutputStream( file.getAbsoluteFile( ) ) );
                    Log.d( "file_path", "" + file.getAbsolutePath( ) );
                } catch (DocumentException e) {
                    e.getMessage( );
                    Toast.makeText( MainActivity.this, e.getMessage( ).toString( ), Toast.LENGTH_SHORT ).show( );
                } catch (FileNotFoundException e) {
                    e.printStackTrace( );
                    Toast.makeText( MainActivity.this, e.getMessage( ).toString( ), Toast.LENGTH_SHORT ).show( );

                }
                document.open( );

                try {
                    document.add( new Paragraph( mSpeechTxt.getText( ).toString( ) ) );
                } catch (DocumentException e) {
                    e.printStackTrace( );
                    Toast.makeText( MainActivity.this, e.getMessage( ).toString( ), Toast.LENGTH_SHORT ).show( );

                }
                Toast.makeText( MainActivity.this, "successful Downloaded", Toast.LENGTH_SHORT ).show( );


                document.close( );
            }
        } );


    }

    public void composeEmail(String[] addresses, String subject,String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData( Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content );

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void writeFiles(String s) {
        File path = getApplicationContext( ).getFilesDir( );
        try {
            FileOutputStream writer = new FileOutputStream( new File( path, "adityarandom.txt" ) );
            writer.write( s.getBytes( ) );
            writer.close( );


        } catch (Exception e) {
            e.printStackTrace( );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result_txt = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );

                    txt = result_txt.get( 0 );
                    mDownlod.setVisibility( View.VISIBLE );
                    mSpeechTxt.setText( result_txt.get( 0 ) );

                }


                break;

        }


    }

    public void sendSMS(String mobile,String text)
    {
        Uri uri = Uri.parse("smsto:"+mobile);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", text);
        startActivity(it);

    }

    public void  share (String content){
        Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, content );
        startActivity(Intent.createChooser(intent2, "Share via"));
    }

    public void showEmailDialog(){
        final Dialog dialog=new Dialog( this,R.style.dialog_theme );
        dialog.setContentView( R.layout.dialog_email );
        dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable( true );
        dialog.setCanceledOnTouchOutside( true );
        final TextView ok=dialog.findViewById( R.id.btAction );
        final TextView wrongtxt=dialog.findViewById( R.id.textView5 );
        final EditText subject=dialog.findViewById( R.id.etSubject );
        final EditText email= dialog.findViewById( R.id.etEmail );
        ImageView cross=dialog.findViewById( R.id.ivCrossDialog );

        cross.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        ok.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                final String mail=email.getText().toString();
                if(isEmailValid(mail)){
                    wrongtxt.setVisibility( View.GONE );
                    String [] arr=new String[1];
                    arr[0]=mail;
                    composeEmail( arr,subject.getText().toString(),mSpeechTxt.getText().toString() );

                }else{
                    wrongtxt.setVisibility( View.VISIBLE );
                }
            }
        } );


        dialog.show();





    }

    public void showSmsDialog(){
        final Dialog dialog=new Dialog( this,R.style.dialog_theme );
        dialog.setContentView( R.layout.dialig_phone );
        dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable( true );
        dialog.setCanceledOnTouchOutside( true );
        final TextView ok=dialog.findViewById( R.id.btAction );
        final TextView wrongtxt=dialog.findViewById( R.id.textView5 );
        final EditText phoneNo= dialog.findViewById( R.id.etEmail );
        ImageView cross=dialog.findViewById( R.id.ivCrossDialog );

        cross.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        ok.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                final String number=phoneNo.getText().toString();
                if(number.length()==10){
                    wrongtxt.setVisibility( View.GONE );
                    sendSMS( number, mSpeechTxt.getText().toString() );
                }else{
                    wrongtxt.setVisibility( View.VISIBLE );
                }
            }
        } );


        dialog.show();





    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
