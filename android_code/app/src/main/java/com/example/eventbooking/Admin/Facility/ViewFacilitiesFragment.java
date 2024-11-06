package com.example.eventbooking.Admin.Facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.R;

public class ViewFacilitiesFragment extends Fragment {
    private Button adminGoBack;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_facility, container, false);

        adminGoBack = view.findViewById(R.id.admin_go_back);

        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });
        return view;
    }



}
