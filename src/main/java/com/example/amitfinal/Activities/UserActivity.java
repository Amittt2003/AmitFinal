package com.example.amitfinal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.amitfinal.Models.InternetConnection;
import com.example.amitfinal.Models.NotificationReceiver;
import com.example.amitfinal.Models.SharedPreferencesRememberMe;
import com.example.amitfinal.Models.User;
import com.example.amitfinal.Models.UserScore;
import com.example.amitfinal.Models.UserScores;
import com.example.amitfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.sql.Time;
import java.util.Calendar;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    Button btnGreen, btnRed, btnYellow, btnBlue;
    ImageButton btnPlay, btnLogout, btnScoreUser;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    TextView tvUserName;

    ProgressBar progressBarUserName;
    ProgressDialog progressDialog;

    String userId, fullName, gameMode;
    int hour, minute, level;

    boolean btnScoreClicked = false;

    StringBuilder msg = new StringBuilder();

    //יוצר אובייקט חדש מסוג UserScores
    UserScores userScores = new UserScores();;

    RelativeLayout UserActivityLayout;

    AlertDialog dialog;

    MediaPlayer greenSound, redSound, blueSound, yellowSound;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //בודק האם למשתמש יש חיבור לאינטרנט
        if (!new InternetConnection(UserActivity.this).isConnected()){
            showConnectionDialog();
        }

        UserActivityLayout =  findViewById(R.id.UserActivityLayout);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down);

        btnPlay = findViewById(R.id.btnPlay);
        btnLogout = findViewById(R.id.btnLogout);
        btnScoreUser = findViewById(R.id.btnScoreUser);

        btnGreen = findViewById(R.id.btnGreen);
        btnRed = findViewById(R.id.btnRed);
        btnBlue = findViewById(R.id.btnBlue);
        btnYellow = findViewById(R.id.btnYellow);

        greenSound = MediaPlayer.create(this, R.raw.sound1);
        redSound = MediaPlayer.create(this, R.raw.sound2);
        blueSound = MediaPlayer.create(this, R.raw.sound3);
        yellowSound = MediaPlayer.create(this, R.raw.sound4);

        fStore = FirebaseFirestore.getInstance();//firebase - Firestore
        fAuth = FirebaseAuth.getInstance();//firebase - Authentication

        tvUserName = findViewById(R.id.tvUserName);
        progressBarUserName = findViewById(R.id.progressBarUserName);

        showName();

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGreen.startAnimation(animation);
                greenSound.start();            }
        });

        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRed.startAnimation(animation);
                redSound.start();            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlue.startAnimation(animation);
                blueSound.start();            }
        });

        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYellow.startAnimation(animation);
                yellowSound.start();            }
        });

        btnScoreUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreTable();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_mode_dialog();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
    }

    public void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //יצירת אובייקט מסוג SharedPreferencesRememberMe והרצת פעולת הבנאי של האובייקט
                SharedPreferencesRememberMe sharedPreferencesRememberMe = new SharedPreferencesRememberMe(UserActivity.this);

                //מעדכן את ערך ה Key - rememberMe ל false, במטרה לאפשר למשתמש לחזור למסך הבית(MainActivity)
                sharedPreferencesRememberMe.edit(false);

                //מנתק את המשתמש הנוכחי מהמערכת
                FirebaseAuth.getInstance().signOut();//logout

                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateFade(UserActivity.this);
                finish();
            }
        });

        builder.setNeutralButton("No", null);//בלחיצת כפתור סוגר את הדיאלוג

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    public void showScoreTable(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose Game Mode");
        builder.setTitle("Top 10 Players");

        builder.setPositiveButton("Classic", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        builder.setNeutralButton("My Score", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        builder.setNegativeButton("Chaos", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gameMode = "classic";
                dialog.setTitle("Top 10 Players   Mode: Classic");
                btnScoreClicked = true;
                createScoreTable();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gameMode = "chaos";
                dialog.setTitle("Top 10 Player   Mode: Chaos");
                btnScoreClicked = true;
                createScoreTable();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnScoreClicked = false;
                dialog.setTitle(fullName + "'s Score");
                createScoreTable();
            }
        });
    }

    //יוצר את טבלת השיאים
    public void createScoreTable(){
        progressBarDialog();//מציג דיאלוג טעינה

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

                        UserScore userScore = new UserScore(name, bestScoreClassic, bestScoreChaos, bestLevelClassic, bestLevelChaos);
                        userScores.add(userScore);
                    }

                    //ממיין את המערך bestScoreClassicArray מהערך (score) הגדול לקטן
                    userScores.sort();

                    //אם btnScoredClicked הוא true אז מוצג למשתמש את טבלת השיאים של כל המשתמשים
                    if(btnScoreClicked){
                        Top10();
                        dialog.setMessage(msg);
                        msg.setLength(0);//מאפס את תוכן טבלת השיאים(msg)
                        progressDialog.dismiss();//מבטל את דיאולוג הטעינה
                        btnScoreClicked = false;
                    }
                    // במידה ולא, אז מוצג למשתמש רק את טבלת השיאים של עצמו
                    else{
                        showScore();
                    }
                }
            }
        });
    }

    //יוצר את התוכן של טבלת השיאים
    public void Top10() {

        if (gameMode == null) {
            msg.append("");
        }else{
            switch (gameMode) {

                case "classic":

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

                case "chaos":

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

    // מחזיר באיזה מקום (במוד "classic") נמצא המשתמש המחובר כרגע למערכת
    public int getPlaceClassic() {
        int index = 0;

        for (int i = 0; i < userScores.getSizeClassicArray(); i++) {
            if(userScores.getNameByIndexClassic(i).equals(fullName)){
                index = i;
                break;
            }
        }
       return index + 1;
    }

    // מחזיר באיזה מקום (במוד "chaos") נמצא המשתמש המחובר כרגע למערכת
    public int getPlaceChaos() {
        int index = 0;

        for (int i = 0; i < userScores.getSizeChaosArray(); i++) {
            if(userScores.getNameByIndexChaos(i).equals(fullName)){
                index = i;
                break;
            }
        }
        return index + 1;
    }

    //מראה את השיא האישי של השחקן בכל מוד של משחק
    public void showScore(){
        if(fAuth.getCurrentUser() != null){//בודק האם יש משתמש שמחובר עכשיו למערכת

            //ה ID של המשתמש המחובר למערכת
            userId = fAuth.getCurrentUser().getUid();

            // מקבל את הdocument של המשתמש (לפי ה ID של המשתמש) המחובר כרגע למערכת
            DocumentReference docRef = fStore.collection("Users").document(userId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document != null) {

                            //מקבל מהdocument את האובייקט של User
                            User user = document.toObject(User.class);

                            int bestScoreClassic = Objects.requireNonNull(user).getBestScoreClassic();//בודק האם המשתנה שווה null ובמידה שכן זורק NullPointerException, כלומר שגיאה שהמשתנה שווה ל null
                            int bestScoreChaos = Objects.requireNonNull(user).getBestScoreChaos();//בודק האם המשתנה שווה null ובמידה שכן זורק NullPointerException, כלומר שגיאה שהמשתנה שווה ל null

                            int bestLevelClassic = user.getBestLevelClassic();
                            int bestLevelChaos = user.getBestLevelChaos();

                            msg.append("𝑴𝒐𝒅𝒆: 𝑪𝒍𝒂𝒔𝒔𝒊𝒄 \n").append("Best Score: ").append(bestScoreClassic).append("\n");
                            msg.append("Place: ").append(getPlaceClassic()).append("\n");

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(bestLevelClassic == 4){
                                msg.append("Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("Level: ").append(bestLevelClassic).append("\n \n");
                            }

                            msg.append("𝑴𝒐𝒅𝒆: 𝑪𝒉𝒂𝒐𝒔 \n").append("Best Score: ").append(bestScoreChaos).append("\n");
                            msg.append("Place: ").append(getPlaceChaos()).append("\n");

                            // במידה והמשתמש סיים את כל השלבים יופיע לו "Done" בLevel
                            if(bestLevelChaos == 4){
                                msg.append("Level: ").append("Done").append("\n \n");
                            }else{
                                msg.append("Level: ").append(bestLevelChaos).append("\n \n");
                            }

                            dialog.setMessage(msg);
                            msg.setLength(0);
                            progressDialog.dismiss();

                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    //מציג את שם המשתמש המחובר כרגע למערכת
    public void showName(){
        progressBarUserName.setVisibility(View.VISIBLE);
        if(fAuth.getCurrentUser() != null){//בודק האם יש משתמש שמחובר עכשיו למערכת

            //ה ID של המשתמש המחובר למערכת
            userId = fAuth.getCurrentUser().getUid();

            // מקבל את הdocument של המשתמש (לפי ה ID של המשתמש) המחובר כרגע למערכת
            DocumentReference docRef = fStore.collection("Users").document(userId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            //מקבל מהdocument את האובייקט של User
                            User user = document.toObject(User.class);

                            fullName = Objects.requireNonNull(user).getName();
                            tvUserName.setText("Welcome, " + fullName);
                            progressBarUserName.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    public void getLevel(){
        progressBarDialog();
        if(fAuth.getCurrentUser() != null){//בודק האם יש משתמש שמחובר עכשיו למערכת

            //ה ID של המשתמש המחובר למערכת
            userId = fAuth.getCurrentUser().getUid();

            // מקבל את הdocument של המשתמש (לפי ה ID של המשתמש) המחובר כרגע למערכת
            DocumentReference docRef = fStore.collection("Users").document(userId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            //מקבל מהdocument את האובייקט של User
                            User user = document.toObject(User.class);

                            switch (gameMode){
                                case "classic":
                                    level = Objects.requireNonNull(user).getBestLevelClassic();
                                    break;

                                case "chaos":
                                    level = Objects.requireNonNull(user).getBestLevelChaos();
                                    break;
                            }

                            openInstructionDialog();
                            progressDialog.dismiss();

                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    //מציג למשתמש את השעון ונותן לו לבחור שעה
    public void getTimeFromUser(){
        int currentTimeHour = new Time(System.currentTimeMillis()).getHours();
        int currentTimeMinute = new Time(System.currentTimeMillis()).getMinutes();

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
                setNotification();
                Toast.makeText(UserActivity.this, "A reminder has been set for hour: " + hour + " : " + minute, Toast.LENGTH_SHORT).show();
            }
        }, currentTimeHour, currentTimeMinute, true);

        dialog.show();
    }

    public void setNotification(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifyAmitFinal", "AmitFinal", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("NotificationChannel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext() , NotificationReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);  //set repeating every 24 hours
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.how_to_play) {
            how_to_play_dialog();
        }

        if (item.getItemId() == R.id.alarm) {
            showAlarmDialog();
        }
        return true;
    }

    public void how_to_play_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("How To Play");
        String message = "The principle of the game is simple: the player have to memorize the series of illuminated keys and reproduce it. The purpose of the game is to reproduce the longest series of colors / sounds randomly generated by the Simon.\n" +
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

    // דיאלוג זה מאפשר למשתמש לבחור באיזה מוד של משחק הוא מעוניין לשחק
    public void game_mode_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("Game Mode");
        builder.setMessage("Choose The Game Mode You Want To Play");

        builder.setPositiveButton("Classic", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gameMode = "classic";
                getLevel();
                //openInstructionDialog();
            }
        });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        builder.setNeutralButton("Chaos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gameMode = "chaos";
                getLevel();
            }
        });

        // יוצר את ה AlertDialog ומראה אותו למשתמש
        builder.create();
        builder.show();
    }

    //דיאלוג שמוצג כדי להראות שה  (ממשק משתמש)(user interface)UI לא נתקע אלא טוען נתונים מהFirebase
    public void progressBarDialog(){
        progressDialog = new ProgressDialog(UserActivity.this);
        progressDialog.setTitle("Loading Score Table");// Setting Message
        progressDialog.setMessage("Loading...");// Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
    }

    //דיאלוג זה מופיע במידה ולמשתמש אין חיבור לאינטרנט
    public void showConnectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to the internet to proceed further");
        builder.setCancelable(false);

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//מעביר את המשתמש להגדרות הWIFI במכשיר שלו
            }
        });

        // יוצר את ה AlertDialog ומראה אותו למשתמש
        builder.create();
        builder.show();
    }

    // מציג דיאלוג שמאפשר למשתמש לבחור האם לקבוע לו התראה שתזכיר לו לחזור לשחק במשחק בכדי לשפר את הזיכרון שלו
    public void showAlarmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("Set A Reminder");
        builder.setMessage("Feel free to choose any time in the day, in which we will send you a notification that will remind you to get back on the game to improve your memory :)");
        builder.setCancelable(true);

        builder.setPositiveButton("Pick A Time", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getTimeFromUser();
            }
        });

        builder.setNeutralButton("Maybe later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // יוצר את ה AlertDialog ומראה אותו למשתמש
        builder.create();
        builder.show();
    }

    public void openInstructionDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.instruction_layout_dialog);//משנה את הרקע של הדיאלוג
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView tv2 = (TextView) dialog.findViewById(R.id.tv2);
        tv2.setText("Good Luck, " + fullName);


        Button btnPlay = dialog.findViewById(R.id.btnInstructionPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(UserActivity.this, SplashScreenGameActivity.class);
                switch (gameMode){
                    case "classic":
                        intent.putExtra("gameMode","classic");
                        intent.putExtra("fullName",fullName);
                        intent.putExtra("level",level);
                        startActivity(intent);
                        Animatoo.animateFade(UserActivity.this);//אנימציה למעבר בין activities
                        break;

                    case "chaos":
                        intent.putExtra("gameMode","chaos");
                        intent.putExtra("fullName",fullName);
                        intent.putExtra("level",level);
                        startActivity(intent);
                        Animatoo.animateFade(UserActivity.this);//אנימציה למעבר בין activities
                        break;
                }
            }
        });

        dialog.show();
    }


}