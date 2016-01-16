package uk.me.gman.getmedia;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AboutDialog extends DialogFragment {

    private Context mCont;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String versionCode = "unknown";
        try {
            versionCode = BuildConfig.VERSION_NAME;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle("Version")
                .setMessage(versionCode)
                .setPositiveButton("OK", null)
                .create();
    }
}
