package com.example.eventbooking.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminActivity;
import com.example.eventbooking.Events.EventPageFragment.EventActivity;
import com.example.eventbooking.Events.EventView.EventViewActivity;
import com.example.eventbooking.Login.LoginActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.Testing.TestFragment;
import com.example.eventbooking.profile.ProfileActivity;

public class HomeViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_view, container, false);

        // Initialize buttons
        Button adminButton = rootView.findViewById(R.id.admin_activity);
        Button profileButton = rootView.findViewById(R.id.profile_activity);
        Button eventButton = rootView.findViewById(R.id.event_activity);
        Button eventViewButton = rootView.findViewById(R.id.event_view_activity);
        Button loginButton = rootView.findViewById(R.id.login_activity);
        Button testButton = rootView.findViewById(R.id.button_test);

        // Set click listeners
        adminButton.setOnClickListener(v -> startActivity(new Intent(getContext(), AdminActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(getContext(), ProfileActivity.class)));
        eventButton.setOnClickListener(v -> startActivity(new Intent(getContext(), EventActivity.class)));
        eventViewButton.setOnClickListener(v -> startActivity(new Intent(getContext(), EventViewActivity.class)));
        loginButton.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        testButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_fragment_container, new TestFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }
}
