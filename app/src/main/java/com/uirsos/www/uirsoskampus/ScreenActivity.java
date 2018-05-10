package com.uirsos.www.uirsoskampus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.uirsos.www.uirsoskampus.SignUp.LoginActivity;

public class ScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        TextView logo = findViewById(R.id.logoUirsos);
        TextView desc = findViewById(R.id.deskripsiUirsos);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
        logo.setAnimation(anim);
        desc.setAnimation(anim);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final Intent i = new Intent(ScreenActivity.this, LoginActivity.class);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };

        timer.start();
    }
}
