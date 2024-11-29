package com.example.eventbooking.Facility;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.Facility.FacilityViewAdapter;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewFacilityFragment extends Fragment {
    private Button goBackButton;
    private FirebaseFirestore db;
    private ListView facilitiesListView;
    private FacilityViewAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private Facility selectedFacility = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_facility2, container, false);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        facilityList = new ArrayList<>();
        facilitiesListView = view.findViewById(R.id.facility_list);
        facilityAdapter = new FacilityViewAdapter(getContext(), facilityList);
        facilitiesListView.setAdapter(facilityAdapter);
        goBackButton = view.findViewById(R.id.go_back);

        // Load facilities from Firestore
        loadFacilitiesFromFirestore();

        // Set click listener for the back button
        goBackButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        // Set item click listener for ListView
        facilitiesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            // Get the selected facility
            selectedFacility = facilityList.get(position);
            openFacilityDetailPage(selectedFacility);
            Toast.makeText(getContext(), "Selected: " + selectedFacility.getName(), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    /**
     * Loads the list of facilities from Firestore and updates the ListView.
     */
    private void loadFacilitiesFromFirestore() {
        db.collection("Facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Facility facility = document.toObject(Facility.class);
                            // Ensure the facility ID is set
                            facility.setFacilityID(document.getId());
                            facilityList.add(facility);
                        }
                        facilityAdapter.notifyDataSetChanged();
                        Log.d("ViewFacilityFragment", "Facilities loaded: " + facilityList.size());
                    } else {
                        Log.e("ViewFacilityFragment", "Error loading facilities: ", task.getException());
                        Toast.makeText(getContext(), "Error loading facilities", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Opens the detail page for the selected facility.
     *
     * @param selectedFacility The facility selected by the user.
     */
    private void openFacilityDetailPage(Facility selectedFacility) {
        if (selectedFacility == null || selectedFacility.getFacilityID() == null) {
            Toast.makeText(getContext(), "Invalid facility data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to the EditFacilityFragment with the selected facility ID
        EditFacilityFragment detailFragment = new EditFacilityFragment(selectedFacility.getFacilityID());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
