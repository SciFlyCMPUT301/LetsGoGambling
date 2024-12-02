package com.example.eventbooking.Events.EventView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.UserManager;

/**
 * OrganizerEventDetailFragment displays event details for the organizer
 * and provides a button to navigate to the OrganizerMenuFragment.
 */
public class OrganizerEventDetailFragment extends Fragment {

    private String eventId;
    private String userId;
    private Event event;
    private ImageView eventPosterImage;
    private TextView eventTitleText, eventDescriptionText, eventLocationText;
    private Button organizerMenuButton;

    /**
     * Creates a new instance of OrganizerEventDetailFragment with the specified event ID.
     *
     * @param eventID The ID of the event to display.
     * @return A new instance of the OrganizerEventDetailFragment.
     */
    public static OrganizerEventDetailFragment newInstance(String eventID) {
        OrganizerEventDetailFragment fragment = new OrganizerEventDetailFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Inflates the view for this fragment, retrieves event data, and sets up the navigation button.
     * It also fetches event details from the server based on the provided event ID.
     *
     * @param inflater The LayoutInflater object used to inflate the view.
     * @param container The parent view group for the fragment's UI.
     * @param savedInstanceState A Bundle containing any saved instance state.
     * @return The inflated view for this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_event_detail, container, false);

        // Initialize views
        eventPosterImage = view.findViewById(R.id.event_poster_image);
        eventTitleText = view.findViewById(R.id.event_title_text);
        eventDescriptionText = view.findViewById(R.id.event_description_text);
        eventLocationText = view.findViewById(R.id.event_location_text); // Add this ID to XML
        organizerMenuButton = view.findViewById(R.id.button_navigate_to_menu); // Add to XML

        // Retrieve arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");

        }
        userId = UserManager.getInstance().getUserId();

        // Fetch event data based on eventId
        Event.findEventById(eventId, event -> {
            if (event != null) {
                this.event = event;
                displayEventDetails(event);
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }, e -> {
            Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // Set up navigation to OrganizerMenuFragment
        organizerMenuButton.setOnClickListener(v -> navigateToOrganizerMenu());

        return view;
    }
    /**
     * Displays the event details on the screen, such as title, description, and location.
     *
     * @param event The event object containing the event details to display.
     */
    private void displayEventDetails(Event event) {
        if (event == null) {
            Log.e("OrganizerEventDetail", "Event is null, cannot display details.");
            return;
        }

        eventTitleText.setText(event.getEventTitle());
        eventDescriptionText.setText(event.getDescription());
        eventLocationText.setText(event.getLocation()); // Assuming a "location" field is available.

    }
    /**
     * Navigates to the OrganizerMenuFragment, which allows the event organizer to manage the event.
     */
    private void navigateToOrganizerMenu() {
        Log.d("Organizer Event Detail", "Navigate to menu");
        OrganizerMenuFragment organizerMenuFragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }
}
