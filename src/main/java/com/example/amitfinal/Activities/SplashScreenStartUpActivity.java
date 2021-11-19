package com.example.amitfinal.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.R;

import java.util.Objects;

public class SplashScreenStartUpActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 3000;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_start_up);
        Objects.requireNonNull(getSupportActionBar()).hide();//מסתיר את הActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//FullScreen

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image = findViewById(R.id.imageSplash);
        logo = findViewById(R.id.textLogo);
        slogan = findViewById(R.id.textSlogan);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenStartUpActivity.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateFade(SplashScreenStartUpActivity.this);
                finish();
            }
        },SPLASH_SCREEN);//מראה את המסך למשך 3 שניות
    }
}