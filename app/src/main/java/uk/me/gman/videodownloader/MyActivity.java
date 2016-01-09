package uk.me.gman.videodownloader;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_PERMISSIONS);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d("MYActivity", "action: " + action);
                Log.d("MYActivity", "type: " + type);
                Log.d("MYActivity", "sharedText: " + sharedText);

                //if( sharedText.startsWith("https://youtu.be/") )
                {
                    Log.d("MYActivity", "Boom!");
                    String url = "http://youtube-dl55.herokuapp.com/api/info?url=" + sharedText;
                    new getJSON().execute(url);

                }

            }
        }

    }


    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private class getJSON extends AsyncTask<String, Integer, Long> {
        @Override
        protected Long doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(params[0]);
                //url = new URL("http://youtube-dl55.herokuapp.com/api/info?url=https://youtu.be/R_dd7eVGAh0");

                Log.i("MYActivity", "url: " + url.toString());

                publishProgress(1);
                urlConnection = (HttpURLConnection) url
                        .openConnection();


                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                JSONObject json = new JSONObject(convertStreamToString(in));

                Log.i("MYActivity", "out: " + json.toString());


                String downloadURL = json.getJSONObject("info").get("url").toString();
                String fileName = json.getJSONObject("info").get("title").toString() + "." + json.getJSONObject("info").get("ext").toString();

                /*
                //DO we care? Just download teh default file
                JSONArray jsonT = json.getJSONObject("info").getJSONArray("formats");
                int id = findIdToDownload(jsonT, "mp4", 720);
                if(id != -1) {
                    downloadURL = json.getJSONObject("info").getJSONArray("formats").getJSONObject(id).get("url").toString();
                }
                */

                Log.i("MYActivity", "downloadUrl: " + downloadURL);

                publishProgress(2);

                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(downloadURL));

                // This put the download in the same Download dir the browser uses
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName );

                // When downloading music and videos they will be listed in the player
                // (Seems to be available since Honeycomb only)
                r.allowScanningByMediaScanner();

                // Notify user when download is completed
                // (Seems to be available since Honeycomb only)
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                // Start download
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(r);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            System.exit(0);
            return null;
        }

        private int findIdToDownload(JSONArray json,String ext, int height ) {

            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject row = json.getJSONObject(i);
                    if (row.getInt("height") == height && row.getString("ext").equals(ext)) {
                        return i;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return -1;
        }

        protected void onPostExecute(Long result) {
            Log.i("MYActivity", "Downloaded " + result + " bytes");
        }

        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == 1) {
                Toast.makeText(MyActivity.this, "Getting Video URL", Toast.LENGTH_SHORT).show();
            } else if (progress[0] == 2) {
                Toast.makeText(MyActivity.this, "Starting Download", Toast.LENGTH_LONG).show();
            }
        }

    }


}

