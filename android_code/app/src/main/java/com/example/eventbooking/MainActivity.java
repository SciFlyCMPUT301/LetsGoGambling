package com.example.eventbooking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.Testing.TestFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private QRcodeGenerator qrGenerator;
    private ImageView qrCodeImageView;
    private NavController navController;
//    private ActivityMainBinding binding;

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

        if (savedInstanceState == null) {
            // Load HomeFragment by default
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }




    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    EventFragment eventFragment = new EventFragment();
    TestFragment testFragment = new TestFragment();
//    AdminFragment adminFragment = new AdminFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {
        int itemId = item.getItemId();
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
//        } else if (itemId == R.id.nav_events) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, adminFragment)
//                    .commit();
//            return true;
//        }
        return false;
    }


}