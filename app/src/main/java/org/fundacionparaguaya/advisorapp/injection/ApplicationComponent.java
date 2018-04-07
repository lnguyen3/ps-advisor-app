package org.fundacionparaguaya.advisorapp.injection;

import android.app.Application;
import dagger.Component;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.jobs.JobCreator;
import org.fundacionparaguaya.advisorapp.ui.common.LifeMapFragment;
import org.fundacionparaguaya.advisorapp.ui.dashboard.DashActivity;
import org.fundacionparaguaya.advisorapp.ui.families.AllFamiliesFragment;
import org.fundacionparaguaya.advisorapp.ui.families.detail.*;
import org.fundacionparaguaya.advisorapp.ui.login.LoginFragment;
import org.fundacionparaguaya.advisorapp.ui.settings.SettingsStackedFrag;
import org.fundacionparaguaya.advisorapp.ui.survey.ChooseSurveyFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.SurveyActivity;
import org.fundacionparaguaya.advisorapp.ui.survey.TakeSurveyFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.indicators.SurveyIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.indicators.SurveyIndicatorsSummary;
import org.fundacionparaguaya.advisorapp.ui.survey.priorities.EditPriorityActivity;
import org.fundacionparaguaya.advisorapp.ui.survey.priorities.SurveyPriorityListFrag;
import org.fundacionparaguaya.advisorapp.ui.survey.priorities.SurveyChoosePrioritiesFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.questions.QuestionFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.questions.SurveyEconomicQuestionsFragment;
import org.fundacionparaguaya.advisorapp.ui.survey.questions.SurveyFamilyRecordFrag;
import org.fundacionparaguaya.advisorapp.ui.survey.resume.ResumeSnapshotPopupWindow;

import javax.inject.Singleton;

/**
 * The main application component.
 */

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    Application application();

    void inject(AdvisorApplication application);
    void inject(LoginFragment loginFragment);

    void inject(DashActivity dashActivity);
    void inject(AllFamiliesFragment allFamiliesFragment);
    void inject(FamilyDetailFrag familyDetailFrag);
    void inject(SurveyActivity surveyActivity);
    void inject(ChooseSurveyFragment chooseSurveyFragment);

    void inject(SurveyIndicatorsFragment surveyIndicatorsFragment);

    void inject(SurveyFamilyRecordFrag frag);

    void inject(SurveyEconomicQuestionsFragment frag);

    void inject(JobCreator jobCreator);

    void inject(LifeMapFragment lifeMapFragment);

    void inject(SurveyPriorityListFrag lifeMapFragment);

    void inject(SettingsStackedFrag settingsFragment);

    void inject(SurveyChoosePrioritiesFragment fragment);

    void inject(FamilyPriorityDetailFragment familyPriorityDetailFragment);

    void inject(FamilyLifeMapFragment familyLifeMapFragment);

    void inject(FamilyPrioritiesListFrag familyPrioritiesListFrag);

    void inject(QuestionFragment.DateQuestionFrag dateQuestionFrag);

    void inject(QuestionFragment.LocationQuestionFrag locationQuestionFrag);

    void inject(QuestionFragment.ReviewPageFragment reviewPageFragment);

    void inject(EditPriorityActivity editPriorityActivity);

    void inject(ResumeSnapshotPopupWindow resumeSnapshotPopupWindow);

    void inject(SurveyIndicatorsSummary surveyIndicatorsSummary);

    void inject(TakeSurveyFragment takeSurveyFragment);

    void inject(FamilyPrioritiesFrag familyPrioritiesFrag);
}
