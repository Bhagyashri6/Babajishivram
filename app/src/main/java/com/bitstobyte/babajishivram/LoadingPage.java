package com.bitstobyte.babajishivram;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bitstobyte.babajishivram.model.entities.LoginEntity;
import com.bitstobyte.babajishivram.model.sql.UserDBMethods;
import com.bitstobyte.babajishivram.view.CustomerEnd.CustomerDashboard;
import com.bitstobyte.babajishivram.view.Dashboard;
import com.bitstobyte.babajishivram.view.utils.staticUtilsMethods;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aradh on 25-12-2017.
 */

public class LoadingPage extends Activity {
    Context context;

    SharedPreferences myPrefs;
    String newVersion, oldVersion;
    int timedifference;
    Date date1;
    ArrayList<LoginEntity> loginentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingpage);

        context = this;

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(10);
                } catch (Exception e) {
                    System.out.println("inside exception for thread "
                            + e.toString());
                } finally {
                    checkVersion();
                    checkLogin();
                }
            }
        };
        timer.start();

    }

    private void checkLogin() {
        try {
            int resu = 0;
            String lotime = null;

            UserDBMethods db = new UserDBMethods(getApplicationContext());
            resu = db.GetUserCode();
            lotime = db.GetTime();
            loginentity = db.SelectOfflineData();
            staticUtilsMethods.SysOutPrint(lotime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datea = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            if (lotime == null){
                Intent n = new Intent(getApplicationContext(), MainActivity.class);
                n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(n);
                finish();
            }else {
                date1 = simpleDateFormat.parse(lotime);
            }

            Date date2 = simpleDateFormat.parse(datea);
            ChackLoginTime(date1, date2);
            //Checking Login Id
            if (resu == 0) {
                Intent n = new Intent(getApplicationContext(), MainActivity.class);
                n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(n);
                finish();
            //Checking Timr Difference of Days greater than 0
            }else if (timedifference > 0) {
                new MaterialStyledDialog.Builder(this)
                        .withDialogAnimation(true)
                        .setIcon(R.drawable.mainbs)
                        .setHeaderColor(R.color.appblue)
                        .setTitle("Message!")
                        .setCancelable(false)
                        .setDescription("Login Session Has Expired! " +
                                "Please Log in Again To Continue")
                        .setPositiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                                dialog.dismiss();

                            }
                        })
                        .show();

                Intent n = new Intent(getApplicationContext(), MainActivity.class);
                n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(n);
                finish();
            } else {
               /* Intent n = new Intent(getApplicationContext(), Dashboard.class);
                n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(n);
                finish();*/
               if (loginentity.get(0).lType.equals("1")){
                   Intent n = new Intent(getApplicationContext(), Dashboard.class);
                   n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(n);
               }else if (loginentity.get(0).lType.equals("2")) {
                   Intent n = new Intent(getApplicationContext(), CustomerDashboard.class);
                   n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(n);
               }
                finish();
            }
    }
        catch (Exception e) {
            staticUtilsMethods.LogIt("SplashScreen-NextUi"
                    + staticUtilsMethods.getStackTrace(e));
        }


    }
    public void ChackLoginTime(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
       timedifference = Integer.parseInt(String.valueOf(elapsedDays));
       System.out.print(timedifference);
        System.out.print(timedifference);
    }




    // to get the version from server
    private void checkVersion() {
        // TODO Auto-generated method stub
        try {
            System.out.println("inside check version");
            new FetchAppVersionFromGooglePlayStore().execute();
        } catch (Exception e) {

            System.out.println("inside error for check version" + e.toString());
        }
    }

    class FetchAppVersionFromGooglePlayStore extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            try {
                return Jsoup.connect("https://play.google.com/store/apps/details?" +
                        "id=com.bitstobyte.babajishivram"
                        + "&hl=en")
                        .timeout(10000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

            } catch (Exception e) {

                System.out.println("inside error for async task " + e.toString());
                return "";
            }
        }


        protected void onPostExecute(String string) {
            try {
                newVersion = string;

                oldVersion = getAppVersion();

                System.out.println("got the old version as " + oldVersion + "\n the new version as " + newVersion);

                if (!newVersion.isEmpty() && !oldVersion.isEmpty() && !newVersion.equals("")) {
                    if (!newVersion.equals(oldVersion)) {
                        //open pop-up here for new version available

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle("Update");

                        System.out.println("done adding title");

                        // set dialog message
                        alertDialogBuilder.setMessage("New update is available!\n Update Now!");

                        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.bitstobyte.babajishivram"));
                                startActivity(intent);
                            }
                        });

                        // set dialog message
                        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                checkPermissions();
                            }
                        });
                        System.out.println("done adding text and setting button");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else {
                        checkPermissions();
                    }
                } else {
                    checkPermissions();
                }
            }catch (Exception e){
                System.out.println("inside error for on post execute "+e.toString());
            }
        }
    }



    void checkPermissions() {

        System.out.println("inside check permissions");

        int permissionCheck = 1;


        String permissions[] = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INSTALL_SHORTCUT,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        };

        if (!hasPermissions(this, permissions)) {
            System.out.println("inside does not have all permissions" );
            ActivityCompat.requestPermissions(this, permissions, permissionCheck);
        }else{
            System.out.println("inside has all permissions");
            checkLogin();
           /* Intent intent=new Intent(LoadingPage.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        }

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("inside permissions granted");

        Intent intent=new Intent(LoadingPage.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    // to fetch the current app version
    private String getAppVersion() throws PackageManager.NameNotFoundException {
        // TODO Auto-generated method stub

        System.out.println("inside get app version");

        PackageInfo packageInfo = getPackageManager().getPackageInfo(
                getPackageName(), 0);
        return packageInfo.versionName;
    }


}