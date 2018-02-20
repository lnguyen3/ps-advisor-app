package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionReviewAdapter;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionSpinnerAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.BackgroundQuestionCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

import java.util.Calendar;

import static java.lang.String.format;

public abstract class QuestionFragment extends Fragment {
    protected BackgroundQuestion mQuestion;
    protected TextView mTvQuestionTitle;
    protected boolean mRequirementsMet;
    protected BackgroundQuestionCallback mCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvQuestionTitle = view.findViewById(R.id.tv_questionall_title);
        initQuestionView();
    }

    /**Stores the question that is being set and sets the title of the question
     *
     * @param question Question to set
     */
    public void setQuestion(BackgroundQuestion question)
    {
        mQuestion = question;

        if(getCallback()!=null && this.getView()!=null && mQuestion!=null) {
            initQuestionView();
        }
    }

    /**
     * Sets all of the views to match the current question for this fragment
     */
    protected void initQuestionView()
    {
        mTvQuestionTitle.setText(mQuestion.getDescription());
    }

    protected BackgroundQuestionCallback getCallback()
    {
        return mCallback;
    }

    public void setCallback(BackgroundQuestionCallback callback){
        mCallback = callback;
    }

    public static class TextQuestionFrag extends QuestionFragment {

        private AppCompatEditText familyInfoEntry;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_questiontext, container, false);
            familyInfoEntry = v.findViewById(R.id.et_questiontext_answer);
            familyInfoEntry.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String answer = familyInfoEntry.getText().toString();
                    getCallback().onQuestionAnswered(mQuestion, answer);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            return v;
        }

        @Override
        protected void initQuestionView()
        {
            switch (mQuestion.getResponseType())
            {
                case INTEGER:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                default:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
            }

            String savedResponse = getCallback().getResponseFor(mQuestion);

            if(familyInfoEntry!=null) {
                familyInfoEntry.setText(savedResponse);
            }

            super.initQuestionView();
        }
    }

    public static class DropdownQuestionFrag extends QuestionFragment {

        private Spinner mSpinnerOptions;
        private SurveyQuestionSpinnerAdapter mSpinnerAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_questiondropdown, container, false);
            mSpinnerOptions = (Spinner)v.findViewById(R.id.spinner_questiondropdown);

            mSpinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedOption = mSpinnerAdapter.getDataAt(i);
                    mSpinnerAdapter.setSelected(i);

                    getCallback().onQuestionAnswered(mQuestion,
                            mQuestion.getOptions().get(selectedOption));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            return v;
        }

        @Override
        protected void initQuestionView() {
            super.initQuestionView();

            if(mQuestion.getOptions() != null){

                mSpinnerAdapter =
                        new SurveyQuestionSpinnerAdapter(getContext(), R.layout.item_tv_questionspinner);

                mSpinnerAdapter.setValues(mQuestion.getOptions().keySet().toArray(
                        new String[mQuestion.getOptions().size()]));

                mSpinnerOptions.setAdapter(mSpinnerAdapter);

                String existingResponse = getCallback().getResponseFor(mQuestion);

                if(existingResponse==null || existingResponse.isEmpty())
                {
                    mSpinnerAdapter.showEmptyPlaceholder(getContext().getResources().
                            getString(R.string.spinner_placeholder));
                }
                else {
                    mSpinnerAdapter.setSelected(existingResponse);
                }

            } else {
                throw new IllegalArgumentException("This question has no options");
            }
        }
    }

    public static class LocationQuestionFrag extends TextQuestionFrag{

    }

    public static class DateQuestionFrag extends QuestionFragment{

        private DatePicker mDatePicker;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_questiondate, container, false);
            mDatePicker = v.findViewById(R.id.dp_questiondate_answer);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            mDatePicker.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    (view, year, monthOfYear, dayOfMonth) ->
                            getCallback().onQuestionAnswered(mQuestion,
                                    format("%04d-%02d-%02d", year, monthOfYear, dayOfMonth))
            );

            return v;
        }
    }

    //TODO: implement a Picture Frag
    public static class PictureQuestionFrag /*extends QuestionViewHolder*/ {
        /*
        LinearLayout familyInfoItem;
        ImageButton cameraButton;
        ImageButton galleryButton;
        ImageView responsePicture;

        public PictureQuestionFrag(BackgroundQuestionCallback callback, View itemView) {
            super(callback, itemView);

            familyInfoItem = itemView.findViewById(R.id.item_picturequestion);
            cameraButton = itemView.findViewById(R.id.camera_button);
            galleryButton = itemView.findViewById(R.id.gallery_button);

            cameraButton.setOnClickListener(view -> {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                itemView.getContext().startActivity(intent);
            });
        }

        public void onResponse()
        {
            responsePicture.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.INVISIBLE);
            galleryButton.setVisibility(View.INVISIBLE);
        }*/

    }


    public static class ReviewPageFragment extends Fragment {

        private Button mSubmitButton;
        private RecyclerView mRv;
        private BackgroundQuestionCallback mBackgroundQuestionCallback;
        private SurveyQuestionReviewAdapter mSurveyReviewAdapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_questionsreview, container, false);

            mRv = v.findViewById(R.id.rv_questionsreview);
            mRv.setLayoutManager(new LinearLayoutManager(v.getContext()));
            mRv.setAdapter(mSurveyReviewAdapter);

            mSubmitButton = (Button) v.findViewById(R.id.btn_surveyquestions_submit);
            mSubmitButton.setOnClickListener((view)-> mBackgroundQuestionCallback.onSubmit());

            return v;
        }

        public void setAdapter(SurveyQuestionReviewAdapter adapter) {
            mSurveyReviewAdapter = adapter;
        }

        public void setBackgroundQuestionCallback (BackgroundQuestionCallback callback){
            mBackgroundQuestionCallback = callback;
        }
    }

}