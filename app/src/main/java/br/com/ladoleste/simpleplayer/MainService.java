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
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Criado por Anderson Silva em 22/12/2016.
 */
public class MainService extends Service {
    //    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String TAG = "MainService";
    private static final String ACTION_STOP = "STOP";
    private static final String ACTION_NEXT = "NEXT";

    private static final List<String> SUPPORTED_FILES = Arrays.asList("mp3", "m4a", "wma", "flac", "ogg", "m4p");

    MediaPlayer mediaPlayer = null;
    private List<String> files = new ArrayList<>();

    private BroadcastReceiver playerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            if (intent.getAction().equals(ACTION_STOP)) {
                stopSelf();
            } else if (intent.getAction().equals(ACTION_NEXT)) {
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

            if (files.size() == 0) {
                stopSelf();
                return;
            }

            Uri myUri = Uri.parse(files.remove(0));

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, myUri);
            String artista = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            String musica = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            Toast.makeText(this, String.format("%s - %s", artista, musica), Toast.LENGTH_SHORT).show();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play();
                }
            });
            mediaPlayer.start();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        registerReceiver(playerReceiver, new IntentFilter(ACTION_STOP));
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);

        registerReceiver(playerReceiver, new IntentFilter(ACTION_NEXT));
        PendingIntent contentIntent2 = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action actionStop = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.stop), contentIntent).build();

        NotificationCompat.Action actionNext = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_add, getString(R.string.next), contentIntent2).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Simple Player")
                .setContentText("by Lado Leste Corporation")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("ticker text")
                .addAction(actionStop)
                .addAction(actionNext)
                .build();

        startForeground(36, notification);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (playerReceiver != null) {
            try {
                unregisterReceiver(playerReceiver);
            } catch (IllegalArgumentException ignored) {
            }
            playerReceiver = null;
        }
        super.onDestroy();
    }

    void addFiles(File[] _files) {
        for (File file : _files) {
            if (file.isFile()) {
                if (SUPPORTED_FILES.contains(Util.getExtension(file))) {
                    files.add(file.getAbsolutePath());
                }
            } else {
                addFiles(file.listFiles());
            }
        }
    }

    private void prepareList() {
        String path = Environment.getExternalStorageDirectory().toString();
        addFiles(new File(path).listFiles());
        Collections.shuffle(files);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}