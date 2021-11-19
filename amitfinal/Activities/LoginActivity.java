package com.example.amitfinal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.Models.SharedPreferencesRememberMe;
import com.example.amitfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText edEmail, edPassword;
    Button loginBtn;
    ImageButton btnBack, btnPass;
    TextView tvLoginBtn, tvForgotPassword;
    FirebaseAuth fAuth;
    ProgressDialog progressDialog;
    CheckBox rememberMe;

    boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();//מסתיר את הActionBar

        edEmail = findViewById(R.id.edEmailLogin);
        edPassword = findViewById(R.id.edPasswordLogin);
        loginBtn = findViewById(R.id.loginBtn);
        tvLoginBtn = findViewById(R.id.tvLoginBtn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnBack = findViewById(R.id.btnBackLogin);
        rememberMe = findViewById(R.id.checkboxRememberMe);
        btnPass = findViewById(R.id.btnPassLogin);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

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

                progressBarDialog();
                //בודק האם הנתונים שהמשתמש הכניס תואמים למה שיש בפיירביס. במדיה וכן, המשתמש יכנס למשתמש שלו ויתחבר בהצלחה
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //יצירת אובייקט מסוג SharedPreferencesRememberMe והרצת פעולת הבנאי של האובייקט
                            SharedPreferencesRememberMe sharedPreferencesRememberMe = new SharedPreferencesRememberMe(LoginActivity.this);

                            //זוכר את המשתמש במידה וסימן את ה checkBox - RememberMe
                            // כלומר, במידה והמשתמש סימן את ה checkBox - RememberMe, מעודכנים בתיקייה RememberMe.xml הערכים(rememberMe, true) == (key, value)
                            sharedPreferencesRememberMe.edit(rememberMe.isChecked());

                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            startActivity(intent);
                            Animatoo.animateFade(LoginActivity.this);//אנימציה למעבר בין activities
                            Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            //מדפיס הודעת שגיאה במידה והייתה בעיה בהתחברות. כגון: סיסמה לא נכונה, משתמש שלא קיים או איימיל שלא מנוסח בצורה טובה
                            Toast.makeText(LoginActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        tvLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                Animatoo.animateFade(LoginActivity.this);//אנימציה למעבר בין activities
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText((view.getContext()));
                AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();

                        if(TextUtils.isEmpty(mail)){//בודק האם השדה של האיימיל ריק, אם כן מדפיס הודעת שגיאה
                            Toast.makeText(LoginActivity.this, "Email is Required!" , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error! Email not Found" , Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(LoginActivity.this);
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
    }

    //דיאלוג שמוצג כדי להראות שה  (ממשק משתמש)(user interface)UI לא נתקע אלא טוען נתונים מהFirebase
    public void progressBarDialog(){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Connecting To The User"); // Setting Message
        progressDialog.setMessage("Loading..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
    }
}