package org.fundacionparaguaya.advisorapp.fragments;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.viewmodels.AllFamiliesViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;

/**
 * Created by Mone Elokda on 1/22/2018.
 */

public class BackgroundQuestionsFrag extends Fragment {

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    SharedSurveyViewModel mSharedSurveyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                 of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);
        

    }




}
