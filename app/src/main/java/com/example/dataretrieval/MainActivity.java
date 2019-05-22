package com.example.dataretrieval;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView textView, textView2, textView3, textView4,textView5, textView6;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv1);
        textView2 = findViewById(R.id.tv2);
        textView3 = findViewById(R.id.tv3);
        textView4 = findViewById(R.id.tv4);
        textView5 = findViewById(R.id.tv5);
        textView6 = findViewById(R.id.tv6);



        //To get complete device details
        String myDeviceModel = Build.MODEL;
        String Device_info = Build.DEVICE;
        String board = Build.BOARD;
        String bootloaer = Build.BOOTLOADER;
        String brand = Build.BRAND;
        String display = Build.DISPLAY;
        String fingerprintenabled = Build.FINGERPRINT;
        String hw = Build.HARDWARE;
        String host = Build.HOST;
        String id5 = Build.ID;
        String manufacturer = Build.MANUFACTURER;
        String product = Build.PRODUCT;
        long time = Build.TIME;
        String user = Build.USER;
        textView5.append("Device Details\n"+myDeviceModel + "\n"+ Device_info+ "\n"+board+ "\n"+bootloaer+ "\n"+brand+ "\n"+display
                + "\n"+fingerprintenabled+ "\n"+hw+ "\n"+host+ "\n"+id5+ "\n"+manufacturer+ "\n"+product+ "\n"+time
                + "\n"+user);

        //To get the battery level
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        textView6.append("Battery :"+String.valueOf(batLevel)+"%");



        //All messages from inbox of mobile
        ListView listView;
        ArrayList smsList;
        listView = (ListView) findViewById(R.id.listmsj);
        smsList = new ArrayList();
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(Uri.parse("content://sms/"), null, null, null, null);
        while (c.moveToNext()) {
            String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
            String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();
            String date = c.getString(c.getColumnIndexOrThrow("date")).toString();
            String type = c.getString(c.getColumnIndexOrThrow("type")).toString();
            String id = c.getString(c.getColumnIndexOrThrow("_id")).toString();


            smsList.add("Number: " + Number + "\n" + type + "\n" + date + "\n" + id + "\n" + "Body: " + Body);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, smsList);
            listView.setAdapter(adapter);
        }
        c.close();


        //Location Receiver in Langitude and Latitude after each 100ms
        Location location;
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        textView2.append("Location");
        onLocationChanged(loc);


        //get a list of call function calling here
        getCallDetails();

        //get a list of installed apps function calling here
        installedApps();

        //get emails logged in
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        textView4.append("Logged In IDs"+"\n");
        for(Account account :accounts)
        {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                textView4.append(possibleEmail+"\n");

            }
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        double lo = location.getLongitude();
        double la = location.getLatitude();
        textView2.append("\nLongitude = " + lo + "\nLatitude = " + la);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //get a list of installed apps.
    public void installedApps() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        textView3.append("Installed Apps Details");
        for (ApplicationInfo packageInfo : packages) {
            textView3.append("Installed package :" + packageInfo.packageName + "\n");
            textView3.append("Source dir : " + packageInfo.sourceDir + "\n");
            textView3.append("Name : " + packageInfo.name + "\n");
            textView3.append("Class Name : " + packageInfo.className + "\n");
            textView3.append("UI Option : " + packageInfo.uiOptions + "\n");
            textView3.append("Process Name : " + packageInfo.processName + "\n" + "\n");

            //textView3.append("Launch Activity :" +  pm.getLaunchIntentForPackage(packageInfo.packageName+"\n"));
        }
    }

    //Call Logs
    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("\n Call Log :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n_________________________________________");
        }
        textView.append(sb);
    }


}