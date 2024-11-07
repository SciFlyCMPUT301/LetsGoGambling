package com.example.eventbooking.Events.EventPageFragment;
import com.example.eventbooking.Events.EventData.Event;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.waitinglist.WaitingList;

public class EventFragment extends Fragment {
    private TextView additionalInfoTextView;
    private static final String ARG_INTEGER = "arg_integer";
    private int receivedInteger;

    private String eventId;
    private Button joinWaitingListButton;
    private Button leaveWaitingListButton;
    private Event event;
    private String currentUserId;
    private WaitingList waitingList;
    //setting up a button that to organizer
    private Button organizerMenuButton;

    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        int integer;
        args.putInt(ARG_INTEGER, integer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            receivedInteger = getArguments().getInt(ARG_INTEGER);
        }
    }

    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        TextView integerTextView = rootView.findViewById(R.id.event_integer_text);
        integerTextView.setText("Integer: " + receivedInteger);
        TextView page_name = rootView.findViewById(R.id.event_title);

        // Set up button to go back to HomeFragment
        Button backButton = rootView.findViewById(R.id.button_back_home);
        Button organizerMenuButton = rootView.findViewById(R.id.organizer_menu);

        joinWaitingListButton = rootView.findViewById(R.id.join_waiting_list);
//        joinWaitingListButton.findViewById(R.id.join_waiting_list);
        joinWaitingListButton.setOnClickListener(v->joinWaitingList());

        leaveWaitingListButton = rootView.findViewById(R.id.leave_waiting_list);
//        leaveWaitingListButton.findViewById(R.id.leave_waiting_list);
        leaveWaitingListButton.setOnClickListener(v->leaveWaitingList());

        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        organizerMenuButton.setOnClickListener(v->{
            //navigate to the waiting list menu
            OrganizerMenuFragment organizerMenuFragment=OrganizerMenuFragment.newInstance( eventId);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, organizerMenuFragment)
                    .commit();
        });


        return rootView;
    }

    private void checkIfUserIsOrganizer(){
        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser==null){
            organizerMenuButton.setVisibility(View.GONE);
            return;
        }
        String currentUserId = currentUser.getDeviceID();
        String organizerId= event.getOrganizerId();
        if(currentUserId.equals(organizerId)){
            organizerMenuButton.setVisibility(View.VISIBLE);
        }else{
            organizerMenuButton.setVisibility(View.GONE);
        }
    }

    //allow current user to join the waiting list
    private void joinWaitingList(){
        if(waitingList==null){
            Toast.makeText(getContext(),"WaitingList not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getContext(),"User not found",Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getDeviceID();
        boolean success = waitingList.addParticipantToWaitingList(currentUserId);
        if(success){
            //update firebase
            //updateWaitingListInFirebase();
            Toast.makeText(getContext(),"Joined the waiting list",Toast.LENGTH_SHORT).show();
            updateButtonsState();
        }else{
            Toast.makeText(getContext(),"Failed to join the waitingList",Toast.LENGTH_SHORT).show();
        }


    }

    //allow current user to leave waiting list
    private void leaveWaitingList(){
        if(waitingList==null){
            Toast.makeText(getContext(),"WaitingList not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getContext(),"User not found",Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getDeviceID();
        boolean success = waitingList.cancelParticipation(currentUserId);
        if(success){
            //update firebase
            //updateWaitingListInFirebase();
            Toast.makeText(getContext(),"left the waiting list",Toast.LENGTH_SHORT).show();
            updateButtonsState();
        }else{
            Toast.makeText(getContext(),"Failed to leave the waitingList",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * update leave and join button state based on the status*/
    private void updateButtonsState(){
        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser ==  null){
            joinWaitingListButton.setEnabled(false);
            leaveWaitingListButton.setEnabled(false);
            return;
        }
        String currentUserId = currentUser.getDeviceID();
        if(waitingList.getWaitingParticipantIds().contains(currentUserId)
        ){
            //what should I do for user in other list
            joinWaitingListButton.setEnabled(false);
            leaveWaitingListButton.setEnabled(true);
        } else{
            joinWaitingListButton.setEnabled(true);
            leaveWaitingListButton.setEnabled(true);
        }
    }




}
