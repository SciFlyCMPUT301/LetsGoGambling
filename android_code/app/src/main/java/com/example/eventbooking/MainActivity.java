package com.example.eventbooking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.CameraFragment;
import com.example.eventbooking.QRCode.QRCodeEventGenerate;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.QRCode.ScannedFragment;
import com.example.eventbooking.Testing.TestFragment;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.waitinglist.ViewAcceptedListFragment;
import com.example.eventbooking.waitinglist.ViewCanceledListFragment;
import com.example.eventbooking.waitinglist.ViewSignedListFragment;
import com.example.eventbooking.waitinglist.ViewWaitingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
//    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private QRcodeGenerator qrGenerator;
    private ImageView qrCodeImageView;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
//    private ActivityMainBinding binding;

    // Setting fragments here any calls will be based off of this, easier to track what
    // Fragments can be called to generate new views
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNavigationView
                = findViewById(R.id.bottom_navigation);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
////        boolean dataUploaded = preferences.getBoolean("dataUploaded", false);
////
////        if (!dataUploaded) {
//            // Call DataGenerator to upload data
//            DataGenerator dataGenerator = new DataGenerator();
//            dataGenerator.generateAndUploadData();
//
//            // Set flag to true
////            SharedPreferences.Editor editor = preferences.edit();
////            editor.putBoolean("dataUploaded", true);
////            editor.apply();
////        }

//        if (savedInstanceState == null) {
//            // Load HomeFragment by default
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new LoginFragment())
//                    .commit();
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home || itemId == R.id.nav_profile ||
        itemId == R.id.nav_events || itemId == R.id.nav_test){

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.nav_events) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, eventFragment)
                        .commit();
                return true;
            }else if (itemId == R.id.nav_test) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, testFragment)
                        .commit();
                return true;
            }
        }
        else {
            if (itemId == R.id.nav_organizer_menu) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, organizerMenuFragment)
                        .commit();
            } else if (itemId == R.id.nav_view_accepted) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, viewAcceptedListFragment)
                        .commit();
            } else if (itemId == R.id.nav_view_canceled) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, viewCanceledListFragment)
                        .commit();
            } else if (itemId == R.id.nav_view_signed) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, viewSignedListFragment)
                        .commit();
            } else if (itemId == R.id.nav_view_waiting) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, viewWaitingListFragment)
                        .commit();
            } else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commit();
            } else if (itemId == R.id.nav_notifications) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, notificationFragment)
                        .commit();
            } else if (itemId == R.id.nav_login) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, loginFragment)
                        .commit();
            } else if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
            } else if (itemId == R.id.nav_event) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventFragment)
                        .commit();
            } else if (itemId == R.id.nav_event_create) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventCreateFragment)
                        .commit();
            } else if (itemId == R.id.nav_camera) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, cameraFragment)
                        .commit();
            } else if (itemId == R.id.nav_scanned_event) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, scannedFragment)
                        .commit();
            } else if (itemId == R.id.nav_event_code_generate) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventCodeGenerate)
                        .commit();
            }else if (itemId == R.id.nav_admin) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, adminFragment)
                        .commit();
            }
            // Close the drawer after an item is selected
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //QR code intent here, this is to get the links intercepted such that we can then pass
    //Them into the program
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleQRCodeScan(intent);
    }

    private void handleQRCodeScan(Intent intent) {
        if (LoginFragment.isLoggedIn) { // Check if user is logged in
            String scannedData = intent.getStringExtra("scanned_data");
            if (scannedData != null) {
                ScannedFragment scannedFragment = ScannedFragment.newInstance(scannedData);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, scannedFragment)
                        .commit();
            }
        }
    }


}