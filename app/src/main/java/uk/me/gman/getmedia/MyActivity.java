package uk.me.gman.getmedia;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MyActivity extends AppCompatActivity implements AsyncResponse {

    private static final String TAG = "MyActivity";
    private int numClicks = 0;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        numClicks = settings.getInt("numClicks", 0);

        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);

        }

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(this, "We *need* Permissions to write to storage!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "action: " + action);
                Log.d(TAG, "type: " + type);
                Log.d(TAG, "sharedText: " + sharedText);

                if(numClicks < 5) {
                    if (sharedText.startsWith("https://youtu.be/") || sharedText.toLowerCase().contains("youtube.com")) {
                        Toast.makeText(this, "Unsupported Site?", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime(new Date());               //Set the Calendar to now
                int hour = calendar.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar

                String url = "http://";
                // We're running on a free heroku instance. THey need to sleep for atleast 6 hrs in a day
                // SO lets just run two free instances and swap between them depending on teh time of the day, giving each instance a chance to sleep for 12hrs
                if(hour >= 0 && hour <= 12)
                {
                    url += "youtube-dl55.";
                } else {
                    url += "youtube-dl99.";
                }
                url += "herokuapp.com/api/info?url=" + sharedText;
                AsyncGetJSON asyncTask =new AsyncGetJSON(this);
                asyncTask.delegate = this;
                asyncTask.execute(url);

            }
        }

    }

    @Override
    protected void onStop(){
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("numClicks", numClicks);

        // Commit the edits!
        editor.commit();
    }


    public void showAbout(MenuItem item){
        AboutDialog myDiag = new AboutDialog(this.getApplicationContext());
        myDiag.show(getSupportFragmentManager(), "Diag");
        numClicks++;

        if(numClicks == 5) {
            Toast.makeText(this, "It's not changed :)", Toast.LENGTH_LONG).show();
        }
    }

    public void processFinish(){
        finish();
    }

}

