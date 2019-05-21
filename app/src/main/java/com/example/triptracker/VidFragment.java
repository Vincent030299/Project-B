package com.example.triptracker;

import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VidFragment extends Fragment {
    VideoView chosenVideoView;
    Uri chosenVideoUri;
    MediaController videoPlayOptions;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chosenVideoLayout = inflater.inflate(R.layout.vidfragmentlayout,container,false);
        chosenVideoView = chosenVideoLayout.findViewById(R.id.chosenVideoFrag);
        videoPlayOptions = new MediaController(getContext());
        chosenVideoUri = Uri.parse(getArguments().getString("the video"));
        chosenVideoView.setVideoURI(chosenVideoUri);
        chosenVideoView.seekTo(1);
        chosenVideoView.setMediaController(videoPlayOptions);
        videoPlayOptions.setAnchorView(chosenVideoLayout);

//        chosenVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!chosenVideoView.isPlaying()){
//                    chosenVideoView.start();
//                }
//                else{
//                    chosenVideoView.pause();
//                }
//
//            }
//        });
        return chosenVideoLayout;

    }
}
