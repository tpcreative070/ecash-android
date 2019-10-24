package vn.ecpay.fragmentcommon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import vn.ecpay.fragmentcommon.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import static vn.ecpay.fragmentcommon.BuildConfig.DEBUG;

public class FragmentManager {

    static final String TARGET_REQUEST_CODE_STATE_TAG = "fragment:target_req_state";
    static final String TARGET_STATE_TAG = "fragment:target_state";
    static final String VIEW_STATE_TAG = "fragment:view_state";
    static final String USER_VISIBLE_HINT_TAG = "fragment:user_visible_hint";
    private static final String TAG = "BaseFragmentManager";
    int mCurState = BaseFragment.INITIALIZING;
    BaseActivity mActivity;
    BaseFragment mParent;
    FragmentContainer mContainer;
    boolean mDestroyed;
    SparseArray<Parcelable> mStateArray;
    Bundle mStateBundle;
    ArrayList<BaseFragment> mBackStack;
    ArrayList<BaseFragment> mChildFragments;
    private List<BaseFragment> mTempStack;
    private float animationProgress;
    private long lastFrameTime;
    private Runnable animationRunnable;
    private long transitionAnimationStartTime;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    private Runnable onOpenAnimationEndRunnable;
    private Runnable onCloseAnimationEndRunnable;
    private boolean transitionAnimationInProgress;
    private Animator currentAnimation;

    public void attachContainer(BaseActivity baseActivity, FragmentContainer fragmentContainer, BaseFragment parent) {
        mActivity = baseActivity;
        mContainer = fragmentContainer;
        mParent = parent;
    }

    void initBackStack(ArrayList<BaseFragment> backStack) {
        if (backStack == null) {
            mBackStack = new ArrayList<>();
        } else {
            mBackStack = backStack;
        }
    }

    void initChildFragments(ArrayList<BaseFragment> fragments) {
        if (fragments == null) {
            mChildFragments = new ArrayList<>();
        } else {
            mChildFragments = fragments;
        }
    }

    public void dispatchCreate() {
        moveToState(BaseFragment.CREATED, true);
    }

    public void dispatchActivityCreated() {
        moveToState(BaseFragment.ACTIVITY_CREATED, false);
    }

    public void dispatchStart() {
        moveToState(BaseFragment.STARTED, false);
    }

    public void dispatchResume() {
        if (transitionAnimationInProgress) {
            if (currentAnimation != null) {
                currentAnimation.cancel();
                currentAnimation = null;
            }
            if (onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        moveToState(BaseFragment.RESUMED, false);
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            BaseFragment lastFragment = mBackStack.get(mBackStack.size() - 1);
            lastFragment.performConfigurationChanged(newConfig);
        }
    }

    public void dispatchLowMemory() {
        int count = mBackStack.size();
        BaseFragment fragment;
        for (int i = 0; i < count; i++) {
            fragment = mBackStack.get(i);
            fragment.performLowMemory();
        }
    }

    public void dispatchPause() {
        moveToState(BaseFragment.STARTED, true);
    }

    public void dispatchStop() {
        moveToState(BaseFragment.STOPPED, true);
    }

    public void dispatchReallyStop() {
        moveToState(BaseFragment.ACTIVITY_CREATED, true);
    }

    public void dispatchDestroyView() {
        moveToState(BaseFragment.CREATED, true);
    }

    public void dispatchDestroy() {
        mDestroyed = true;
        moveToState(BaseFragment.INITIALIZING, true);
        mActivity = null;
        mContainer = null;
        mParent = null;
    }

    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            mBackStack.get(mBackStack.size() - 1).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void dispatchRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            mBackStack.get(mBackStack.size() - 1).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void moveToState(int newState, boolean dispatchAll) {
        mCurState = newState;
        if (mBackStack != null && !mBackStack.isEmpty()) {
            if (dispatchAll) {
                int count = mBackStack.size();
                BaseFragment fragment;
                for (int i = 0; i < count; i++) {
                    fragment = mBackStack.get(i);
                    moveToState(fragment, newState);
                }
            } else {
                BaseFragment lastFragment = mBackStack.get(mBackStack.size() - 1);
                moveToState(lastFragment, newState);
            }
        }
        if (mChildFragments != null && !mChildFragments.isEmpty()) {
            int count = mChildFragments.size();
            BaseFragment fragment;
            for (int i = 0; i < count; i++) {
                fragment = mChildFragments.get(i);
                moveToState(fragment, newState);
            }
        }
    }

    void moveToState(BaseFragment f, int newState) {
        // Fragments that are not currently added will sit in the onCreate() state.
        if ((!f.mAdded || f.mDetached) && newState > BaseFragment.CREATED) {
            newState = BaseFragment.CREATED;
        }
        if (f.mRemoving && newState > f.mState) {
            // While removing a fragment, we can't change it to a higher state.
            newState = f.mState;
        }
        // Defer start if requested; don't allow it to move to STARTED or higher
        // if it's not already started.
        if (f.mDeferStart && f.mState < BaseFragment.STARTED && newState > BaseFragment.STOPPED) {
            newState = BaseFragment.STOPPED;
        }
        if (f.mState < newState) {
            // For fragments that are created from a layout, when restoring from
            // state we don't want to allow them to be created until they are
            // being reloaded from the layout.
            switch (f.mState) {
                case BaseFragment.INITIALIZING:
                    if (DEBUG) Log.v(TAG, "moveto CREATED: " + f);
                    if (f.mSavedFragmentState != null) {
                        f.mSavedFragmentState.setClassLoader(mActivity.getClassLoader());
                        f.mSavedViewState = f.mSavedFragmentState.getSparseParcelableArray(
                                VIEW_STATE_TAG);
                        f.mUserVisibleHint = f.mSavedFragmentState.getBoolean(
                                USER_VISIBLE_HINT_TAG, true);
                        if (!f.mUserVisibleHint) {
                            f.mDeferStart = true;
                            if (newState > BaseFragment.STOPPED) {
                                newState = BaseFragment.STOPPED;
                            }
                        }
                    }
                    f.mActivity = mActivity;
                    f.mParentFragment = mParent;
                    f.mFragmentManager = mParent != null
                            ? mParent.mChildFragmentManager : mActivity.getBaseFragmentManager();
                    f.mCalled = false;
                    f.onAttach(mActivity);
                    if (!f.mCalled) {
                        throw new SuperNotCalledException("Fragment " + f
                                + " did not call through to super.onAttach()");
                    }
                    /*if (f.mParentFragment == null) {
                        mHost.onAttachFragment(f);
                    } else {
                        f.mParentFragment.onAttachFragment(f);
                    }*/
                    f.performCreate(f.mSavedFragmentState);

                case BaseFragment.CREATED:
                    if (newState > BaseFragment.CREATED) {
                        if (DEBUG) Log.v(TAG, "moveto ACTIVITY_CREATED: " + f);
                        ViewGroup container = null;
                        if (f.mContainerId != 0) {
                            if (f.mContainerId == View.NO_ID) {
                                throwException(new IllegalArgumentException(
                                        "Cannot create fragment "
                                                + f
                                                + " for a container view with no id"));
                            }
                            container = (ViewGroup) mContainer.findViewById(f.mContainerId);
                            if (container == null && !f.mRestored) {
                                String resName;
                                try {
                                    resName = f.getResources().getResourceName(f.mContainerId);
                                } catch (Resources.NotFoundException e) {
                                    resName = "unknown";
                                }
                                throwException(new IllegalArgumentException(
                                        "No view found for id 0x"
                                                + Integer.toHexString(f.mContainerId) + " ("
                                                + resName
                                                + ") for fragment " + f));
                            }
                        }
                        f.mContainer = container;
                        f.mView = f.performCreateView(f.getLayoutInflater(
                                f.mSavedFragmentState), container, f.mSavedFragmentState);
                        if (f.mView != null) {
                            f.mView.setSaveFromParentEnabled(false);
                            if (container != null) {
                                if (mBackStack != null && !mBackStack.isEmpty()) {
                                    if (mBackStack.get(mBackStack.size() - 1).equals(f)) {
                                        container.addView(f.mView);
                                    } else if (mBackStack.contains(f)) {
                                        container.addView(f.mView, 0);
                                    }
                                } else {
                                    container.addView(f.mView, 0);
                                }
                            }
                            if (f.mHidden) f.mView.setVisibility(View.GONE);
                            f.onViewCreated(f.mView, f.mSavedFragmentState);
                        }
                        f.performActivityCreated(f.mSavedFragmentState);
                        if (f.mView != null) {
                            f.restoreViewState(f.mSavedFragmentState);
                        }
                        f.mSavedFragmentState = null;
                    }
                case BaseFragment.ACTIVITY_CREATED:
                    if (newState > BaseFragment.ACTIVITY_CREATED) {
                        f.mState = BaseFragment.STOPPED;
                    }
                case BaseFragment.STOPPED:
                    if (newState > BaseFragment.STOPPED) {
                        if (DEBUG) Log.v(TAG, "moveto STARTED: " + f);
                        f.performStart();
                    }
                case BaseFragment.STARTED:
                    if (newState > BaseFragment.STARTED) {
                        if (DEBUG) Log.v(TAG, "moveto RESUMED: " + f);
                        f.performResume();
                        f.mSavedFragmentState = null;
                        f.mSavedViewState = null;
                    }
            }
        } else if (f.mState > newState) {
            switch (f.mState) {
                case BaseFragment.RESUMED:
                    if (newState < BaseFragment.RESUMED) {
                        if (DEBUG) Log.v(TAG, "movefrom RESUMED: " + f);
                        f.performPause();
                    }
                case BaseFragment.STARTED:
                    if (newState < BaseFragment.STARTED) {
                        if (DEBUG) Log.v(TAG, "movefrom STARTED: " + f);
                        f.mSavedFragmentState = saveFragmentBasicState(f);
                        f.performStop();
                    }
                case BaseFragment.STOPPED:
                    if (newState < BaseFragment.STOPPED) {
                        if (DEBUG) Log.v(TAG, "movefrom STOPPED: " + f);
                        f.performReallyStop();
                    }
                case BaseFragment.ACTIVITY_CREATED:
                    if (newState < BaseFragment.ACTIVITY_CREATED) {
                        if (DEBUG) Log.v(TAG, "movefrom ACTIVITY_CREATED: " + f);
                        if (f.mView != null) {
                            // Need to save the current view state if not
                            // done already.
                            if (f.mSavedViewState == null) {
                                saveFragmentViewState(f);
                            }
                        }
                        f.performDestroyView();
                        if (f.mView != null && f.mContainer != null) {
                            f.mContainer.removeView(f.mView);
                        }
                        f.mContainer = null;
                        f.mView = null;
                    }
                case BaseFragment.CREATED:
                    if (newState < BaseFragment.CREATED) {
                        if (DEBUG) Log.v(TAG, "movefrom CREATED: " + f);
                        f.performDestroy();
                        f.performDetach();
                        f.mActivity = null;
                        f.mParentFragment = null;
                        f.mFragmentManager = null;
                    }
            }
        }
        if (f.mState != newState) {
            Log.w(TAG, "moveToState: Fragment state for " + f + " not updated inline; "
                    + "expected state " + newState + " found " + f.mState);
            f.mState = newState;
        }
    }

    void saveFragmentViewState(BaseFragment f) {
        if (f.mView == null) {
            return;
        }
        if (mStateArray == null) {
            mStateArray = new SparseArray<>();
        } else {
            mStateArray.clear();
        }
        f.mView.saveHierarchyState(mStateArray);
        if (mStateArray.size() > 0) {
            f.mSavedViewState = mStateArray;
            mStateArray = null;
        }
    }

    Bundle saveFragmentBasicState(BaseFragment f) {
        Bundle result = null;
        if (mStateBundle == null) {
            mStateBundle = new Bundle();
        }
        f.performSaveInstanceState(mStateBundle);
        if (!mStateBundle.isEmpty()) {
            result = mStateBundle;
            mStateBundle = null;
        }
        if (f.mView != null) {
            saveFragmentViewState(f);
        }
        if (f.mSavedViewState != null) {
            if (result == null) {
                result = new Bundle();
            }
            result.putSparseParcelableArray(
                    VIEW_STATE_TAG, f.mSavedViewState);
        }
        if (!f.mUserVisibleHint) {
            if (result == null) {
                result = new Bundle();
            }
            // Only add this if it's not the default value
            result.putBoolean(USER_VISIBLE_HINT_TAG, f.mUserVisibleHint);
        }
        return result;
    }

    private void throwException(RuntimeException ex) {
        Log.e(TAG, ex.getMessage());
        Log.e(TAG, "Activity state:");
//        LogWriter logw = new LogWriter(TAG);
//        PrintWriter pw = new PrintWriter(logw);
//        if (mHost != null) {
//            try {
//                mHost.onDump("  ", null, pw, new String[] { });
//            } catch (Exception e) {
//                Log.e(TAG, "Failed dumping state", e);
//            }
//        } else {
//            try {
//                dump("  ", null, pw, new String[] { });
//            } catch (Exception e) {
//                Log.e(TAG, "Failed dumping state", e);
//            }
//        }
        throw ex;
    }

    private int getIndexFragment(@NonNull BaseFragment f) {
        int index = -1;
        BaseFragment currentFragment = null;
        for (int i = 0; i < mBackStack.size(); i++) {
            currentFragment = mBackStack.get(i);
            if (currentFragment != null && TextUtils.equals(currentFragment.getClass().getName(), f.getClass().getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public boolean checkFragmentExist(boolean backStack, String fName) {
        if (backStack) {
            if (mBackStack == null || mBackStack.isEmpty()) {
                return false;
            }
            for (int i = 0; i < mBackStack.size(); i++) {
                BaseFragment f = mBackStack.get(i);
                if (TextUtils.equals(fName, f.getClass().getName())) {
                    return true;
                }
            }
        } else {
            if (mChildFragments == null || mChildFragments.isEmpty()) {
                return false;
            }
            for (int i = 0; i < mChildFragments.size(); i++) {
                BaseFragment f = mChildFragments.get(i);
                if (TextUtils.equals(fName, f.getClass().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addFragmentToBackStack(@NonNull BaseFragment fragment) {
        if (mBackStack == null) {
            return;
        }
        fragment.initState();
        fragment.mAdded = true;
        fragment.mContainerId = R.id.fragment_container;
        moveToState(fragment, BaseFragment.ACTIVITY_CREATED);
        mBackStack.add(fragment);
    }

    public boolean isTopFragment(@NonNull String fname) {
        if (mBackStack == null || mBackStack.isEmpty()) {
            return false;
        }
        return TextUtils.equals(mBackStack.get(mBackStack.size() - 1).getClass().getName(), fname);
    }

    public void presentFragment(@NonNull final FragmentTransaction transaction) {
        if (checkTransitionAnimation()
                || (!TextUtils.isEmpty(transaction.presentFragmentName) && isTopFragment(transaction.presentFragmentName))
                || (transaction.presentFragment != null && isTopFragment(transaction.presentFragment.getClass().getName()))) {
            return;
        }
        if (mActivity.getCurrentFocus() != null) {
            Utilities.hideKeyboard(mActivity.getCurrentFocus());
        }
        final BaseFragment fragment;
        if (transaction.presentFragment != null) {
            fragment = transaction.presentFragment;
        } else {
            fragment = BaseFragment.instantiate(mActivity, transaction.presentFragmentName, null);
        }
        fragment.initState();
        fragment.mArguments = transaction.arguments;
        fragment.mContainerId = transaction.containerId;
        fragment.mAdded = true;
        fragment.mTargetRequestCode = transaction.requestCode;
        if (transaction.addToBackStack) {
            if (mBackStack == null) {
                mBackStack = new ArrayList<>();
            }
            BaseFragment currentFragment;
            if (!mBackStack.isEmpty()) {
                currentFragment = mBackStack.get(mBackStack.size() - 1);
            } else {
                currentFragment = null;
            }
            if (transaction.requestCode != 0) {
                fragment.mTarget = currentFragment;
            }
            if (transaction.isClearStack) {
                mTempStack = new ArrayList<>(mBackStack);
                mBackStack.clear();
            } else if (transaction.isClearTop) {
                int index = getIndexFragment(fragment);
                if (index >= 0 && index < mBackStack.size()) {
                    mTempStack = new ArrayList<>();
                    for (int i = mBackStack.size() - 1; i >= index; i--) {
                        mTempStack.add(mBackStack.get(i));
                    }
                    mBackStack.removeAll(mTempStack);
                }
            }
            mBackStack.add(fragment);

            moveToState(fragment, mCurState);

            if (currentFragment != null) {
                moveToState(currentFragment, BaseFragment.STOPPED);
            }
            onOpenAnimationEndRunnable = () -> {
                fragment.onTransitionAnimationEnd(true, false);
                presentFragmentRemoveOld(transaction.removeLast, currentFragment);
                if (mTempStack != null && !mTempStack.isEmpty()) {
                    for (int i = 0; i < mTempStack.size(); i++) {
                        presentFragmentRemoveOld(true, mTempStack.get(i));
                    }
                    mTempStack.clear();
                    mTempStack = null;
                }
            };
            transitionAnimationStartTime = System.currentTimeMillis();
            transitionAnimationInProgress = true;
            if (transaction.animated) {
                Animator customAnimation = fragment.onCustomTransitionAnimation(true, onOpenAnimationEndRunnable);
                if (customAnimation != null) {
                    customAnimation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            onAnimationEndCheck(false);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            fragment.onTransitionAnimationStart(true, false);
                        }
                    });
                    currentAnimation = customAnimation;
                    customAnimation.start();
                } else if (fragment.mView != null) {
                    fragment.onTransitionAnimationStart(true, false);
                    fragment.mView.setAlpha(0f);
                    fragment.mView.setTranslationX(Utilities.dp(48f));
                    startLayoutAnimation(true, true, fragment.mView);
                }
            } else {
                fragment.onTransitionAnimationStart(true, false);
                onAnimationEndCheck(false);
            }
        } else {
            if (mChildFragments == null) {
                if (mParent != null && mParent.getChildFragments() != null) {
                    mChildFragments = mParent.getChildFragments();
                } else {
                    mChildFragments = new ArrayList<>();
                }
            }
            mChildFragments.add(fragment);
            moveToState(fragment, mCurState);
            fragment.onTransitionAnimationStart(true, false);
            fragment.onTransitionAnimationEnd(true, false);
        }
    }

    private void presentFragmentRemoveOld(boolean removeLast, BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        if (removeLast) {
            moveToState(fragment, BaseFragment.INITIALIZING);
            mBackStack.remove(fragment);
        } else {
            if (fragment.mView != null) {
                if (fragment instanceof BaseFragment.HideViewOnly) {
                    fragment.mView.setVisibility(View.GONE);
                } else {
                    ViewGroup parent = (ViewGroup) fragment.mView.getParent();
                    if (parent != null) {
                        parent.removeView(fragment.mView);
                        fragment.onRemoveFromParentView();
                    }
                }
            }
        }
    }

    public boolean onBackPressed() {
        if (mBackStack == null || mBackStack.isEmpty()) {
            return false;
        }
        BaseFragment currentFragment = mBackStack.get(mBackStack.size() - 1);
        boolean result = currentFragment.onBackPressed();
        if (!result) {
            if ((mParent == null && mBackStack.size() < 2)) {
                return false;
            }
            closeFragment(currentFragment, true);
        }
        return true;
    }

    public void closeLastFragment(boolean animated) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            closeFragment(mBackStack.get(mBackStack.size() - 1), animated);
        }
    }

    public void closeFragment(@NonNull final BaseFragment f, boolean animated) {
        if (mBackStack != null && mBackStack.contains(f) && checkTransitionAnimation()) {
            return;
        }
        if (mActivity.getCurrentFocus() != null) {
            Utilities.hideKeyboard(mActivity.getCurrentFocus());
        }
        f.mRemoving = true;
        if (mBackStack != null && mBackStack.contains(f)) {
            BaseFragment previousFragment;
            int index = mBackStack.indexOf(f);
            if (index > 0 && index < mBackStack.size()) {
                previousFragment = mBackStack.get(index - 1);
            } else {
                previousFragment = null;
            }
            if (previousFragment != null) {
                if (previousFragment.mState < BaseFragment.ACTIVITY_CREATED) {
                    moveToState(previousFragment, BaseFragment.ACTIVITY_CREATED);
                } else {
                    if (previousFragment.mView != null) {
                        ViewGroup container = (ViewGroup) previousFragment.mView.getParent();
                        if (!(previousFragment instanceof BaseFragment.HideViewOnly)) {
                            if (container != null) {
                                previousFragment.onRemoveFromParentView();
                                container.removeView(previousFragment.mView);
                            }
                        }
                        if (previousFragment.mContainer != null) {
                            container = previousFragment.mContainer;
                        } else if (previousFragment.mContainerId != 0) {
                            container = (ViewGroup) mContainer.findViewById(previousFragment.mContainerId);
                        }
                        if (container != null) {
                            if (previousFragment instanceof BaseFragment.HideViewOnly) {
                                previousFragment.mView.setVisibility(View.VISIBLE);
                            } else {
                                container.addView(previousFragment.mView, 0);
                            }
                            previousFragment.mContainer = container;
                        }
                    }
                }
            }
            if (f.mTargetRequestCode != 0 && f.mTarget != null) {
                f.mTarget.onActivityResult(f.mTargetRequestCode, f.mResultCode, f.mResultData);
            }
            moveToState(previousFragment, mCurState);
            if (animated) {
                transitionAnimationStartTime = System.currentTimeMillis();
                transitionAnimationInProgress = true;
                final BaseFragment previousFragmentFinal = previousFragment;
                onCloseAnimationEndRunnable = () -> {
                    closeFragmentInternalRemoveOld(f);
                    f.onTransitionAnimationEnd(false, false);
                    if (previousFragmentFinal != null) {
                        previousFragmentFinal.mView.setTranslationX(0);
                        previousFragmentFinal.onTransitionAnimationEnd(true, true);
                    }
                };
                startLayoutAnimation(false, true, f.mView);
                f.onTransitionAnimationStart(false, false);
                if (previousFragment != null) {
                    previousFragment.onTransitionAnimationStart(true, true);
                }
            } else {
                f.onTransitionAnimationStart(false, false);
                if (previousFragment != null) {
                    previousFragment.onTransitionAnimationStart(true, true);
                }
                f.onTransitionAnimationEnd(false, false);
                if (previousFragment != null) {
                    previousFragment.onTransitionAnimationEnd(true, true);
                }
                closeFragmentInternalRemoveOld(f);
            }
        } else if (mChildFragments != null && mChildFragments.contains(f)) {
            f.onTransitionAnimationStart(false, false);
            f.onTransitionAnimationEnd(false, false);
            closeFragmentInternalRemoveOld(f);
        }
    }

    private void closeFragmentInternalRemoveOld(BaseFragment f) {
        if (f == null) {
            return;
        }
        moveToState(f, BaseFragment.INITIALIZING);
        if (mBackStack != null) {
            mBackStack.remove(f);
        }
        if (mChildFragments != null) {
            mChildFragments.remove(f);
        }
    }

    private void onOpenAnimationEnd() {
        if (transitionAnimationInProgress && onOpenAnimationEndRunnable != null) {
            transitionAnimationInProgress = false;
            transitionAnimationStartTime = 0;
            onOpenAnimationEndRunnable.run();
            onOpenAnimationEndRunnable = null;
        }
    }

    private void onCloseAnimationEnd() {
        if (transitionAnimationInProgress && onCloseAnimationEndRunnable != null) {
            transitionAnimationInProgress = false;
            transitionAnimationStartTime = 0;
            onCloseAnimationEndRunnable.run();
            onCloseAnimationEndRunnable = null;
        }
    }

    private void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        if (currentAnimation != null) {
            if (byCheck) {
                currentAnimation.cancel();
            }
            currentAnimation = null;
        }
    }

    public boolean checkTransitionAnimation() {
        if (transitionAnimationInProgress && transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return transitionAnimationInProgress;
    }

    private void startLayoutAnimation(final boolean open, final boolean first, final View view) {
        if (first) {
            animationProgress = 0.0f;
            lastFrameTime = System.nanoTime() / 1000000;
        }
        Utilities.runOnUIThread(animationRunnable = new Runnable() {
            @Override
            public void run() {
                if (animationRunnable != this) {
                    return;
                }
                animationRunnable = null;
                if (first) {
                    transitionAnimationStartTime = System.currentTimeMillis();
                }
                long newTime = System.nanoTime() / 1000000;
                long dt = newTime - lastFrameTime;
                if (dt > 18) {
                    dt = 18;
                }
                lastFrameTime = newTime;
                animationProgress += dt / 150.0f;
                if (animationProgress > 1.0f) {
                    animationProgress = 1.0f;
                }
                float interpolated = decelerateInterpolator.getInterpolation(animationProgress);
                if (open) {
                    view.setAlpha(interpolated);
                    view.setTranslationX(Utilities.dp(48f) * (1.0f - interpolated));
                } else {
                    view.setAlpha(1.0f - interpolated);
                    view.setTranslationX(Utilities.dp(48f) * interpolated);
                }
                if (animationProgress < 1) {
                    startLayoutAnimation(open, false, view);
                } else {
                    onAnimationEndCheck(false);
                }
            }
        });
    }


    public void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            int count = mBackStack.size();
            BaseFragment fragment;
            for (int i = 0; i < count; i++) {
                fragment = mBackStack.get(i);
                fragment.performMultiWindowModeChanged(isInMultiWindowMode);
            }
        }
        if (mChildFragments != null && !mChildFragments.isEmpty()) {
            int count = mChildFragments.size();
            BaseFragment fragment;
            for (int i = 0; i < count; i++) {
                fragment = mChildFragments.get(i);
                fragment.performMultiWindowModeChanged(isInMultiWindowMode);
            }
        }
    }

    public void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (mBackStack != null && !mBackStack.isEmpty()) {
            int count = mBackStack.size();
            BaseFragment fragment;
            for (int i = 0; i < count; i++) {
                fragment = mBackStack.get(i);
                fragment.performPictureInPictureModeChanged(isInPictureInPictureMode);
            }
        }
        if (mChildFragments != null && !mChildFragments.isEmpty()) {
            int count = mChildFragments.size();
            BaseFragment fragment;
            for (int i = 0; i < count; i++) {
                fragment = mChildFragments.get(i);
                fragment.performPictureInPictureModeChanged(isInPictureInPictureMode);
            }
        }
    }
}