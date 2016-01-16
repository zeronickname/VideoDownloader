package uk.me.gman.getmedia;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by micro on 16/01/2016.
 */
public class AboutDialog extends DialogFragment {

    private Context mCont;
    public AboutDialog(Context context)
    {
        mCont = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String versionCode = "unknown";
        try {
            versionCode = mCont.getPackageManager().getPackageInfo(mCont.getPackageName(), 0).versionName;
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
