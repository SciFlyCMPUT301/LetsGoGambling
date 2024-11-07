package com.example.eventbooking.Admin.Facility;

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
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Admin.Users.EditUserFragment;
import com.example.eventbooking.Admin.Users.UserViewAdapter;
import com.example.eventbooking.Facility;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewFacilitiesFragment extends Fragment {
    private Button adminGoBack;
    private FirebaseFirestore db;
    private ListView facilitiesListView;
    private FacilityViewAdapter facilityAdapter;
    private ArrayList<Facility> facilityList;
    private Button addUFacility;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_facility, container, false);

        db = FirebaseFirestore.getInstance();
        facilityList = new ArrayList<>();
        facilitiesListView = view.findViewById(R.id.facility_list);
        facilityAdapter = new FacilityViewAdapter(getContext(), facilityList);
        facilitiesListView.setAdapter(facilityAdapter);
        addUFacility = view.findViewById(R.id.add_facility_button);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        adminGoBack = view.findViewById(R.id.admin_go_back);
        addUFacility = view.findViewById(R.id.add_facility_button);
        loadFacilitiesFromFirestore();


        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });


        facilitiesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Facility selectedFacility = facilityList.get(position);
            openFacilityDetailsFragment(selectedFacility);
        });
        return view;
    }

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

    private void openFacilityDetailsFragment(Facility selectedFacility) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EditFacilityFragment fragment = new EditFacilityFragment();

        Bundle bundle = new Bundle();
        bundle.putString("facilityId", selectedFacility.getFacilityID());
        Log.d("Loading Facility", "Document ID: "+ selectedFacility.getFacilityID());

        fragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}
