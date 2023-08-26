package com.bvt.encodezip;

import static android.provider.Telephony.Carriers.PASSWORD;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bvt.encodezip.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.view.Menu;
import android.view.MenuItem;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;



import java.io.File;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {// 6.0
            String[] perms = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android. Manifest.permission.READ_PHONE_STATE};
            for (String p : perms) {
                int f = ContextCompat.checkSelfPermission(MainActivity.this, p);
                Log.d("---", String.format("%s - %d", p, f));
                if (f != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(perms, 0XCF);
                    break;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// android 11  且 不是已经被拒绝
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            }
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void excise() {
        ZipFile zipFile = new ZipFile("tom.zip", "PASSWORD".toCharArray());
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        zipFile.setRunInThread(true);
        try {
            zipFile.addFolder(new File("/some/folder"));


            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                System.out.println("Percentage done: " + progressMonitor.getPercentDone());
                System.out.println("Current file: " + progressMonitor.getFileName());
                System.out.println("Current task: " + progressMonitor.getCurrentTask());


                    Thread.sleep(100);

            }
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (progressMonitor.getResult().equals(ProgressMonitor.Result.SUCCESS)) {
            System.out.println("Successfully added folder to zip");
        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.ERROR)) {
            System.out.println("Error occurred. Error message: " + progressMonitor.getException().getMessage());
        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.CANCELLED)) {
            System.out.println("Task cancelled");
        }
    }

    public void addFile() {
        File myDoc = new File("");
        ZipOutputStreamExample zipOutput = new ZipOutputStreamExample();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}