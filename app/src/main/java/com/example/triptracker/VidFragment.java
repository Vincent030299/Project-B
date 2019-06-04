package com.example.triptracker;

import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VidFragment extends Fragment {
    VideoView chosenVideoView;
    Uri chosenVideoUri;
    ConstraintLayout customMediaController;
    ImageButton rewindBtn,fastForwardBtn,playBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View chosenVideoLayout = inflater.inflate(R.layout.vidfragmentlayout,container,false);
        chosenVideoView = chosenVideoLayout.findViewById(R.id.chosenVideoFrag);
        customMediaController = chosenVideoLayout.findViewById(R.id.customMediaPLayer);
        rewindBtn = chosenVideoLayout.findViewById(R.id.rewindbtn);
        fastForwardBtn = chosenVideoLayout.findViewById(R.id.forwardBtn);
        playBtn = chosenVideoLayout.findViewById(R.id.playBtn);
        chosenVideoUri = Uri.parse(getArguments().getString("the video"));
        chosenVideoView.setVideoURI(chosenVideoUri);
        chosenVideoView.seekTo(1);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chosenVideoView.isPlaying()){
                    chosenVideoView.start();
                    playBtn.setImageResource(R.drawable.pausebtn);
                    playBtn.getBackground().setAlpha(0);
                }
                else{
                    chosenVideoView.pause();
                    playBtn.setImageResource(R.drawable.videoplaybtn);
                    playBtn.getBackground().setAlpha(0);
                }
            }
        });
        fastForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenVideoView.getCurrentPosition()+2000 > chosenVideoView.getDuration()){
                    chosenVideoView.seekTo(chosenVideoView.getDuration());
                }
                else{
                    chosenVideoView.seekTo(chosenVideoView.getCurrentPosition()+2000);
                    chosenVideoView.start();
                }

            }
        });
        rewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenVideoView.getCurrentPosition()-2000 <= 0){
                    chosenVideoView.seekTo(0);
                    chosenVideoView.start();
                }
                else {
                    chosenVideoView.seekTo(chosenVideoView.getCurrentPosition()-2000);
                    chosenVideoView.start();
                }

            }
        });
        return chosenVideoLayout;

    }
}
