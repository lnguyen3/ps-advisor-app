package org.fundacionparaguaya.adviserplatform.ui.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import com.instabug.library.InstabugTrackingDelegate;

import java.lang.reflect.InvocationTargetException;

/**
 * Extending this class makes it easier to switch between fragments within the activity's container,
 * without losing the fragment's state
 *
 * @author benhylak
 */

public abstract class AbstractFragSwitcherActivity extends FragmentActivity
{
    Fragment mLastFrag;

    private int mFragmentContainer;
    private Class<? extends Fragment> mSelectedFragment;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InstabugTrackingDelegate.notifyActivityGotTouchEvent(ev, this);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Sets the container for the fragments and attaches/detatches so their state is tracked by fragment manager
     *
     *
     * @param resourceId Container that holds fragment
     */
    public void setFragmentContainer(int resourceId)
    {
        mFragmentContainer = resourceId;

    }

    public void addFragmentFromClass(Class<? extends Fragment> fragmentClass)
    {
        Fragment fragment;

        try {
            fragment = (Fragment) fragmentClass.getConstructor().newInstance();

            getSupportFragmentManager().beginTransaction().add(fragment, fragment.getClass().getName()).commit();
            getSupportFragmentManager().executePendingTransactions();

            Fragment f = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());

            getSupportFragmentManager().beginTransaction().detach(f).commit();
            getSupportFragmentManager().executePendingTransactions();

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void addFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().add(fragment, fragment.getClass().getName()).commit();
        getSupportFragmentManager().executePendingTransactions();

        getSupportFragmentManager().beginTransaction().detach(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    protected Fragment getFragment(Class fragmentClass)
    {
        return getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
    }

    protected ViewGroup getFragmentContainer()
    {
        return findViewById(mFragmentContainer);
    }
    /**
     * Detatches the currently attached fragment and replaces it with the specific fragment
     *
     * @param fragmentClass Class of the fragment to switch to
     */
    protected void switchToFrag(Class<? extends Fragment> fragmentClass)
    {
        if(!hasFragForClass(fragmentClass))
        {
            addFragmentFromClass(fragmentClass);
        }

        if(mLastFrag!=null) {
            getSupportFragmentManager().beginTransaction().detach(mLastFrag).commit();
        }

        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
        getSupportFragmentManager().beginTransaction().replace(mFragmentContainer, f).attach(f).commit();

        mSelectedFragment = fragmentClass;
        mLastFrag = f;
    }

    public Fragment getSelectedFragment() {
        if(mSelectedFragment != null)
        {
            return getFragment(mSelectedFragment);
        }
        else {
            return null;
        }
        //todo replace this with instabug call
    }

    protected boolean hasFragForClass(Class fragmentClass)
    {
        return (getSupportFragmentManager().findFragmentByTag(fragmentClass.getName())) != null;
    }

}
