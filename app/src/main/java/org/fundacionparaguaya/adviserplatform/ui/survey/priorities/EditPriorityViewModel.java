package org.fundacionparaguaya.adviserplatform.ui.survey.priorities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;
import org.fundacionparaguaya.adviserplatform.data.repositories.SurveyRepository;
import org.jcodec.common.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;

import java.util.Calendar;
import java.util.Date;

public final class EditPriorityViewModel extends ViewModel {
    private SurveyRepository repo;
    private LiveData<IndicatorQuestion> mIndicator = null;

    private String mReason;
    private String mAction;
    private Date mCompletionDate = null;

    private int numberOfMonths = 0;

    private final MutableLiveData<Integer> mQuestionsUnanswered = new MutableLiveData<>();
    private IndicatorOption.Level mPovertyLevel;

    public EditPriorityViewModel(SurveyRepository repository) {
        this.repo = repository;
        updateUnansweredCount();
    }

    final String getReason() {
        return this.mReason;
    }

    final void setReason(String mReason) {
        this.mReason = mReason;
        updateUnansweredCount();
    }

    String getAction() {
        return this.mAction;
    }

    void setAction(String value) {
        this.mAction = value;
        updateUnansweredCount();
    }

    Date getCompletionDate() {
        return this.mCompletionDate;
    }

    final void setCompletionDate(Date value) {
        this.mCompletionDate = value;
        updateUnansweredCount();
    }

    final void setIndicator(int surveyId, int indicatorIndex) {
        mIndicator = Transformations.map(repo.getSurvey(surveyId), value -> value.getIndicatorQuestions().get(indicatorIndex));
    }

    final boolean isAchievement() {
        return mPovertyLevel == IndicatorOption.Level.Green;
    }


    final LiveData<IndicatorQuestion> getIndicator() {
        if(mIndicator != null)
        {
            return mIndicator;
        }

        throw new IllegalStateException("getIndicator called before surveyId and IndicatorIndex set");
    }


    boolean areRequirementsMet()
    {
        return getNumUnanswered() == 0;
    }

    void setLevel(IndicatorOption.Level level)
    {
        mPovertyLevel = level;
        updateUnansweredCount();
    }

    IndicatorOption.Level getLevel()
    {
        return mPovertyLevel;
    }

    private void updateUnansweredCount()
    {
        int unanswered = 0;

        if(mPovertyLevel != IndicatorOption.Level.Green && (numberOfMonths == 0 || mCompletionDate==null)) unanswered++;

        if(StringUtils.isEmpty(mReason)) unanswered++;

        if(StringUtils.isEmpty(mAction)) unanswered++;

        mQuestionsUnanswered.setValue(unanswered);
    }

    LiveData<Integer> NumberOfQuestionsUnanswered()
    {
        return mQuestionsUnanswered;
    }

    int getNumUnanswered()
    {
        Integer value = mQuestionsUnanswered.getValue();

        return value!=null ? value : 0;
    }

    void setNumMonths(int i) {
        numberOfMonths = i;

        if(i>0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, numberOfMonths);
            this.mCompletionDate = cal.getTime();
        }

        updateUnansweredCount();
    }

    int getMonthsUntilCompletion()
    {
        if(mCompletionDate==null) return 0;
        else {
            DateTime start = new DateTime();
            DateTime end = new DateTime(mCompletionDate);

            return Months.monthsBetween(start, end).getMonths() + 1;
        }
    }
}
