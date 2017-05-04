package dsign.example.com.dsigntest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

/**
 * Created by Бейбут on 10.04.2017.
 */

public class ChooseCertificateActivity extends AppCompatActivity {
    private static final int VERIFY_FILE_SELECT_CODE = 1;
    private static String TAG = "DSign";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_certificate);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        TextView instructionText = (TextView) findViewById(R.id.certificate_instruction);
        instructionText.setText("The certificate will be used to sign documents");
        instructionText.setTextSize(16);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "roboto-regular.ttf");
        instructionText.setTypeface(face);

        Button mVerifyButton = (Button) findViewById(R.id.certificate_chooser);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooserSamsung(VERIFY_FILE_SELECT_CODE);
            }
        });
    }

    private void fileChooserSamsung(int code) {
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



                    LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                    final View textEntryView = factory.inflate(R.layout.dialog_view, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(ChooseCertificateActivity.this, R.style.AppCompatAlertDialogStyle);
                    final EditText loginField = (EditText) textEntryView.findViewById(R.id.pkpassword);
                    final TextView pktextView = (TextView) textEntryView.findViewById(R.id.pk_instruction);
                    pktextView.setText("Please enter password to private key.");
                    pktextView.setTextSize(16);
                    Typeface face = Typeface.createFromAsset(getAssets(),
                            "roboto-regular.ttf");
                    pktextView.setTypeface(face);
                    alert.setView(textEntryView)
                            .setPositiveButton("Enter",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            SharedPreferences sharedPref = getSharedPreferences(
                                                    "DSIGN", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("PK_PASS", loginField.getText().toString());

                                            editor.commit();
                                            Intent intent = new Intent(ChooseCertificateActivity.this, SignActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                        }
                                    });
                    alert.show();
                    SharedPreferences sharedPref = getSharedPreferences(
                            "DSIGN", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("CERT_PATH", path);

                    editor.commit();

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
