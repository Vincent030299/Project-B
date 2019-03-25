package com.example.triptracker;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

public class VidFragment extends Fragment {
    VideoView chosenVideoView;
    Uri chosenVideoUri;
    int clicks=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chosenVideoLayout=inflater.inflate(R.layout.vidfragmentlayout,container,false);
        chosenVideoView =chosenVideoLayout.findViewById(R.id.chosenVideoFrag);
        chosenVideoUri = Uri.parse(getArguments().getString("the video"));
        chosenVideoView.setVideoURI(chosenVideoUri);
        chosenVideoView.seekTo(1);
        chosenVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicks==0){
                    chosenVideoView.start();
                    clicks++;
                }
                else{
                    chosenVideoView.pause();
                    clicks=0;
                }

            }
        });
        return chosenVideoLayout;

    }
}
