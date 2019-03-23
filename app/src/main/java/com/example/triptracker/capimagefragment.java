package com.example.triptracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class capimagefragment extends Fragment {
    ImageView takenimage;
    Bitmap mybitmap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View capturedimage=inflater.inflate(R.layout.imagefragmentlayout,container,false);
        takenimage=capturedimage.findViewById(R.id.fragimage);
        String mymap= getArguments().getString("the cam");
        byte[] mybyte= Base64.decode(mymap,Base64.DEFAULT);
        mybitmap= BitmapFactory.decodeByteArray(mybyte,0,mybyte.length);
        takenimage.setImageBitmap(mybitmap);
        return capturedimage;

    }
}