package com.example.eventbooking.Events.EventPageFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EventViewModel {
    private final MutableLiveData<String> mText;

    public EventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Event View fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
