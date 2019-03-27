package com.example.triptracker;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {
    Uri imageuri;
    ImageView mychosenimageview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thechosenimage= inflater.inflate(R.layout.imagefragmentlayout,container,false);
        mychosenimageview=thechosenimage.findViewById(R.id.chosenImageFrag);
        imageuri=Uri.parse(getArguments().getString("the image"));
        mychosenimageview.setImageURI(imageuri);
        mychosenimageview.setAdjustViewBounds(true);
        return thechosenimage;
    }
}
