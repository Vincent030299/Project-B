package com.example.test_app;

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
    Uri myviduri;
    VideoView myvvideoview;
    int clicks=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theview= inflater.inflate(R.layout.vidfragmentlayout,container,false);
        myvvideoview=theview.findViewById(R.id.fragvid);
        myviduri= Uri.parse(getArguments().getString("the video"));
        myvvideoview.setVideoURI(myviduri);
        myvvideoview.seekTo(1);
        myvvideoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicks==0){
                    myvvideoview.start();
                    clicks++;
                }
                else{
                    myvvideoview.pause();
                    clicks=0;
                }

            }
        });
        return theview;


    }
}
