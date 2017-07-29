package eladjarby.bakeit.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;

import eladjarby.bakeit.R;

/**
 * Created by EladJ on 29/07/2017.
 */

public class myProgressDialog {
    private ProgressDialog mProgressDialog;
    private Context context;

    public myProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

    }

    public void showProgressDialog() {
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
