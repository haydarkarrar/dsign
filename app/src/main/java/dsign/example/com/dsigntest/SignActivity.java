package dsign.example.com.dsigntest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static android.widget.Toast.makeText;


public class SignActivity extends AppCompatActivity {
    private static String TAG = "DSign";
    private static final int SIGN_FILE_SELECT_CODE = 0;
    private static final int  MY_PERMISSIONS_REQUEST_STORAGE = 123;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }

        Button  nextButton = (Button) findViewById(R.id.arrow);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignActivity.this, VerifyActivity.class);
                startActivity(i);
            }
        });

        TextView instructionText = (TextView) findViewById(R.id.sign_instruction);
        instructionText.setText("Please choose PDF file to be signed");
        instructionText.setTextSize(16);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "roboto-regular.ttf");
        instructionText.setTypeface(face);

//        String[] PERMISSIONS_STORAGE = {
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//        final int REQUEST_EXTERNAL_STORAGE = 1;
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    SignActivity.this,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }

        Button mSignButton = (Button) findViewById(R.id.choose_for_sign);
        mSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser(SIGN_FILE_SELECT_CODE);
            }
        });
    }

    private void fileChooser(int code) {
        Intent intent;
        if(Build.MANUFACTURER.equals("Samsung"))
        {
            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        }
        else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }


        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    code);
        } catch (android.content.ActivityNotFoundException ex) {
            makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGN_FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());

                    path = uri.getPath();
                    Log.d(TAG, "File Path: " + path);

                    try {
                        final String the_path = SignPDF.configureCertificate(this, path);
                        Log.d("DSIGNGTESTPATH", the_path);
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.activity_main),
                                "Document is signed", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Send via mail", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent .setType("vnd.android.cursor.dir/email");
                                String to[] = {""};
                                emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
                                emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(the_path)));
                                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "");
                                startActivity(Intent.createChooser(emailIntent , "Send email..."));
                            }
                        });
                        mySnackbar.setDuration(Snackbar.LENGTH_LONG);
                        mySnackbar.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {


                }
                return;
            }


        }
    }
}
