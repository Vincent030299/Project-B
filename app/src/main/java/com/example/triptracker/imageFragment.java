package com.example.triptracker;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {
    Uri chosenImageUri;
    ImageView chosenImageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chosenImageLayout= inflater.inflate(R.layout.imagefragmentlayout,container,false);
        chosenImageView =chosenImageLayout.findViewById(R.id.chosenImageFrag);
        chosenImageUri =Uri.parse(getArguments().getString("the image"));
        chosenImageView.setImageURI(chosenImageUri);
        chosenImageView.setAdjustViewBounds(true);
        return chosenImageLayout;
    }
}
