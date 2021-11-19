    package com.example.amitfinal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.Models.InternetConnection;
import com.example.amitfinal.Models.SharedPreferencesRememberMe;
import com.example.amitfinal.Models.User;
import com.example.amitfinal.Models.UserScore;
import com.example.amitfinal.Models.UserScores;
import com.example.amitfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    //An enum is a special "class" that represents a group of constants (unchangeable variables, like final variables).
    enum GAME_MODE {
        CLASSIC, CHAOS
    }

    Button btnLogin, btnRegister , btnScore, btnHowToPlay;
    ImageButton btnEmail;

    TextView tvTopPlayerClassic, tvTopPlayerChaos, tvTopScoreClassic, tvTopScoreChaos, tvTopLevelClassic, tvTopLevelChaos;

    //Firebase - Cloud Firestore
    //אחראי על שמירת הפרטים של המשתמשים(איימיל,שם והשיא של המשתמש בכל מוד של המשחק)
    FirebaseFirestore fStore;

    //Firebase Authentication
    //אחראי על כניסת משתמשים למערכת (איימיל וסיסמה)
    FirebaseAuth fAuth;

    //StringBuilder objects are like String objects, except that they can be modified(ניתנים לשינוי)
    StringBuilder msg = new StringBuilder();

    //יוצר אובייקט חדש מסוג UserScores
    UserScores userScores = new UserScores();

    ProgressDialog progressDialog;

    GAME_MODE gameMode;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();//מסתיר את הActionBar

        //בודק האם למשתמש יש חיבור לאינטרנט
        if (!new InternetConnection(MainActivity.this).isConnected()){
            showConnectionDialog();
        }

        // Initialize Firebase Firestore
        fStore = FirebaseFirestore.getInstance();//firebase - Firestore

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();//firebase - Authentication

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister  = findViewById(R.id.btnRegister);
        btnScore = findViewById(R.id.btnScoreTable);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);
        btnEmail = findViewById(R.id.btnEmail);

        tvTopPlayerClassic = findViewById(R.id.tvPlayerClassic);
        tvTopPlayerChaos = findViewById(R.id.tvPlayerChaos);
        tvTopScoreClassic = findViewById(R.id.tvScoreClassic);
        tvTopScoreChaos = findViewById(R.id.tvScoreChaos);
        tvTopLevelClassic = findViewById(R.id.tvBestLevelClassic);
        tvTopLevelChaos = findViewById(R.id.tvBestLevelChaos);

        //יצירת אובייקט מסוג SharedPreferencesRememberMe והרצת פעולת הבנאי של האובייקט
        SharedPreferencesRememberMe sharedPreferencesRememberMe = new SharedPreferencesRememberMe(MainActivity.this);

        //בודק האם קיים ה key - rememberMe בתוך התיקייה RememberMe.xml
        if(sharedPreferencesRememberMe.contains()){

            //המשתנה rememberMe מקבל את ערך ה Value של ה Key - rememberMe
            boolean rememberMe = sharedPreferencesRememberMe.getBoolean();

            //במידה והמשתמש היה מעוניין שהמערכת תזכור אותו ,כלומר, המשתנה rememberMe הוא true, המשתמש לא יידרש להתחבר בשנית
            if (rememberMe) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
                Animatoo.animateFade(MainActivity.this);//אנימציה למעבר בין activities
                Toast.makeText(MainActivity.this, "User Already Logged In", Toast.LENGTH_SHORT).show();
                finish();//סוגר את הActivity
            }
            else {
                getTopPlayers();
            }
        }else {
            getTopPlayers();
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Animatoo.animateFade(MainActivity.this);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                Animatoo.animateFade(MainActivity.this);
            }
        });

        btnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreTable();
            }
        });

        btnHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                how_to_play_dialog();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                startActivity(intent);
                Animatoo.animateFade(MainActivity.this);            }
        });
    }

    //מציג את המשתמש במקום הראשון בכל מוד(chaos,classic)
    public void getTopPlayers(){
        createScoreTable(true);
    }

    //מציג את טבלת השיאים של 10 השחקנים הטובים ביותר בכל מוד באמצעות דיאלוג
    public void showScoreTable(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose Game Mode");
        builder.setTitle("Top 10 Players");

        builder.setPositiveButton("Classic", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton("Chaos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gameMode = GAME_MODE.CLASSIC;
                dialog.setTitle("Top 10 Players \n Mode: Classic");
                createScoreTable(false);
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gameMode = GAME_MODE.CHAOS;
                dialog.setTitle("Top 10 Players \n Mode: Chaos");
                createScoreTable(false);
            }
        });
    }

    public void createScoreTable(boolean checkTopPlayers){
        progressBarDialog();

        Log.d("D", "" + checkTopPlayers);

        //מקבל את ה collection בשם "Users" שנמצאת בתןך הFirestore
        //בתןך Users נמצאים Documents שכל אחד מהם שומר את הID של כל משתמש שמחובר למערכת, וכל ID שומר את הפרטים על המשתמש
        CollectionReference docRef = fStore.collection("Users");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //מאפס את המערכים
                    userScores.clear();

                    //עובר על כל הdocuments שנמצאים ב collection Users
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                        //מקבל מהdocument את האובייקט של User
                        User user = document.toObject(User.class);

                        String name = user.getName();
                        int bestScoreClassic = user.getBestScoreClassic();
                        int bestScoreChaos = user.getBestScoreChaos();

                        int bestLevelClassic = user.getBestLevelClassic();
                        int bestLevelChaos = user.getBestLevelChaos();

                        //מכניס את הערכים בצורה (User(name, scoreClassic,scoreChaos)) לתוך המערך
                        UserScore userScore = new UserScore(name, bestScoreClassic, bestScoreChaos, bestLevelClassic, bestLevelChaos);
                        userScores.add(userScore);
                    }

                    //ממיין את המערך bestScoreClassicArray מהערך (score) הגדול לקטן
                    userScores.sort();

                    int ClassicArraySize = userScores.getSizeClassicArray();
                    int ChaosArraySize = userScores.getSizeChaosArray();

                    if(checkTopPlayers){
                        if(ChaosArraySize > 0 || ClassicArraySize > 0){
                            String topPlayerNameClassic = userScores.getTopNameClassic();
                            String topPlayerNameChaos = userScores.getTopNameChaos();
                            int topPlayerScoreClassic = userScores.getTopScoreClassic();
                            int topPlayerScoreChaos = userScores.getTopScoreChaos();
                            int topPlayerLevelClassic = userScores.getTopLevelClassic();
                            int topPlayerLevelChaos = userScores.getTopLevelChaos();

                            //מציג את המשתמש במקום הראשון בכל מוד(chaos,classic)

                            tvTopPlayerClassic.setText("Player: " + topPlayerNameClassic);
                            tvTopPlayerChaos.setText("Player: " + topPlayerNameChaos);

                            tvTopScoreClassic.setText("Score: " + topPlayerScoreClassic);
                            tvTopScoreChaos.setText("Score: " + topPlayerScoreChaos);

                            if(topPlayerLevelClassic == 4){
                                tvTopLevelClassic.setText("Level: Done");
                            }else{
                                tvTopLevelClassic.setText("Level: " + topPlayerLevelClassic);

                            }

                            if(topPlayerLevelChaos == 4){
                                tvTopLevelChaos.setText("Level: Done");
                            }else{
                                tvTopLevelChaos.setText("Level: " + topPlayerLevelChaos);
                            }
                        }
                    }
                    else {
                        top10();

                        if (msg.length() != 0) {
                            dialog.setMessage(msg);
                        } else {
                            dialog.setMessage("No Users in the System");
                        }
                        msg.setLength(0);//מאפס את תוכן טבלת השיאים(msg)
                    }
                    progressDialog.dismiss();
                }
            }
        });
    }

    //יוצר את התוכן של טבלת השיאים
    public void top10(){
        if (gameMode == null) {
            msg.append("");
        }else{
            switch (gameMode) {

                case CLASSIC:

                    if(userScores.getSizeClassicArray() > 10){
                        for (int i = 0; i < 10; i++) {

                            String name = userScores.getNameByIndexClassic(i);
                            int score = userScores.getScoreByIndexClassic(i);
                            int level = userScores.getLevelByIndexClassic(i);

                            msg.append(i + 1).append(". ").append(name).append("\n Best Score: ").append(score);

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(level == 4){
                                msg.append("\n Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("\n Level: ").append(level).append("\n \n");
                            }
                        }
                    }else{
                        for (int i = 0; i < userScores.getSizeClassicArray(); i++) {

                            String name = userScores.getNameByIndexClassic(i);
                            int score = userScores.getScoreByIndexClassic(i);
                            int level = userScores.getLevelByIndexClassic(i);

                            msg.append(i + 1).append(". ").append(name).append("\n Best Score: ").append(score);

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(level == 4){
                                msg.append("\n Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("\n Level: ").append(level).append("\n \n");
                            }
                        }
                    }
                    break;

                case CHAOS:

                    if(userScores.getSizeChaosArray() > 10){
                        for (int i = 0; i < 10; i++) {

                            String name = userScores.getNameByIndexChaos(i);
                            int score = userScores.getScoreByIndexChaos(i);
                            int level = userScores.getLevelByIndexChaos(i);

                            msg.append(i + 1).append(". ").append(name).append("\n Best Score: ").append(score);

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(level == 4){
                                msg.append("\n Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("\n Level: ").append(level).append("\n \n");
                            }
                        }
                    }else{
                        for (int i = 0; i < userScores.getSizeChaosArray(); i++) {

                            String name = userScores.getNameByIndexChaos(i);
                            int score = userScores.getScoreByIndexChaos(i);
                            int level = userScores.getLevelByIndexChaos(i);

                            msg.append(i + 1).append(". ").append(name).append("\n Best Score: ").append(score);

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(level == 4){
                                msg.append("\n Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("\n Level: ").append(level).append("\n \n");
                            }
                        }
                    }
                    break;
            }
        }
    }

    //מציג דיאלוג בו מוצג הוראות המשחק
    public void how_to_play_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("How To Play");
        String message = "The principle of the game is simple: the player has to memorize the series of illuminated keys and reproduce it. The purpose of the game is to reproduce the longest series of colors / sounds randomly generated by the Simon.\n" +
                "In each round a new key is added to the series and the game becomes increasingly difficult because the player's memory is more and more solicited.\n" +
                "\n" +
                "1- At the beginning of the game, one of the 4 keys lights up randomly producing simultaneously a sound associated to the key.\n" +
                "2- The player has to press the same key.\n" +
                "3- Next, the Simon turns back the same light on and a second one, again randomly.\n" +
                "4- The player has to reproduce this chain of light using his memory.\n" +
                "5- And so on... In each round a new key is added to the series and the game becomes all the more difficult as the player's memory is put to the test.\n" +
                "6- If the player doesn't make any mistake, the game goes on, so it is an endless game!";
        builder.setMessage(message);

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        builder.setNegativeButton ("Ok", null);//בלחיצת כפתור סוגר את הדיאלוג

        // יוצר את ה AlertDialog ומראה אותו למשתמש
        builder.create();
        builder.show();
    }

    //דיאלוג שמוצג כדי להראות שה  (ממשק משתמש)(user interface)UI לא נתקע אלא טוען נתונים מהFirebase
    public void progressBarDialog(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading Score Table"); // Setting Message
        progressDialog.setMessage("Loading..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
    }

    //במידה ואין למשתמש חיבור לאינטרנט, דיאלוג זה יופיע לו ויבקש ממנו להתחבר לאינטרנט
    public void showConnectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to the internet to proceed further");
        builder.setCancelable(false);

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//מעביר את המשתמש להגדרות הWIFI במכשיר שלו
                Animatoo.animateFade(MainActivity.this);
            }
        });

        // יוצר את ה AlertDialog ומראה אותו למשתמש
        builder.create();
        builder.show();
    }
}