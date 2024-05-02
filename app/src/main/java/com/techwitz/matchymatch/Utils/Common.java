package com.techwitz.matchymatch.Utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

public class Common {
    public static View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    };

    public static boolean checkPermission(Activity activity, String[] permissions, int requestCode, boolean askForPermission) {
        boolean isGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
        }
        if(askForPermission) {
            if (isGranted)
                return true;
            else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return false;
            }
        }
        else
            return isGranted;
    }

}
