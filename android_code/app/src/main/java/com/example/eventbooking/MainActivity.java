package com.example.eventbooking;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Events.EventPageFragment.OragnizerEventFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.CameraFragment;
import com.example.eventbooking.QRCode.QRCodeEventGenerate;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.QRCode.ScannedFragment;
import com.example.eventbooking.Testing.TestFragment;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.waitinglist.ViewAcceptedListFragment;
import com.example.eventbooking.waitinglist.ViewCanceledListFragment;
import com.example.eventbooking.waitinglist.ViewSignedListFragment;
import com.example.eventbooking.waitinglist.ViewWaitingListFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.GeoPoint;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * MainActivity, this is the main portion to define navigation views and a controller to move
 * QR code intent to pass information to other models such as the LoginFragment and ScannedFragment
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    /**
     * Generating navigation controllers and instances to be utilized later
     */
//    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private QRcodeGenerator qrGenerator;
    private ImageView qrCodeImageView;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private static final String TAG = "MainActivity";
    private String eventIdFromQR = null;
    public static boolean isLoggedIn = false;
    public static boolean testMode = false;
//    private ActivityMainBinding binding;


    /**
     * Setting fragments here any calls will be based off of this, easier to track what fragments
     * are being called in the navigation bar and to see if they are being used at all
     */
    private OragnizerEventFragment organizerFragment = new OragnizerEventFragment();

    private HomeFragment homeFragment = new HomeFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private EventFragment eventFragment = new EventFragment();
    private TestFragment testFragment = new TestFragment();
    private EventCreateFragment eventCreateFragment= new EventCreateFragment();
    private LoginFragment loginFragment = new LoginFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    private ViewWaitingListFragment viewWaitingListFragment = new ViewWaitingListFragment();
    private ViewSignedListFragment viewSignedListFragment =new ViewSignedListFragment();
    private ViewCanceledListFragment viewCanceledListFragment = new ViewCanceledListFragment();
    private ViewAcceptedListFragment viewAcceptedListFragment = new ViewAcceptedListFragment();
    private OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
    private ScannedFragment scannedFragment = new ScannedFragment();
    private CameraFragment cameraFragment = new CameraFragment();
    private QRCodeEventGenerate eventCodeGenerate = new QRCodeEventGenerate();
    private AdminFragment adminFragment = new AdminFragment();
    private ProfileEntrantFragment profileEntrantFragment = new ProfileEntrantFragment();

//    LocationManager manager;
    public GeoPoint currentGeoPoint;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main Activity", "Create Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            testMode = bundle.getBoolean("testingMode");
//        }
        testMode = UniversalProgramValues.getInstance().getTestingMode();



        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        UserManager.getInstance().setFusedLocationClient(fusedLocationClient);
        UserManager.getInstance().setContext(this);

        /**
         * Finding and setting the views for the navigation inside of activity_main
         */
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNavigationView
                = findViewById(R.id.bottom_navigation);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        // On create dont want to go here immediately
//        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadStandardSidePanel();

        // Set up ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();



        // Not sure if needing to comment this out or not, needed?
        //For location:
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(2000);
        handleIntent(getIntent());
        getCurrentLocation();



        // Getting the login fragment given intent
//        handleIntent(getIntent());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(!UniversalProgramValues.getInstance().getTestingMode())
            createNotificationChannel(this);
//        FirebaseMessaging.getInstance().subscribeToTopic("test_topic").addOnCompleteListener(task -> {
//            String msg = task.isSuccessful() ? "Subscribed" : "Subscription failed";
//            Log.d(TAG, msg);
//        });
    }

    /**
     * The main navigation code here, first we check if its the bottom navigation view that was
     * clicked on or the drawer side bar.
     *
     * This distinguising is used for future implimentation and ease of use with current switching
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home_bottom || itemId == R.id.nav_profile_bottom ||
        itemId == R.id.nav_events || itemId == R.id.nav_test || itemId== R.id.nav_organizer_bottom){
            Log.d("Main Activity", "Navigation Item Bottom Bar");
            if (itemId == R.id.nav_home_bottom) {
                moveToFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_profile_bottom) {
                moveToFragment(profileFragment);
                return true;
            }else if (itemId == R.id.nav_test) {
                moveToFragment(testFragment);
                return true;
            }
            else if (itemId == R.id.nav_organizer_bottom){
                moveToFragment(organizerFragment);
                return true;
            }
        }
        else if(itemId == R.id.standard_nav_profile ||
                itemId == R.id.standard_nav_notifications ||
                itemId == R.id.standard_nav_event_menu ||
                itemId == R.id.standard_nav_sign_out ||
                itemId == R.id.standard_nav_admin){

            if (itemId == R.id.standard_nav_notifications) {
                moveToFragment(notificationFragment);
                return true;
            } else if (itemId == R.id.standard_nav_profile) {
                moveToFragment(profileFragment);
                return true;
            }else if (itemId == R.id.standard_nav_event_menu) {
                moveToFragment(homeFragment);
                return true;
            }
            else if (itemId == R.id.standard_nav_admin){
                moveToFragment(adminFragment);
                return true;
            }
            else if(itemId == R.id.standard_nav_sign_out){
                hideNavigationUI();
                UserManager.getInstance().setCurrentUser(new User());
                moveToFragment(loginFragment);
            }


        }



        /**
         * Drawer side bar navigation
         */
        else {
            Log.d("Main Activity", "Navigation Item Side Bar");
            if (itemId == R.id.nav_organizer_menu) {
                //here is testing by iris added
                Bundle bundle = new Bundle();
                bundle.putString("eventId","event1");//hardcode the event id
                //bundle.putInt("maxParticipants",100);
                OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
                organizerMenuFragment.setArguments(bundle);
                moveToFragment(organizerMenuFragment);
            } else if (itemId == R.id.nav_view_accepted) {
                Bundle bundle = new Bundle();
                bundle.putString("eventId","event1");
                ViewAcceptedListFragment viewAcceptedListFragment = new ViewAcceptedListFragment();
                viewAcceptedListFragment.setArguments(bundle);
                moveToFragment(viewAcceptedListFragment);
            } else if (itemId == R.id.nav_view_canceled) {
                Bundle bundle = new Bundle();
                bundle.putString("eventId","event1");
                ViewCanceledListFragment viewCanceledListFragment = new ViewCanceledListFragment();
                viewCanceledListFragment.setArguments(bundle);
                moveToFragment(viewCanceledListFragment);
            } else if (itemId == R.id.nav_view_signed) {
                Bundle bundle = new Bundle();
                bundle.putString("eventId","event1");
                ViewSignedListFragment viewSignedListFragment = new ViewSignedListFragment();
                viewSignedListFragment.setArguments(bundle);
                moveToFragment(viewSignedListFragment);
            } else if (itemId == R.id.nav_view_waiting) {
                Bundle bundle = new Bundle();
                bundle.putString("eventId","event1");
                ViewWaitingListFragment viewWaitingListFragment = new ViewWaitingListFragment();
                viewWaitingListFragment.setArguments(bundle);
                moveToFragment(viewWaitingListFragment);
            } else if (itemId == R.id.nav_profile) {
                moveToFragment(profileFragment);
            } else if (itemId == R.id.nav_notifications) {
                moveToFragment(notificationFragment);
            } else if (itemId == R.id.nav_login) {
                moveToFragment(loginFragment);
            } else if (itemId == R.id.nav_home) {
                moveToFragment(homeFragment);
            } else if (itemId == R.id.nav_event_create) {
                moveToFragment(eventCreateFragment);
            } else if (itemId == R.id.nav_camera) {
                moveToFragment(cameraFragment);
            } else if (itemId == R.id.nav_scanned_event) {
                moveToFragment(scannedFragment);
            } else if (itemId == R.id.nav_event_code_generate) {
                moveToFragment(eventCodeGenerate);
            }else if (itemId == R.id.nav_admin) {
                moveToFragment(adminFragment);
            }
            // Close the drawer after an item is selected
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }
    /**
     * ability to press the back arrow on the phone to allow increased mobility for users to close
     * the side bar.
     */
    @Override
    public void onBackPressed() {
        // Close drawer if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * The below two functions are intended on getting the QR code scanning outside of the app
     * and then passing the data inside of the QR code into the app such that we can use it.
     *
     * handleQRCodeScan gets the data and opens scannedFragment with the data being passed into it.
     * Future iterations should instead use bundles to enable more consistent loading across
     * other navigation pathways
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("INTENT", "New intent: " + intent.getData());
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    /**
     * Handling of external links and QR code scanning from outside of the app
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        Log.d("Main Activity", "Handle Intent: ");
        Log.d("MainActivity", "Intent: " + intent.getData());
//        boolean loggingin = false;
        if (intent != null && intent.getData() != null) {
            String url = intent.getData().toString();
            Log.d(TAG, "Incoming URL: " + url);

            eventIdFromQR = extractEventIdFromUrl(url);
            String eventHash = extractEventHashFromUrl(url);
            Log.d("MainActivity", "Incoming eventID: " + eventIdFromQR);
            Log.d("MainActivity", "Incoming event Hash: " + eventHash);
            FirestoreAccess fs = new FirestoreAccess();
            FirestoreAccess.getInstance().checkEventExists(eventIdFromQR, eventHash)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean exists = task.getResult();
                            if(!exists){
                                eventIdFromQR = null;
                            }
                            Log.d("Firestore", "Event exists: " + exists);
                        } else {
                            Log.e("Firestore", "Error checking event existence", task.getException());
                        }
                    });
            if (eventIdFromQR != null) {
                Log.d(TAG, "Event ID from QR code: " + eventIdFromQR);

                // If the user is already logged in, redirect immediately to ScannedFragment
                if (LoginFragment.isLoggedIn) {
                    Log.d("MainActivity Is Loggedin", "Logged in");
                    openEventViewFragment(eventIdFromQR);
//                    navigateToScannedFragment(eventIdFromQR);
                } else {
                    // Show login screen first
                    Log.d("MainActivity Is not Loggedin", "Not Logged in");
//                    loggingin = true;
                    showLoginFragment(eventIdFromQR);
                }
            } else {
                Log.e(TAG, "No event ID found in URL");
            }
        }
        if (!LoginFragment.isLoggedIn) {
            Log.d("Main Activity", "Login Fragment");
            showLoginFragment(null);
        }
    }

    /**
     * Getting the eventID from the QR code URL string
     *
     * @param url
     * @return string
     */
    private String extractEventIdFromUrl(String url) {
        // Assuming the URL is in the format: eventbooking://eventDetail?eventID=12345
        String[] eventIdParts = url.split("eventID=");
        String[] splitParts = eventIdParts[1].split("\\?hash=");
        String eventID = splitParts[0];
        String hash = splitParts[1];
        Log.d("Main Activity", "Parts1: " + eventIdParts[0]);
        Log.d("Main Activity", "Parts2: " + eventIdParts[1]);
        Log.d("Main Activity", "SUBParts1: " + splitParts[0]);
        Log.d("Main Activity", "SUBParts2: " + splitParts[1]);
        return eventID;


    }

    /**
     * Getting the event hash from the QR code URL string
     *
     * @param url
     * @return string
     */
    private String extractEventHashFromUrl(String url) {
        // Assuming the URL is in the format: eventbooking://eventDetail?eventID=12345
//        "eventbooking://eventDetail?eventID=12345?hash=" + qrCodeHash;
        String[] eventIdParts = url.split("eventID=");
        String[] splitParts = eventIdParts[1].split("\\?hash=");
        String eventID = splitParts[0];
        String hash = splitParts[1];
        Log.d("Main Activity", "Parts1: " + eventIdParts[0]);
        Log.d("Main Activity", "Parts2: " + eventIdParts[1]);
        Log.d("Main Activity", "SUBParts1: " + splitParts[0]);
        Log.d("Main Activity", "SUBParts2: " + splitParts[1]);
        return hash;
    }

    /**
     * Navigating to the login fragment
     *
     * @param eventIdFromQR
     */
    private void showLoginFragment(String eventIdFromQR) {
        // Show LoginFragment first
//        if(!testMode){
        Log.d("Main Activity", "No Test Show Login Fragment");
        LoginFragment loginArgs = LoginFragment.newInstance(eventIdFromQR);
        moveToFragment(loginArgs);
//        }
//        else{
//            Log.d("Main Activity", "No Test Show Login Fragment");
//            LoginFragment loginArgs = LoginFragment.newInstance(true, "testDeviceID", eventIdFromQR);
//            moveToFragment(loginArgs);
//        }
        Log.d("MainActivity Login Move", "Event ID: " + eventIdFromQR);


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Bundle bundle = new Bundle();
//        bundle.putString("eventIdFromQR", eventIdFromQR);
//        loginFragment.setArguments(bundle);
//        fragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, loginFragment)
//                .commit();
    }

    /**
     * If login is successful and a QR code is scanned then go to the Event View with the
     * Event ID as a parameter
     */
    public void onLoginSuccess() {
        if (eventIdFromQR != null) {
            openEventViewFragment(eventIdFromQR);
//            navigateToScannedFragment(eventIdFromQR);
        }
    }

    /**
     * Directly going to the event view fragment given an eventID as a parameter to pass through
     *
     * @param eventID
     */
    private void openEventViewFragment(String eventID) {
        Log.d("Moving to Event", "Event: " + eventID);
        EventViewFragment eventViewFragment = new EventViewFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);

        // Here get the userid and put it into it
//        args.putString("deviceId", "User1");
        eventViewFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, eventViewFragment)
                .addToBackStack(null)
                .commit();
    }



    /**
     * Displays the navigation UI elements: bottom navigation bar, sidebar, and toolbar.
     */
    public void showNavigationUI() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationView sidebar = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (nav != null) nav.setVisibility(View.VISIBLE);
        if (sidebar != null) sidebar.setVisibility(View.VISIBLE);
        if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the navigation UI elements: bottom navigation bar, sidebar, and toolbar.
     */
    public void hideNavigationUI() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationView sidebar = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (nav != null) nav.setVisibility(View.GONE);
        if (sidebar != null) sidebar.setVisibility(View.GONE);
        if (toolbar != null) toolbar.setVisibility(View.GONE);
    }

    /**
     * Retrieves the user's current location, ensuring that GPS is enabled and permissions are granted.
     */
    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        GeoPoint point = new GeoPoint(latitude, longitude);
                                        currentGeoPoint = point;
                                        UserManager.getInstance().setGeolocation(point);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    /**
     * Prompts the user to enable GPS if it is not already enabled.
     */
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(this, "GPS is already turned on", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Device does not support location services
                        break;
                }
            }
        });
    }

    /**
     * Checks if GPS is enabled on the device.
     *
     * @return true if GPS is enabled, false otherwise.
     */
    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Handles the result of permission requests, specifically for location access.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isGPSEnabled()) {
                getCurrentLocation();
            } else {
                turnOnGPS();
            }
        }
    }

    /**
     * Creates a notification channel for sending notifications on devices running Android O and above.
     *
     * @param context Application context used to access the notification manager.
     */
    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "my_channel_id";
            String channelName = "My Channel";
            String channelDescription = "This is my notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param movingFragment The fragment to display.
     */
    private void moveToFragment(Fragment movingFragment) {
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, movingFragment)
                .commit();
    }

    /**
     * Loads the admin-specific side panel menu.
     */
    public void loadAdminSidePanel() {
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear existing items
        navigationView.inflateMenu(R.menu.side_menu_admin);
    }

    /**
     * Loads the standard user-specific side panel menu.
     */
    public void loadStandardSidePanel() {
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear existing items
        navigationView.inflateMenu(R.menu.side_menu_standard);
    }

    /**
     * Loads a test-specific side panel menu for debugging or testing purposes.
     */
    public void loadTestSidePanel() {
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear existing items
        navigationView.inflateMenu(R.menu.side_menu_testing);
    }
}