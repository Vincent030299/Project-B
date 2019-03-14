package com.example.test_app;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class imagefragment extends Fragment {
    Uri myimageuri;
    ImageView myimageview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theview= inflater.inflate(R.layout.imagefragmentlayout,container,false);
        myimageview=theview.findViewById(R.id.fragimage);
        myimageuri=Uri.parse(getArguments().getString("the image"));
        myimageview.setImageURI(myimageuri);
        return theview;

    }
}
