package com.example.jeff.viewpagerdelete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartUpActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        loginButton = (Button) findViewById(R.id.loginSubmit);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authenticate();
            }
        });
    }

    private void authenticate(){
        Intent quizMe = new Intent(this, QuizLoaderActivity.class);
        startActivity(quizMe);
    }
}


