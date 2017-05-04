package dsign.example.com.dsigntest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Бейбут on 26.04.2017.
 */

public class LoginActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        final EditText editText = (EditText) findViewById(R.id.edittext_enter_pin);
        Button pinButton = (Button) findViewById(R.id.button_submit_pin);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = getSharedPreferences("DSIGN", MODE_PRIVATE);
                String pinCode = sharedPrefs.getString("pin", "null").toString();
                if(pinCode.equals(editText.getText().toString())) {
                    SharedPreferences sharedPreferences  = getSharedPreferences("DSIGN", MODE_PRIVATE);
                    if(!sharedPreferences.contains("CERT_PATH")) {

                        Intent i = new Intent(LoginActivity.this, ChooseCertificateActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(LoginActivity.this, SignActivity.class);
                        startActivity(i);
                    }


                }else {

                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.activity_enter_pin),
                            "PIN is incorrect", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }


            }
        });
    }


}
