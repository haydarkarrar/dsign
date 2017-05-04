package dsign.example.com.dsigntest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Бейбут on 26.04.2017.
 */

public class RegisterActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        final EditText editText = (EditText) findViewById(R.id.edittext_new_pin);
        Button createPIN = (Button) findViewById(R.id.button_new_pin);
        createPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPin = editText.getText().toString();
                SharedPreferences sharedPrefs = getSharedPreferences("DSIGN", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("pin", newPin);
                editor.commit();

                Intent i = new Intent(RegisterActivity.this, ChooseCertificateActivity.class);
                startActivity(i);
            }
        });
    }
}