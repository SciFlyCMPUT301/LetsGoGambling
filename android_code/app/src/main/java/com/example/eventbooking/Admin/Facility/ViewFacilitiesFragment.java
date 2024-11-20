package com.example.eventbooking.Admin.Facility;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminActivity;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
/**
 * Fragment for displaying and managing a list of facilities.
 * This fragment loads facility data from Firestore and allows the user to view or edit facility details.
 */
public class ViewFacilitiesFragment extends Fragment {
    private FirebaseFirestore db;
    private ListView facilitiesListView;
    private FacilityViewAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private Button addFacility, adminGoBack;

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

        db = FirebaseFirestore.getInstance();
        facilityList = new ArrayList<>();
        facilitiesListView = view.findViewById(R.id.facility_list);
        facilityAdapter = new FacilityViewAdapter(getContext(), facilityList);
        facilitiesListView.setAdapter(facilityAdapter);
        addFacility = view.findViewById(R.id.add_facility_button);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        adminGoBack = view.findViewById(R.id.admin_go_back);
        addFacility = view.findViewById(R.id.add_facility_button);
        loadFacilitiesFromFirestore();

        addFacility.setOnClickListener(v ->{
            openFacilityDetailsFragment(null);
        });

        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            backToAdmin();
        });


        facilitiesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Facility selectedFacility = facilityList.get(position);
            openFacilityDetailsFragment(selectedFacility);
        });



        return view;
    }

    /**
     * Loads the list of facilities from Firestore and updates the ListView.
     */
    private void loadFacilitiesFromFirestore() {
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

    /**
     *  Opens the EditFacilityFragment for the selected facility to view or edit details.
     *
     * @param selectedFacility
     */
    private void openFacilityDetailsFragment(Facility selectedFacility) {
        EditFacilityFragment detailFragment = new EditFacilityFragment(selectedFacility);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.flFragmentAdmin, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void backToAdmin(){
        Intent intent = new Intent(getActivity(), AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
