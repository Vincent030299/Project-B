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

public class CapImageFragment extends Fragment {
    ImageView takenImage;
    Bitmap takenImageBitMap,resizedImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View capturedImage=inflater.inflate(R.layout.imagefragmentlayout,container,false);
        takenImage=capturedImage.findViewById(R.id.chosenImageFrag);
        String mymap= getArguments().getString("the cam");
        byte[] mybyte= Base64.decode(mymap,Base64.DEFAULT);
        takenImageBitMap= BitmapFactory.decodeByteArray(mybyte,0,mybyte.length);
        resizedImage=Bitmap.createScaledBitmap(takenImageBitMap, (int)(takenImageBitMap.getWidth()*2), (int)(takenImageBitMap.getHeight()*2), false);
        takenImage.setImageBitmap(takenImageBitMap);
        return capturedImage;
    }
}
