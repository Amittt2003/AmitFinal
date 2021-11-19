package com.example.amitfinal.Activities;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amitfinal.Models.User;
import com.example.amitfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import android.os.Vibrator;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    //An enum is a special "class" that represents a group of constants (unchangeable variables, like final variables).
    enum COLOR {
        GREEN, RED, BLUE, YELLOW
    }

    enum CHECK {
        LEVEL_PASSED, GAME_OVER, EXIT_LEVEL
    }

    Random rand = new Random();
    Handler myHandler = new Handler();

    Button btnGreen, btnRed, btnYellow, btnBlue;
    TextView tvScore;

    MediaPlayer greenSound, redSound, blueSound, yellowSound, lostGameSound;
    Animation animation;

    boolean gameOver = false;

    // מערך ששומר בתוכו את כל הצבעים שנמצאים בסדרה שעליה השחקן צריך לחזור
    ArrayList<COLOR> allColors = new ArrayList<>();

    User user;

    int count = 0;
    int currentScore = 0;
    int bestScoreClassic = 0;
    int bestScoreChaos = 0;
    int bestLevelClassic = 1;
    int bestLevelChaos = 1;
    int currentLevel = 1;

    final int LEVEL1 = 2;
    final int LEVEL2 = 3;
    final int LEVEL3 = 4;

    String gameMode;

    //Firebase Authentication
    //אחראי על כניסת משתמשים למערכת (איימיל וסיסמה)
    FirebaseAuth fAuth;

    //Firebase - Cloud Firestore
    //אחראי על שמירת הפרטים של המשתמשים(איימיל,שם והשיא של המשתמש בכל מוד של המשחק)
    FirebaseFirestore fStore;

    String userId, fullName;
    DocumentReference docRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//FullScreen

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {

            //ה ID של המשתמש המחובר למערכת
            userId = fAuth.getCurrentUser().getUid();

            // מקבל את הdocument של המשתמש (לפי ה ID של המשתמש) המחובר כרגע למערכת
            docRef = fStore.collection("Users").document(userId);
        }

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down);

        tvScore = findViewById(R.id.tvScore);

        btnGreen = findViewById(R.id.btnGreen);
        btnRed = findViewById(R.id.btnRed);
        btnBlue = findViewById(R.id.btnBlue);
        btnYellow = findViewById(R.id.btnYellow);

        btnGreen.setOnClickListener(this);
        btnRed.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btnYellow.setOnClickListener(this);

        greenSound = MediaPlayer.create(this, R.raw.sound1);
        redSound = MediaPlayer.create(this, R.raw.sound2);
        blueSound = MediaPlayer.create(this, R.raw.sound3);
        yellowSound = MediaPlayer.create(this, R.raw.sound4);
        lostGameSound = MediaPlayer.create(this, R.raw.losgamesound);

        gameMode = getIntent().getStringExtra("gameMode");//מקבל את הבחירה של המשתמש שנקלטה בUserActivity לגבי סוג המשחק
        fullName = getIntent().getStringExtra("fullName");
        currentLevel = getIntent().getIntExtra("level", 0);

        disableButtons();

        if(currentLevel == 4){
            Toast.makeText(GameActivity.this, "Level: Done", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(GameActivity.this, "Level: " + currentLevel, Toast.LENGTH_SHORT).show();

        }
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(GameActivity.this, "Watch and Repeat", Toast.LENGTH_SHORT).show();
                game();
            }
        }, 1200 );
    }

    //מבטל מהמשתמש את האפשרות ללחוץ על הכפתורים
    public void disableButtons() {
        btnGreen.setEnabled(false);
        btnRed.setEnabled(false);
        btnBlue.setEnabled(false);
        btnYellow.setEnabled(false);
    }

    //מחזיר למשתמש את האפשרות ללחוץ על הכפתורים
    public void enableButtons() {
        btnGreen.setEnabled(true);
        btnRed.setEnabled(true);
        btnBlue.setEnabled(true);
        btnYellow.setEnabled(true);
    }

    // מפעיל אנימציה וסאונד לכפתור
    public void clickGreen() {
        btnGreen.startAnimation(animation);
        greenSound.start();
    }

    // מפעיל אנימציה וסאונד לכפתור
    public void clickRed() {
        btnRed.startAnimation(animation);
        redSound.start();
    }

    // מפעיל אנימציה וסאונד לכפתור
    public void clickBlue() {
        btnBlue.startAnimation(animation);
        blueSound.start();
    }

    // מפעיל אנימציה וסאונד לכפתור
    public void clickYellow() {
        btnYellow.startAnimation(animation);
        yellowSound.start();

    }

    // אלגוריתם להוספת צבעים לרצף הצבעים וללחיצת לחצן על ידי המערכת
    public void game() {
        disableButtons();
        vibrate();

        count = 0;
        int random;

        random = rand.nextInt(4);// מספר רנדומלי מ0 עד 4(לא כולל)
        allColors.add(COLOR.values()[random]);//מוסיף צבע רנדומלי למערך allColors

        //game mode: chaos
        // מעדכן במערך allColors צבעים רנדומליים. הסדר שונה כל סיבוב. משנה את רצץ הצבעים.
        if(gameMode.equals("chaos")){
            for (int i = 0; i < allColors.size(); i++) {
                random = rand.nextInt(4);// מספר רנדומלי מ0 עד 4(לא כולל)
                allColors.set(i, COLOR.values()[random]);// משנה את הצבע שהיה במקום i במערך בצבע רנדומלי
            }
        }

        // עובר על מערך הצבעים allColors ו"לוחץ" - (מפעיל את האנימציה של הלחיצה וסאונד מתאים לכל צבע) לפי רצף הצבעים שנמצא במערך בהפרש של שנייה לכל לחיצה
        for (int i = 0; i < allColors.size(); i++) {
            int finalI = i;
            myHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    switch (allColors.get(finalI)) {
                        case GREEN:
                            clickGreen();
                            break;
                        case RED:
                            clickRed();
                            break;
                        case BLUE:
                            clickBlue();
                            break;
                        case YELLOW:
                            clickYellow();
                            break;
                    }
                }
            }, 1000 * i);// דיילי של שנייה בין כל לחיצת כפתור על ידי המערכת
        }

        //אחרי שהכפתור האחרון נלחץ על ידי המערכת, המשתמש יוכל ללחוץ על הכפתורים(לאחר 900 מאיות)
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                enableButtons();
            }
        }, 900 * allColors.size());
    }


    //מעדכן את השיא של המשתמש במידת הצורך
    // הפונקציה בודקת האם הניקוד הנוכחי שהמשתמש הגיע אליו במשחק גדול מהשיא שלו,
    // במידה וכן, הפונקציה מעדכנת את השיא הנוכחי של המשתמש בFirestore
    public void score(CHECK check, String level, String levelGoal, boolean isFinish){
        progressBarDialog();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    if (document != null) {

                        //מקבל מהdocument את האובייקט של User
                        user = document.toObject(User.class);

                        switch (gameMode){
                            case "classic":

                                bestScoreClassic = Objects.requireNonNull(user).getBestScoreClassic();//בודק האם המשתנה שווה null ובמידה שכן זורק NullPointerException, כלומר שגיאה שהמשתנה שווה ל null
                                if(currentScore > bestScoreClassic){//בודק האם התוצאה הנוכחית גדולה מהשיא של השחקן
                                    updateBestScoreClassic();
                                }

                                bestLevelClassic =  Objects.requireNonNull(user).getBestLevelClassic();
                                if(currentLevel > bestLevelClassic){//בודק האם הרמה הנוכחית גדולה מהשיא של הרמה אליו הגיע השחקן
                                    updateBestLevelClassic();
                                }
                                break;
                            case "chaos":

                                bestScoreChaos = Objects.requireNonNull(user).getBestScoreChaos();//בודק האם המשתנה שווה null ובמידה שכן זורק NullPointerException, כלומר שגיאה שהמשתנה שווה ל null
                                if(currentScore > bestScoreChaos){//בודק האם התוצאה הנוכחית גדולה מהשיא של השחקן
                                    updateBestScoreChaos();
                                }

                                bestLevelChaos =  Objects.requireNonNull(user).getBestLevelChaos();
                                if(currentLevel > bestLevelChaos){//בודק האם הרמה הנוכחית גדולה מהשיא של הרמה אליו הגיע השחקן
                                    updateBestLevelChaos();
                                }
                                break;
                        }

                        progressDialog.dismiss();

                        switch (check){
                            case LEVEL_PASSED:
                                openLevelDialog(level, levelGoal, isFinish);
                                break;
                            case GAME_OVER:
                                openLoseDialog();
                                break;
                            case EXIT_LEVEL:
                                Intent intent = new Intent(GameActivity.this, UserActivity.class);
                                startActivity(intent);
                                break;
                        }


                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }


    //מעדכן את השיא של השחקן במוד "classic"
    public void updateBestScoreClassic(){
        //משנה את הערך של השיא באובייקט user הנוכחי ומעדכן את DocumentReference עם האובייקט המעודכן
        user.setBestScoreClassic(currentScore);
        docRef.set(user);
    }

    //מעדכן את השיא של השחקן במוד "chaos"
    public void updateBestScoreChaos(){
        //משנה את הערך של השיא באובייקט user הנוכחי ומעדכן את DocumentReference עם האובייקט המעודכן
        user.setBestScoreChaos(currentScore);
        docRef.set(user);
    }

    //מעדכן את השיא של הרמה (Level) אליו הגיע השחקן במוד "classic"
    public void updateBestLevelClassic(){
        //משנה את הערך של השיא באובייקט user הנוכחי ומעדכן את DocumentReference עם האובייקט המעודכן
        user.setBestLevelClassic(currentLevel);
        docRef.set(user);
    }

    // מעדכן את השיא של הרמה (Level) אליו הגיע השחקן במוד "chaos"
    public void updateBestLevelChaos(){
        //משנה את הערך של השיא באובייקט user הנוכחי ומעדכן את DocumentReference עם האובייקט המעודכן
        user.setBestLevelChaos(currentLevel);
        docRef.set(user);
    }

    //הפונקציה מתחילה לרוץ לאחר שהשחקן לחץ על אחד מכפתורי המשחק
    @Override
    public void onClick(View v) {
        if(v.equals(btnGreen)) {
            clickGreen();
            gameOver = !allColors.get(count).equals(COLOR.GREEN);//בודק האם הצבע שנמצא במקום count במערך allColors מתאים לצבע של הלחצן שהמשתמש לחץ
            count++;// מוסיף 1 למספר הלחצנים שנלחצו על ידי השחקן - count
        }

        else if(v.equals(btnRed)) {
            clickRed();
            gameOver = !allColors.get(count).equals(COLOR.RED);//בודק האם הצבע שנמצא במקום count במערך allColors מתאים לצבע של הלחצן שהמשתמש לחץ
            count++;// מוסיף 1 למספר הלחצנים שנלחצו על ידי השחקן - count
        }

        else if(v.equals(btnBlue)) {
            clickBlue();
            gameOver = !allColors.get(count).equals(COLOR.BLUE);//בודק האם הצבע שנמצא במקום count במערך allColors מתאים לצבע של הלחצן שהמשתמש לחץ
            count++;// מוסיף 1 למספר הלחצנים שנלחצו על ידי השחקן - count
        }

        else if(v.equals(btnYellow)) {
            clickYellow();
            gameOver = !allColors.get(count).equals(COLOR.YELLOW);//בודק האם הצבע שנמצא במקום count במערך allColors מתאים לצבע של הלחצן שהמשתמש לחץ
            count++;// מוסיף 1 למספר הלחצנים שנלחצו על ידי השחקן - count
        }

        isGameOver();
    }

    // בודק האם השחקן יכול להמשיך לשחק, או שהוא נפסל והמשחק נגמר.
    public void isGameOver(){
        if (gameOver) {
            lostGameSound.start();
            vibrate();
            score(CHECK.GAME_OVER, "","",false);
        } else {
            if (count == allColors.size()) { //בודק האם המשתמש חזר על כל הרצף של הצבעים
                currentScore++;//מעלה בנקודה אחת את הניקוד הנוכחי של השחקן
                tvScore.setText(String.valueOf(currentScore));
                disableButtons();

                isLevelUp();
            }
        }
    }

    public void runGame(){
        //מריץ את פונקצית ה game() לאחר 1.2 שניות
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                game();
            }
        }, 1200);//1000 = 1 second
    }

    public void isLevelUp(){

        if(currentScore == LEVEL1 && currentLevel == 1){
            currentLevel = 2;
            score(CHECK.LEVEL_PASSED,"level 1 completed!", "Reach 20 points to complete the next level!", false);
            vibrate();
        }
        else if(currentScore == LEVEL2 && currentLevel == 2){
            currentLevel = 3;
            score(CHECK.LEVEL_PASSED,"Level 2 completed!", "Reach 30 points to complete the next level!", false);
            vibrate();
        }
        else if(currentScore == LEVEL3 && currentLevel == 3){
            currentLevel = 4;
            score(CHECK.LEVEL_PASSED,"Level 3 completed!", "Game Over!", true);
            vibrate();
        }
        else{
            runGame();
        }
    }

    public void resetScore(){
        currentScore = 0;
        allColors.clear();
        tvScore.setText(String.valueOf(currentScore));
    }

    //Vibration - רטט
    public void vibrate(){
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 400 milliseconds
        v.vibrate(400);
    }

    //דיאלוג שמוצג כדי להראות שה  (ממשק משתמש)(user interface)UI לא נתקע אלא טוען נתונים מהFirebase
    public void progressBarDialog(){
        progressDialog = new ProgressDialog(GameActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Score"); // Setting Message
        progressDialog.setMessage("Loading..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
    }


    public void openLevelDialog(String level, String levelGoal, boolean isFinish){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.level_layout_dialog);//משנה את הרקע של הדיאלוג
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button btnNextLevel = dialog.findViewById(R.id.btnNextLevel);
        btnNextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetScore();

                if(currentLevel == 4){
                    Toast.makeText(GameActivity.this, "Level: Done", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(GameActivity.this, "Level: " + currentLevel, Toast.LENGTH_SHORT).show();

                }

                myHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(GameActivity.this, "Watch and Repeat", Toast.LENGTH_SHORT).show();
                    }
                }, 1200 );

                game();
                dialog.dismiss();
            }
        });
        Button btnMainMenuLevel = dialog.findViewById(R.id.btnMainMenuLevel);
        btnMainMenuLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               score(CHECK.EXIT_LEVEL,"","",false);
            }
        });

        Button btnShareLevel = dialog.findViewById(R.id.btnShareLevel);
        btnShareLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Player: " + fullName + " \nScored: " + currentScore + " points! \nMode: " + gameMode;
                if(currentLevel == 4){
                    shareBody +=  "\nLevel: Done" + "\n\nTry to beat their score :)";
                }else{
                    shareBody +=  "\nLevel: " + currentLevel + "\n\nTry to beat their score :)";
                }                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AmitFinal");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        TextView tvLevel = (TextView) dialog.findViewById(R.id.tvLevel1);
        tvLevel.setText(level);

        TextView tvLevelGoal = (TextView) dialog.findViewById(R.id.tvLevelGoal);
        tvLevelGoal.setText(levelGoal);

        if(isFinish){
            btnNextLevel.setText("CONTINUE");
        }

        dialog.show();
    }

    // דיאלוג שמוצג לאחר שהשחקן נפסל במשחק.
    public void openLoseDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lose_layout_dialog);//משנה את הרקע של הדיאלוג
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvScoreLose = (TextView) dialog.findViewById(R.id.tvScoreLose);
        TextView tvBestScoreLose = (TextView) dialog.findViewById(R.id.tvBestScoreLose);


        tvScoreLose.setText("Score: " + currentScore);
        switch (gameMode) {
            case "classic":
                tvBestScoreLose.setText("Best Score: " + bestScoreClassic);
                break;
            case "chaos":
                tvBestScoreLose.setText("Best Score: " + bestScoreChaos);
                break;
        }

        Button btnPlayAgainLose = dialog.findViewById(R.id.btnPlayAgainLose);
        btnPlayAgainLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                intent.putExtra("gameMode",gameMode);
                intent.putExtra("level",currentLevel);
                startActivity(intent);
            }
        });
        Button btnMainMenuLose = dialog.findViewById(R.id.btnMainMenuLose);
        btnMainMenuLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        Button btnShareLose = dialog.findViewById(R.id.btnShareLose);
        btnShareLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Player: " + fullName + " \nScored: " + currentScore + " points! \nMode: " + gameMode;
                if(currentLevel == 4){
                    shareBody +=  "\nLevel: Done" + "\n\nTry to beat their score :)";
                }else{
                    shareBody +=  "\nLevel: " + currentLevel + "\n\nTry to beat their score :)";
                }

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AmitFinal");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        dialog.show();
    }
}