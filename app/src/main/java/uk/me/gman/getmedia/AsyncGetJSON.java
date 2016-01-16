package uk.me.gman.getmedia;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncGetJSON extends AsyncTask<String, Integer, Long> {
    private static final String TAG = "getJSON";

    public AsyncResponse delegate = null;
    private Context context;
    public AsyncGetJSON( Activity mAct) {
        //this.delegate = mAct;
        context = mAct.getApplicationContext();
    }



    @Override
    protected Long doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(params[0]);
            //url = new URL("http://youtube-dl55.herokuapp.com/api/info?url=https://youtu.be/R_dd7eVGAh0");

            Log.i(TAG, "url: " + url.toString());

            publishProgress(1);
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            if (urlConnection.getResponseCode() == 500){
                publishProgress(0xFF);
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONObject json = new JSONObject(convertStreamToString(in));

            Log.i(TAG, "out: " + json.toString());

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

            Log.i(TAG, "downloadUrl: " + downloadURL);

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
            DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(r);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

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
        if( result != null ) {
            Log.i(TAG, "Downloaded " + result + " bytes");
        }

        delegate.processFinish();
    }

    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            Toast.makeText(context, "Getting Video URL", Toast.LENGTH_LONG).show();
        } else if (progress[0] == 2) {
            Toast.makeText(context, "Starting Download", Toast.LENGTH_LONG).show();
        } else if (progress[0] == 0xFF) {
            Toast.makeText(context, "Unsupported site?" , Toast.LENGTH_LONG).show();
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}