package dsign.example.com.dsigntest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPrefs = getSharedPreferences("DSIGN", MODE_PRIVATE);
        if(!sharedPrefs.contains("pin")){


            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);

        }
        else
        {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

    }

}
