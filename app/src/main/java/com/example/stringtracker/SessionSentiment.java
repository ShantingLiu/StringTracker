package com.example.stringtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
// Dialog Fragment to get User SessionSentiment- WKD
public class SessionSentiment extends DialogFragment {
    private static final String TAG = "SessionSentimentDialog";
    public static float ratingProj, ratingTone, ratingInton;

    public interface SaveSentListener {         // interface to return meal rating
        void didFinishSentDialog(float ratingBeer, float ratingWine, float ratingMusic);
    }

    public SessionSentiment() {  // constructor

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.session_sent_layout, container, false);

        getDialog().setTitle("Session Feedback");

        final RatingBar ratingBarProj = view.findViewById(R.id.ratingBarProj);
        final RatingBar ratingBarTone = view.findViewById(R.id.ratingBarTone);
        final RatingBar ratingBarInton = view.findViewById(R.id.ratingBarInton);

        // Save Sentiment Button
        Button button = view.findViewById(R.id.buttonSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingProj = ratingBarProj.getRating();
                ratingTone = ratingBarTone.getRating();
                ratingInton = ratingBarInton.getRating();

                saveRating(ratingProj, ratingTone, ratingInton);
            }
        });

        return view;
    }

    private void saveRating(float ratingProj, float ratingTone, float ratingInton) {
        SaveSentListener activity = (SaveSentListener) getActivity();
        activity.didFinishSentDialog(ratingProj, ratingTone, ratingInton);
        getDialog().dismiss();
    }

}
