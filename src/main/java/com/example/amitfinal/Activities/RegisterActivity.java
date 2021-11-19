package com.example.amitfinal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.Models.SharedPreferencesRememberMe;
import com.example.amitfinal.Models.User;
import com.example.amitfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText edFullName, edEmail, edPassword, edVerifyPassword;
    Button registerBtn;
    ImageButton btnBack, btnPass, btnPassVerify;
    TextView tvRegisterBtn;

    //Firebase Authentication
    //אחראי על כניסת משתמשים למערכת (איימיל וסיסמה)
    FirebaseAuth fAuth;

    //Firebase - Cloud Firestore
    //אחראי על שמירת הפרטים של המשתמשים(איימיל,שם והשיא של המשתמש בכל מוד של המשחק)
    FirebaseFirestore fStore;

    String userId;
    boolean show = true;
    boolean showVerify = true;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();//מסתיר את הActionBar

        edFullName = findViewById(R.id.edNameRegister);
        edEmail = findViewById(R.id.edEmailRegister);
        edPassword = findViewById(R.id.edPasswordRegister);
        edVerifyPassword = findViewById(R.id.edVerifyPasswordRegister);
        registerBtn = findViewById(R.id.registerBtn);
        tvRegisterBtn = findViewById(R.id.tvRegisterBtn);
        btnBack = findViewById(R.id.btnBackRegister);
        btnPass = findViewById(R.id.btnPassRegister);
        btnPassVerify = findViewById(R.id.btnPassRegisterVerify);

        // Initialize Firebase Authentication
        fAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore
        fStore = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String CheckPassword = edVerifyPassword.getText().toString();
                String fullName = edFullName.getText().toString();

                if(TextUtils.isEmpty(email)){//בודק האם השדה של האיימיל ריק, אם כן מדפיס הודעת שגיאה
                    edEmail.setError("Email is Required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){//בודק האם השדה של הסיסמה ריק, אם כן מדפיס הודעת שגיאה
                    edPassword.setError("Password is Required!");
                    return;
                }
                if(password.length() < 6){//בודק האם הסיסמה שהמשתמש הכניס קטנה מ6, אם כן מדפיס הודעת שגיאה
                    edPassword.setError("Password Must Be 6 Characters Or More");
                    return;
                }
                if(!password.equals(CheckPassword)){
                    edPassword.setError("The Passwords are not equal!");
                    edVerifyPassword.setError("The Passwords are not equal!");
                    return;
                }

                progressBarDialog();
                //יוצר את המשתמש בפיירבייס
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //בודק האם ההרשמה עברה בהצלחה
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();

                            //ה ID של המשתמש שנרשם כרגע למערכת
                            userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

                            // מקבל את ה document של המשתמש (לפי ה ID של המשתמש) הנרשם כרגע למערכת
                            DocumentReference documentReference = fStore.collection("Users").document(userId);

                            User user = new User(fullName, email, 0, 0, 1, 1);

                            //יוצר קובץ עם ID של המשתמש ומכניס לתוכו את האובייקט user - הפרטים של המשתמש
                            documentReference.set(user);

                            //יצירת אובייקט מסוג SharedPreferencesRememberMe והרצת פעולת הבנאי של האובייקט
                            SharedPreferencesRememberMe sharedPreferencesRememberMe = new SharedPreferencesRememberMe(RegisterActivity.this);

                            //זוכר את המשתמש
                            // כלומר, מעודכנים בתיקייה RememberMe.xml הערכים(rememberMe, true) == (key, value)
                            sharedPreferencesRememberMe.edit(true);

                            Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                            startActivity(intent);
                            Animatoo.animateFade(RegisterActivity.this);//אנימציה למעבר בין activities
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        tvRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                Animatoo.animateFade(RegisterActivity.this);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(RegisterActivity.this);
            }
        });

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show){
                    edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnPass.setBackgroundResource(0);
                    btnPass.setBackgroundResource(R.drawable.hide_pass);
                    show = false;
                }else{
                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnPass.setBackgroundResource(0);
                    btnPass.setBackgroundResource(R.drawable.show_pass);
                    show = true;
                }
            }
        });

        btnPassVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showVerify){
                    edVerifyPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnPassVerify.setBackgroundResource(0);
                    btnPassVerify.setBackgroundResource(R.drawable.hide_pass);
                    showVerify = false;
                }else{
                    edVerifyPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnPassVerify.setBackgroundResource(0);
                    btnPassVerify.setBackgroundResource(R.drawable.show_pass);
                    showVerify = true;
                }
            }
        });
    }

    //דיאלוג שמוצג כדי להראות שה  (ממשק משתמש)(user interface)UI לא נתקע אלא טוען נתונים מהFirebase
    public void progressBarDialog(){
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Creating User"); // Setting Message
        progressDialog.setMessage("Loading..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
    }
}