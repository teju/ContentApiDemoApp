package demo.com.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import demo.com.R;


public class Loader {
    private static Dialog AlertDialog;;
    public static void show(Context context) {
        Loader.show(context, "");
    }

    public static void show(final Context context, final String text) {
        try {
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (Loader.AlertDialog != null) {
                        Loader.AlertDialog.dismiss();
                        //Loader.AlertDialog = null;
                    }

                    Loader.AlertDialog = new Dialog(context);
                    Loader.AlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Loader.AlertDialog.setCancelable(false);
                    Loader.AlertDialog.setContentView(R.layout.view_loader_dialog);

                    TextView vw_text = (TextView) Loader.AlertDialog.findViewById(R.id.vw_text);
                    if (vw_text != null) {
                        if (text != null && text.trim().length() > 0) {
                            vw_text.setText(text);
                        }
                    }
                    Loader.AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    Loader.AlertDialog.show();
                    Loader.AlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
            });
        } catch (Exception e){

        }
    }

    public static void hide() {
        try {
            if (Loader.AlertDialog != null && Loader.AlertDialog.isShowing() ) {
                new Handler(Loader.AlertDialog.getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Loader.AlertDialog.dismiss();
                            //Loader.AlertDialog = null;
                        } catch (Exception e){

                        }
                    }
                });
            }
        } catch (Exception e){

        }
    }
}
