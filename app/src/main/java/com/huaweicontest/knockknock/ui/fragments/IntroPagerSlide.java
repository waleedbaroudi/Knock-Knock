package com.huaweicontest.knockknock.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huaweicontest.knockknock.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class IntroPagerSlide extends Fragment {
    int position;

    TextView introText, introHeading;
    CircleImageView introFigure;

    String[] headings;
    String[] texts;
    int[] imageResources;

    public IntroPagerSlide(int pagerPosition) {
        position = pagerPosition;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_intro_pager_slide, container, false);
        introHeading = root.findViewById(R.id.intro_heading);
        introText = root.findViewById(R.id.intro_text);
        introFigure = root.findViewById(R.id.intro_figure);
        setViewContents();
        return root;
    }

    private void setViewContents() {
        headings = getResources().getStringArray(R.array.intro_headings);
        texts = getResources().getStringArray(R.array.intro_texts);
        imageResources = new int[]{R.drawable.touch, R.drawable.verified, R.drawable.global, R.drawable.molecular};
        introHeading.setText(headings[position]);
        introText.setText(texts[position]);
        introFigure.setImageResource(imageResources[position]);
    }
}