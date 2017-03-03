package com.example.ihyunbeom.owls;

/**
 * Created by ihyunbeom on 2017-03-03.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signin =(Button) findViewById(R.id.loginButton);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(signin);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //signupButton
        TextView signup = (TextView) findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signup = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(signup);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
