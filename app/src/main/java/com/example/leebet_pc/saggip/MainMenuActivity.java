package com.example.leebet_pc.saggip;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    //Google Maps Variables
    private GoogleMap mMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean initializeLocation = false;
    private double latestLat;
    private double latestLong;
    double longitudeGeo;
    double latitudeGeo;
    private String address = "";


    private AlarmService alarmService;
    private ServiceConnection alarmConnection;
    private Intent alarmIntent;
    private boolean alarmBound = false;

    private final static int SAGIP_NOTIFY_ID = 1;

    //Permission Variables
    private final static int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    //Firebase db variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("message");
    private List<LatLng> list = null;
    private Integer reportReceived = 0;

    //flags
    private boolean SERVICE_ON_DESTROY = true;
    private String SERVICE_TIMER_RUNNING = "";
    private View mapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

        if(checkFirst_Time()){
            Intent passIntent = new Intent(getApplicationContext(), OnboardDetailsActivity.class);
            startActivityForResult(passIntent, 1);
            overridePendingTransition(R.anim.fade_from,R.anim.fade_to);
        }
        //Google Maps
        reportReceived = 0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = mapFragment.getView();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OnboardActivity.class);
                ActivityOptions options = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(
                            MainMenuActivity.this,
                            android.util.Pair.create((View) fab, "bg"));
                }
                startActivity(intent, options.toBundle());
            }
        });
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Turn off silent mode")
                    .setMessage("This app cannot be used while in do not disturb(silent) mode. Please disable silent mode to use the app. Thank you!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SERVICE_ON_DESTROY = false;
                            MainMenuActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{

            setupService();
            startService();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Intent passIntent;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_past_crime_reports:
                     passIntent = new Intent(getApplicationContext(), PastCrimeReportsActivity.class);
                    startActivity(passIntent);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                    return true;
                case R.id.navigation_emergency_message:
                    passIntent = new Intent(getApplicationContext(), EmergencyMessageActivity.class);
                    startActivity(passIntent);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                    return true;
                case R.id.navigation_contacts:
                    passIntent = new Intent(getApplicationContext(), EmergencyContactsActivity.class);
                    passIntent.putExtra("bobo","bobo");
                    startActivity(passIntent);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                    return true;
            }
            return false;


        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if(this.alarmService != null)
            this.alarmService.stopForeground(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.alarmService != null)
            if(!this.alarmService.isTimerRunning())
                this.alarmService.startCheckForAlert();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(this.alarmService != null)
            this.startForegroundNotif("");
    }

    @Override
    protected void onDestroy() {
        if(SERVICE_ON_DESTROY){
            this.alarmService.stopForeground(true);
            this.stopService(this.alarmIntent);
            this.unbindService(this.alarmConnection);
        }
        super.onDestroy();
    }


    public void setupService(){

        this.alarmConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                AlarmBinder binder = (AlarmBinder) iBinder;

                MainMenuActivity.this.alarmService = binder.getAlarmService();

                MainMenuActivity.this.alarmBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                MainMenuActivity.this.alarmBound = false;
            }
        };
    }

    public void startService(){
        if(this.alarmIntent==null){
            this.alarmIntent = new Intent(this, AlarmService.class);
            this.bindService(this.alarmIntent, this.alarmConnection, Context.BIND_AUTO_CREATE);
            this.startService(this.alarmIntent);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            Intent contactsIntent = new Intent(getApplicationContext(), EmergencyContactsActivity.class);
            startActivity(contactsIntent);
        }
        else if (id == R.id.nav_emergency_message) {
            Intent contactsIntent = new Intent(getApplicationContext(), EmergencyMessageActivity.class);
            startActivity(contactsIntent);
        }
        else if (id == R.id.nav_crime_reports) {
            Intent contactsIntent = new Intent(getApplicationContext(), PastCrimeReportsActivity.class);
            startActivity(contactsIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.addHeatMap();

        // Add a marker in Manila and move the camera
        LatLng manila = new LatLng(14.5995, 120.9842);
        //mMap.addMarker(new MarkerOptions().position(manila).title("Manila"));
        mMap.setMinZoomPreference(11.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(manila));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); // two minute interval
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.google_maps_design));
        } catch (Resources.NotFoundException e) {
        }
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        //Heatmap

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //MarkerOptions markerOptions = new MarkerOptions();
                //markerOptions.position(latLng);
                //markerOptions.title("Current Position");
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                if(!initializeLocation) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
                    initializeLocation = true;
                }
            }
        };

    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainMenuActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("PERMISSIONS DECLINED!")
                        .setMessage("This app cannot be used without permissions!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SERVICE_ON_DESTROY = false;
                                MainMenuActivity.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

    }

    private void addHeatMap() {
        list = null;
        reportReceived = 0;

        // Get the data: latitude/longitude positions of police stations.
        try {
            //Firebase
            //Get datasnapshot at your "users" root node
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://sagip-280d9.firebaseio.com/report");
            Query query = ref.orderByChild("id");
            query.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                //Get map of users in datasnapshot
                                list = retrieveAllReports((Map<String, Object>) dataSnapshot.getValue());

                                createMap(list);
                                reportReceived++;

                                if (reportReceived > 1)
                                    reportReceived();
                            }catch(Exception e){}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();

        }
    }

    private void reportReceived(){
        mLastLocation.getLatitude();
        mLastLocation.getLongitude();


        if(latestLat >= mLastLocation.getLatitude() - 0.01 && latestLat <= mLastLocation.getLatitude() + 0.01){
            if(latestLong >= mLastLocation.getLongitude() - 0.01 && latestLong <= mLastLocation.getLongitude() + 0.01){

                try{

                    longitudeGeo = latestLong;
                    latitudeGeo = latestLat;

                    Geocoder geocoder;
                    List<Address> addresses = new ArrayList<>();
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitudeGeo, longitudeGeo, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0);

                  //  Toast.makeText(this, "ALERT: A nearby report was triggered from " + address, Toast.LENGTH_LONG).show();

                    String message = "A nearby report was recorded from " + address;

                    startForegroundNotif(message);

                }
                catch (Exception e){
                // Toast.makeText(this, "ALERT: A nearby report was triggered", Toast.LENGTH_LONG).show();

                String message = "A nearby report was recorded!";

                startForegroundNotif(message);
            }
        }
        //0.01
    }
    }

    private void createMap(List<LatLng> list){
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        try {
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .radius(50)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }catch(Exception e){}
    }

    private List<LatLng> retrieveAllReports(Map<String,Object> reports) {

        List<LatLng> locations = new ArrayList<>();

        //iterate through each user, ignoring their UID
        try {
            double latest = 0;
            for (Map.Entry<String, Object> entry : reports.entrySet()) {

                //Get user map
                Map latitude = (Map) entry.getValue();
                Map longitude = (Map) entry.getValue();
                Map id = (Map) entry.getValue();

                //Get phone field and append to list
                locations.add(new LatLng(Double.parseDouble(latitude.get("latitude").toString()), Double.parseDouble(longitude.get("longitude").toString())));

                if(Double.parseDouble(id.get("id").toString()) >= latest){
                    latest = Double.parseDouble(id.get("id").toString());
                    latestLat = Double.parseDouble(latitude.get("latitude").toString());
                    latestLong = Double.parseDouble(longitude.get("longitude").toString());
                }
            }

        }catch (Exception e){}
        return getDataReports(locations);
    }

    private List<LatLng> getDataReports(List<LatLng> locations){
        List<LatLng> list = new ArrayList<>();
        LatLng latlng;

        for(int i = 0; i < locations.size(); i++){
            latlng = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
            list.add(latlng);
        }


        return list;
    }

    public void startForegroundNotif(String message){
        Intent notIntent = new Intent(this, MainMenuActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SagipNotification sagipNotification = new SagipNotification();


        this.alarmService.startForeground(SAGIP_NOTIFY_ID, sagipNotification.buildNotification(this, pendInt, message));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean checkFirst_Time(){

        SharedPreferences prefs = this.getSharedPreferences("com.example.leebet_pc.saggip", Activity.MODE_PRIVATE);

        return prefs.getBoolean("First_time",true);
    }
}
