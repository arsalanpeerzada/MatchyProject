package com.techwitz.matchymatch.Utils;

import android.app.Service;

/**
 * Created by HP on 10/9/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.techwitz.matchymatch.R;

public class AlarmSoundService extends Service {

    private MediaPlayer mediaPlayer;
    int position = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences myPosition = this.getSharedPreferences("MyAwesomePosition", Context.MODE_PRIVATE);
        position = myPosition.getInt("position", 0);
        //Start media player
        mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic);
        mediaPlayer.setVolume(0.09f , 0.09f);
        mediaPlayer.seekTo(position);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //On destory stop and release the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            position = mediaPlayer.getCurrentPosition();

            SharedPreferences myPosition = getSharedPreferences("MyAwesomePosition", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myPosition.edit();
            editor.putInt("position", position);
            editor.commit();
            /*mediaPlayer.reset();*/
            mediaPlayer.release();
        }
    }



}