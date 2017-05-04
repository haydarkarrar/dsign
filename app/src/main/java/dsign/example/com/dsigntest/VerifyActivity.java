package dsign.example.com.dsigntest;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static android.widget.Toast.makeText;

/**
 * Created by Бейбут on 12.03.2017.
 */

public class VerifyActivity extends AppCompatActivity {
    private static final int VERIFY_FILE_SELECT_CODE = 1;
    private static String TAG = "DSign";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Button mVerifyButton = (Button) findViewById(R.id.choose_for_verify);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser(VERIFY_FILE_SELECT_CODE);
            }
        });

        Button backButton = (Button) findViewById(R.id.arrow_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView instructionText = (TextView) findViewById(R.id.verify_instruction);
        instructionText.setText("Please choose PDF file to be verified");
        instructionText.setTextSize(16);
        instructionText.setTypeface(Typeface.SANS_SERIF);

    }

    private void fileChooser(int code) {
        Intent intent;
        if(Build.MANUFACTURER.equals("Samsung"))
        {
            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        }
        else
        {
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

            case VERIFY_FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());

                    String path = uri.getPath();
                    Log.d(TAG, "File Path: " + path);

                    try {
                        SignPDF.showSigner(this, path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
