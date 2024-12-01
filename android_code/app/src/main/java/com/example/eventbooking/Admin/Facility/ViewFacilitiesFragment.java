package com.example.eventbooking.Admin.Facility;

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

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
/**
 * Fragment for displaying and managing a list of facilities.
 * This fragment loads facility data from Firestore and allows the user to view or edit facility details.
 */
public class ViewFacilitiesFragment extends Fragment {
    private Button adminGoBack, removeButton;
    private FirebaseFirestore db;
    private ListView facilitiesListView;
    private FacilityViewAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private Facility selectedFacility = null;

    /**
     * Inflates the fragment's layout and initializes components.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_facility, container, false);
        if(!UniversalProgramValues.getInstance().getTestingMode())
            db = FirebaseFirestore.getInstance();
//        facilityList = new ArrayList<>();

        adminGoBack = view.findViewById(R.id.admin_go_back);
        removeButton = view.findViewById(R.id.remove_facility_button);

        loadFacilitiesFromFirestore(view);

        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });

        // Set item click listener for ListView
        facilitiesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {

            // This below code is actually stupid and I think it makes no sense with respect to anything else
            // Dont change how users use the app for one aspect when three others do not follow this schema

            // Store the selected facility
            selectedFacility = facilityList.get(position);
            Toast.makeText(getContext(), "Selected: " + selectedFacility.getName(), Toast.LENGTH_SHORT).show();
        });

        // Handle Remove Button Click
        removeButton.setOnClickListener(v -> {
            if (selectedFacility != null) {
                removeFacility(selectedFacility);
            } else {
                Toast.makeText(getContext(), "Please select a facility to remove.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Loads the list of facilities from Firestore and updates the ListView.
     */
    private void loadFacilitiesFromFirestore(View view) {
        facilityList = new ArrayList<>();
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            db.collection("Facilities").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Facility facility = document.toObject(Facility.class);
                        facilityList.add(facility);
                    }
                    facilityAdapter.notifyDataSetChanged();
                    Log.d("ViewUsersFragment", "Users loaded: " + facilityList.size());
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            });
        }
        else{
//            facilityList = new ArrayList<>();
            facilityList.addAll(UniversalProgramValues.getInstance().getFacilityList());
//            facilityAdapter.notifyDataSetChanged();
        }
        facilitiesListView = view.findViewById(R.id.facility_list);
        facilityAdapter = new FacilityViewAdapter(getContext(), facilityList);
        facilitiesListView.setAdapter(facilityAdapter);
    }

    /**
     * Removes the selected facility from Firestore and updates the ListView.
     *
     * @param facility The selected facility to be removed.
     */
    private void removeFacility(Facility facility) {
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            db.collection("Facilities").document(facility.getFacilityID()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility removed successfully.", Toast.LENGTH_SHORT).show();
                        // Remove the facility from the list and update the adapter
                        facilityList.remove(facility);
                        facilityAdapter.notifyDataSetChanged();
                        selectedFacility = null; // Clear the selected facility
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to remove facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ViewFacilitiesFragment", "Error removing facility", e);
                    });
        }
        else{
            UniversalProgramValues.getInstance().removeSpecificFacility(facility.getFacilityID());
            facilityAdapter.notifyDataSetChanged();
            selectedFacility = null;
        }
    }
}