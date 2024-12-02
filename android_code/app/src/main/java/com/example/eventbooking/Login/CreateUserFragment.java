package com.example.eventbooking.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;
/**
 * CreateUserFragment is a Fragment that represents the user creation screen.
 * It provides the user interface for creating a new user account.
 */

public class CreateUserFragment extends Fragment {
    /**
     * Inflates the layout for the fragment and returns the root view.
     * This method is called when the fragment's view is being created.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     *
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout and return the root view
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }
}
