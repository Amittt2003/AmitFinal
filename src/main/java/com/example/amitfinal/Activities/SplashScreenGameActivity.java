package com.example.amitfinal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.R;

import java.util.Objects;

public class SplashScreenGameActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 4000;

    Animation fadeIn, fadeOut;
    TextView tvReady, tvGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_game);
        Objects.requireNonNull(getSupportActionBar()).hide();//מסתיר את הActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//FullScreen

        //Animations
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);

        tvReady = findViewById(R.id.tvReady);
        tvGo = findViewById(R.id.tvGo);

        tvReady.setAnimation(fadeIn);
        tvReady.setAnimation(fadeOut);

        // מפעיל את האנימציה על tvGO לאחר 2 שניות
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGo.setAnimation(fadeIn);
            }
        },2000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenGameActivity.this, GameActivity.class);
                intent.putExtra("gameMode",getIntent().getStringExtra("gameMode"));
                intent.putExtra("fullName",getIntent().getStringExtra("fullName"));
                intent.putExtra("level",getIntent().getIntExtra("level", 0));
                startActivity(intent);
                Animatoo.animateFade(SplashScreenGameActivity.this);
                finish();
            }
        },SPLASH_SCREEN);//מראה את המסך למשך 4 שניות
    }
}