package com.example.eventbooking.Home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
/**
 * HomeViewModel is a ViewModel that stores and manages UI-related data for the HomeFragment.
 * It provides a LiveData object that can be observed to update the UI when the text message changes.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    /**
     * Constructor for HomeViewModel.
     * Initializes the MutableLiveData object with a default message to be displayed on the HomeFragment.
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }
    /**
     * Returns a LiveData object that allows observers to observe changes to the text message.
     * This LiveData will be observed by the HomeFragment to update the UI when the text changes.
     *
     * @return a LiveData object containing the text message for the HomeFragment
     */
    public LiveData<String> getText() {
        return mText;
    }
}