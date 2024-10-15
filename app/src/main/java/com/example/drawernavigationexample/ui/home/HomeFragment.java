package com.example.drawernavigationexample.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.drawernavigationexample.R;
import com.example.drawernavigationexample.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private TextView additionalInfoTextView;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        additionalInfoTextView = root.findViewById(R.id.additional_info_text_view_home);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int additionalInfo = arguments.getInt("additionalInfo", -1); // Default to -1 if not found
            if (additionalInfo != -1) {
                // Display the additional information
                additionalInfoTextView.setText("Additional Info: " + additionalInfo);
            }
        }

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}