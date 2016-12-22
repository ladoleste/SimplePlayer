package br.com.ladoleste.simpleplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anderson Silva on 22/12/2016.
 */
public class MainService extends Service {
//    private static final String ACTION_PLAY = "com.example.action.PLAY";
//    private static final String TAG = "MainActivity";

    MediaPlayer mediaPlayer = null;
    private List<String> files = new ArrayList<>();

    protected BroadcastReceiver playerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("STOP")) {
                stopSelf();
            } else if (intent.getAction().equals("NEXT")) {
                mediaPlayer.stop();
                play();
            }
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {

        prepareList();

        play();

        return Service.START_STICKY;
    }

    void play() {

        try {
            Uri myUri = Uri.parse(files.remove(0));


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, myUri);
            String artista = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            String musica = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            Toast.makeText(this, String.format("%s - %s", artista, musica), Toast.LENGTH_SHORT).show();

            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(120000);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play();
                }
            });
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        registerReceiver(playerReceiver, new IntentFilter("STOP"));
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, new Intent("STOP"), PendingIntent.FLAG_UPDATE_CURRENT);

        registerReceiver(playerReceiver, new IntentFilter("NEXT"));
        PendingIntent contentIntent2 = PendingIntent.getBroadcast(this, 0, new Intent("NEXT"), PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent thisIntent = new Intent(this, MainService.class);
//        thisIntent.putExtra("startId", startId);
//        PendingIntent thisService = PendingIntent.getService(this, 0, thisIntent, 0);

        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "Stop", contentIntent).build();

        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_add, "Next", contentIntent2).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Simple Player")
                .setContentText("by Lado Leste Corporation")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("ticker text")
                .addAction(action1)
                .addAction(action2)
                .build();

        startForeground(36, notification);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        unregisterReceiver(playerReceiver);
        playerReceiver = null;
        super.onDestroy();
    }

    void addFiles(File[] _files) {
        for (File file : _files) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(".mp3")) {
                    files.add(file.getAbsolutePath());
                }
            } else {
                addFiles(file.listFiles());
            }
        }
    }

    private void prepareList() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Music";
        addFiles(new File(path).listFiles());
        Collections.shuffle(files);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}