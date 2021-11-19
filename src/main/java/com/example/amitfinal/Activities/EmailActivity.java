package com.example.amitfinal.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.R;

public class EmailActivity extends Activity {

    final String TO = "amitpro456@gmail.com";// האיימיל של מפתח האפליקציה
    EditText edSubject,edMessage;
    Button send;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        edSubject = findViewById(R.id.edEmailSubject);
        edMessage = findViewById(R.id.edEmailMessage);
        btnBack = findViewById(R.id.btnBackEmail);
        send = findViewById(R.id.btnEmail);

        send.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                String subject=edSubject.getText().toString();
                String message=edMessage.getText().toString();

                if(TextUtils.isEmpty(subject)){
                    edSubject.setError("Subject is Required!");
                    return;
                }

                if(TextUtils.isEmpty(message)){
                    edMessage.setError("Message is Required!");
                    return;
                }

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ TO});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                Animatoo.animateFade(EmailActivity.this);
                edSubject.setText("");
                edMessage.setText("");
            }

        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailActivity.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(EmailActivity.this);
            }
        });
    }
}

