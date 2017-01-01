package br.com.ladoleste.simpleplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.ladoleste.simpleplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        List<String> songs = new ArrayList<>(Arrays.asList("teste", "teste", "teste", "teste"));
        binding.rcSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.rcSongs.setAdapter(new SongsAdapter(this, songs, android.R.layout.simple_list_item_1));

        intent = new Intent(this, MainService.class);

        binding.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });

        binding.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
            }
        });
    }

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            start();
        }
    }

    private void start() {
        stopService(intent);
        startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            start();
        } else {
            Toast.makeText(this, R.string.sorry, Toast.LENGTH_SHORT).show();
        }
    }
}
