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

public class vidfragment extends Fragment {
    VideoView thechosenvidview;
    Uri chosenviduri;
    int clicks=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thechosenvid=inflater.inflate(R.layout.vidfragmentlayout,container,false);
        thechosenvidview=thechosenvid.findViewById(R.id.fragvid);
        chosenviduri= Uri.parse(getArguments().getString("the video"));
        thechosenvidview.setVideoURI(chosenviduri);
        thechosenvidview.seekTo(1);
        thechosenvidview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicks==0){
                    thechosenvidview.start();
                    clicks++;
                }
                else{
                    thechosenvidview.pause();
                    clicks=0;
                }

            }
        });
        return thechosenvid;

    }
}
