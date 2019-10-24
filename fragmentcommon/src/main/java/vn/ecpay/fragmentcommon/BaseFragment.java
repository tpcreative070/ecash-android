package vn.ecpay.fragmentcommon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import vn.ecpay.fragmentcommon.ui.actionbar.ActionBar;
import vn.ecpay.fragmentcommon.ui.actionbar.ActionBarMenu;
import vn.ecpay.fragmentcommon.ui.widget.DrawerLayoutContainer;
import vn.ecpay.fragmentcommon.ui.widget.EmptyLayout;
import vn.ecpay.fragmentcommon.ui.widget.LoadingLayout;
import vn.ecpay.fragmentcommon.util.FileLog;
import vn.ecpay.fragmentcommon.util.LayoutHelper;
import vn.ecpay.fragmentcommon.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BaseFragment {
    static final int INITIALIZING = 0;     // Not yet created.
    static final int CREATED = 1;          // Created.
    static final int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    static final int STOPPED = 3;          // Fully created, not started.
    static final int STARTED = 4;          // Created and started, not resumed.
    static final int RESUMED = 5;          // Created started and resumed.
    private static final HashMap<String, Class<?>> sClassMap = new HashMap<>();
    protected ActionBar actionBar;
    protected ActionBarMenu actionBarMenu;
    protected LoadingLayout loadingLayout;
    protected EmptyLayout emptyLayout;
    int mState = INITIALIZING;
    boolean mCalled = false;
    View mView;
    boolean mHasMenu;
    boolean mAdded;
    boolean mRemoving;
    boolean mRestored;
    int mContainerId;
    String mTag;
    boolean mHidden;
    boolean mDetached;
    boolean mDeferStart;
    String mWho;
    int mIndex;
    BaseActivity mActivity;
    FragmentManager mFragmentManager;
    FragmentManager mChildFragmentManager;
    Bundle mSavedFragmentState;
    SparseArray<Parcelable> mSavedViewState;
    BaseFragment mTarget;
    int mTargetRequestCode;
    int mResultCode = Activity.RESULT_CANCELED;
    Intent mResultData;
    boolean mUserVisibleHint = true;
    BaseFragment mParentFragment;
    ViewGroup mContainer;
    Bundle mArguments;
    Animator alphaAnimator;
    Animator emptyAnimator;
    Dialog currentDialog;


    public static BaseFragment instantiate(Context context, String fname, @Nullable Bundle args) {
        try {
            Class<?> clazz = sClassMap.get(fname);
            if (clazz == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = context.getClassLoader().loadClass(fname);
                sClassMap.put(fname, clazz);
            }
            BaseFragment f = (BaseFragment) clazz.newInstance();
            if (args != null) {
                args.setClassLoader(f.getClass().getClassLoader());
                f.mArguments = args;
            }
            return f;
        } catch (ClassNotFoundException e) {
            throw new InstantiationException("Unable to instantiate fragment " + fname
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (java.lang.InstantiationException e) {
            throw new InstantiationException("Unable to instantiate fragment " + fname
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (IllegalAccessException e) {
            throw new InstantiationException("Unable to instantiate fragment " + fname
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        }
    }

    public Bundle getArguments() {
        return mArguments;
    }

    public void setArguments(Bundle arguments) {
        this.mArguments = arguments;
    }

    @CallSuper
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onAttach");
        }
        mCalled = true;
    }

    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onCreate");
        }
        mCalled = true;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onCreateView");
        }
        return null;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onViewCreated");
        }
        if (actionBar != null) {
            setUpActionBar();
        }
    }

    public View getView() {
        return mView;
    }

    final public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    final public FragmentManager getChildFragmentManager() {
        if (mChildFragmentManager == null) {
            instantiateChildFragmentManager();
            if (mState >= RESUMED) {
                mChildFragmentManager.dispatchResume();
            } else if (mState >= STARTED) {
                mChildFragmentManager.dispatchStart();
            } else if (mState >= ACTIVITY_CREATED) {
                mChildFragmentManager.dispatchActivityCreated();
            } else if (mState >= CREATED) {
                mChildFragmentManager.dispatchCreate();
            }
        }
        return mChildFragmentManager;
    }

    void instantiateChildFragmentManager() {
        mChildFragmentManager = new FragmentManager();
        mChildFragmentManager.attachContainer(mActivity, new FragmentContainer() {
            @Override
            @Nullable
            public View findViewById(int id) {
                if (mView == null) {
                    throw new IllegalStateException("Fragment does not have a view");
                }
                return mView.findViewById(id);
            }
        }, this);

    }

    final public BaseFragment getParentFragment() {
        return mParentFragment;
    }

    final public boolean isAdded() {
        return mActivity != null && mAdded;
    }

    final public boolean isDetached() {
        return mDetached;
    }

    final public boolean isRemoving() {
        return mRemoving;
    }

    final public boolean isResumed() {
        return mState >= RESUMED;
    }

    public BaseActivity getActivity() {
        return mActivity;
    }

    public boolean onBackPressed() {
        if (mChildFragmentManager != null && mChildFragmentManager.onBackPressed()) {
            return true;
        }
        return false;
    }

    protected void setResult(int resultCode) {
        setResult(resultCode, null);
    }

    protected void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onActivityCreated");
        }
        mCalled = true;
    }

    @CallSuper
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        mCalled = true;
    }

    @CallSuper
    public void onStart() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onStart");
        }
        mCalled = true;
    }

    @CallSuper
    public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onResume");
        }
        mCalled = true;
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
    }

    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        mCalled = true;
    }

    @CallSuper
    public void onPause() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onPause");
        }
        mCalled = true;
    }

    @CallSuper
    public void onStop() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onStop");
        }
        mCalled = true;
    }

    @CallSuper
    public void onLowMemory() {
        mCalled = true;
    }

    @CallSuper
    public void onDestroyView() {
        mCalled = true;
    }

    @CallSuper
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onDestroy");
        }
        mCalled = true;
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @CallSuper
    public void onDetach() {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "onDetach");
        }
        mCalled = true;
    }

    protected void onTransitionAnimationStart(boolean isOpen, boolean isBackward) {

    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean isBackward) {

    }

    protected Animator onCustomTransitionAnimation(boolean isOpen, Runnable runnable) {
        return null;
    }

    protected void onRemoveFromParentView() {

    }

    /**
     * Called by the fragment manager once this fragment has been removed,
     * so that we don't have any left-over state if the application decides
     * to re-use the instance.  This only clears state that the framework
     * internally manages, not things the application sets.
     */
    void initState() {
        mIndex = -1;
        mWho = null;
        mAdded = false;
        mRemoving = false;
        mRestored = false;
        mFragmentManager = null;
        mChildFragmentManager = null;
        mActivity = null;
        mContainerId = 0;
        mTag = null;
        mHidden = false;
        mDetached = false;
    }

    protected void setUpActionBar() {
        if (actionBar != null) {
            actionBar.setItemsBackgroundColor(0xffffffff);
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    onOptionsItemSelected(id);
                }
            });
            if (mHasMenu) {
                if (actionBarMenu == null) {
                    actionBarMenu = actionBar.createMenu();
                }
                onCreateOptionsMenu(actionBarMenu);
            }
        }
    }

    public final void setHasOptionsMenu(boolean hasOptionsMenu) {
        mHasMenu = hasOptionsMenu;
        if (actionBar != null) {
            if (mHasMenu) {
                if (actionBarMenu == null) {
                    actionBarMenu = actionBar.createMenu();
                }
                onCreateOptionsMenu(actionBarMenu);
            }
        }
    }

    public void onCreateOptionsMenu(ActionBarMenu menu) {

    }

    public boolean onOptionsItemSelected(int itemId) {
        if (itemId == -1) {
            finish();
            return true;
        }
        return false;
    }

    protected void showEmptyLayout(int iconId, String title, @NonNull String message) {
        if (emptyLayout == null) {
            emptyLayout = new EmptyLayout(getActivity());
        }
        if (emptyLayout.getParent() == null) {
            if (actionBar != null) {
                ViewGroup fragmentView = (ViewGroup) getView();
                if (fragmentView.getChildCount() > 1) {
                    View contentView = fragmentView.getChildAt(1);
                    if (!(contentView instanceof ScrollView) && contentView instanceof FrameLayout) {
                        ((FrameLayout) contentView).addView(emptyLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    } else if (contentView instanceof RelativeLayout) {
                        ((RelativeLayout) contentView).addView(emptyLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    }
                }
            }
            if (emptyLayout.getParent() == null) {
                if (getView() instanceof FrameLayout) {
                    ((FrameLayout) getView()).addView(emptyLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                } else {
                    FrameLayout contentActivity = getActivity().findViewById(R.id.fragment_container);
                    if (contentActivity != null) {
                        contentActivity.addView(emptyLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    }
                }
            }
        }
        if (emptyLayout.getParent() == null) {
            throw new RuntimeException("Container view can not be null");
        }
        emptyLayout.setErrorMessage(message);
        emptyLayout.setTitle(title);
        emptyLayout.setIcon(iconId);
        if (emptyAnimator != null) {
            emptyAnimator.cancel();
            emptyAnimator = null;
        }
        try {
            emptyAnimator = ValueAnimator.ofFloat(emptyLayout.getAlpha(), 1f);
            ((ValueAnimator) emptyAnimator).addUpdateListener(animation -> {
                if (emptyLayout != null && emptyLayout.getParent() != null) {
                    emptyLayout.setAlpha((float) animation.getAnimatedValue());
                }
            });
            emptyAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (emptyAnimator != null && emptyAnimator.equals(animation)) {
                        emptyAnimator = null;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (emptyAnimator != null && emptyAnimator.equals(animation)) {
                        emptyAnimator = null;
                    }
                }
            });
            emptyAnimator.setDuration(200);
            emptyAnimator.start();

        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected void hideEmptyLayout() {
        try {
            if (emptyAnimator != null) {
                emptyAnimator.cancel();
                emptyAnimator = null;
            }
            if (emptyLayout == null) {
                return;
            }
            emptyAnimator = ValueAnimator.ofFloat(emptyLayout.getAlpha(), 0f);
            ((ValueAnimator) emptyAnimator).addUpdateListener(animation -> {
                if (emptyLayout != null && emptyLayout.getParent() != null) {
                    emptyLayout.setAlpha((float) animation.getAnimatedValue());
                }
            });
            emptyAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (emptyAnimator != null && emptyAnimator.equals(animation)) {
                        if (emptyLayout != null) {
                            ViewGroup parent = (ViewGroup) emptyLayout.getParent();
                            if (parent != null) {
                                parent.removeView(emptyLayout);
                            }
                            emptyLayout = null;
                        }
                        emptyAnimator = null;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (emptyAnimator != null && emptyAnimator.equals(animation)) {
                        if (emptyLayout != null) {
                            ViewGroup parent = (ViewGroup) emptyLayout.getParent();
                            if (parent != null) {
                                parent.removeView(emptyLayout);
                            }
                            emptyLayout = null;
                        }
                        emptyAnimator = null;
                    }
                }
            });
            emptyAnimator.setDuration(200);
            emptyAnimator.start();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected void showLoadingLayout() {
        if (loadingLayout == null) {
            createLoadingLayout();
        }
        if (loadingLayout == null) {
            throw new RuntimeException("Must create empty layout before show it!");
        }
        loadingLayout.setAlpha(0f);
        if (loadingLayout.getParent() == null) {
            View view = null;
            if (getView() instanceof DrawerLayoutContainer) {
                view = ((DrawerLayoutContainer) getView()).getChildAt(0);
            }
            if (actionBar != null) {
                ViewGroup fragmentView = (ViewGroup) (view == null ? getView() : view);
                if (fragmentView.getChildCount() > 1) {
                    View contentView = fragmentView.getChildAt(1);
                    if (!(contentView instanceof ScrollView) && contentView instanceof FrameLayout) {
                        ((FrameLayout) contentView).addView(loadingLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    } else if (contentView instanceof RelativeLayout) {
                        ((RelativeLayout) contentView).addView(loadingLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    }
                }
            }
            if (loadingLayout.getParent() == null) {
                if (getView() instanceof FrameLayout) {
                    ((FrameLayout) getView()).addView(loadingLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                } else {
                    FrameLayout contentActivity = getActivity().findViewById(R.id.fragment_container);
                    if (contentActivity != null) {
                        contentActivity.addView(loadingLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    }
                }
            }
        }
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
            alphaAnimator = null;
        }
        try {
            alphaAnimator = ValueAnimator.ofFloat(loadingLayout.getAlpha(), 1f);
            ((ValueAnimator) alphaAnimator).addUpdateListener(animation -> {
                if (loadingLayout != null && loadingLayout.getParent() != null) {
                    loadingLayout.setAlpha((float) animation.getAnimatedValue());
                }
            });
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (alphaAnimator != null && alphaAnimator.equals(animation)) {
                        alphaAnimator = null;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (alphaAnimator != null && alphaAnimator.equals(animation)) {
                        alphaAnimator = null;
                    }
                }
            });
            alphaAnimator.setDuration(200);
            alphaAnimator.start();

        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected LoadingLayout createLoadingLayout() {
        loadingLayout = new LoadingLayout(getActivity());
        return loadingLayout;
    }

    protected void dismissLoadingLayout() {
        try {
            if (alphaAnimator != null) {
                alphaAnimator.cancel();
                alphaAnimator = null;
            }
            if (loadingLayout == null) {
                return;
            }
            alphaAnimator = ValueAnimator.ofFloat(loadingLayout.getAlpha(), 0f);
            ((ValueAnimator) alphaAnimator).addUpdateListener(animation -> {
                if (loadingLayout != null && loadingLayout.getParent() != null) {
                    loadingLayout.setAlpha((float) animation.getAnimatedValue());
                }
            });
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (alphaAnimator != null && alphaAnimator.equals(animation)) {
                        if (loadingLayout != null) {
                            ViewGroup parent = (ViewGroup) loadingLayout.getParent();
                            if (parent != null) {
                                parent.removeView(loadingLayout);
                            }
                            loadingLayout = null;
                        }
                        alphaAnimator = null;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (alphaAnimator != null && alphaAnimator.equals(animation)) {
                        if (loadingLayout != null) {
                            ViewGroup parent = (ViewGroup) loadingLayout.getParent();
                            if (parent != null) {
                                parent.removeView(loadingLayout);
                            }
                            loadingLayout = null;
                        }
                        alphaAnimator = null;
                    }
                }
            });
            alphaAnimator.setDuration(200);
            alphaAnimator.start();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    protected ArrayList<BaseFragment> getChildFragments() {
        return null;
    }

    protected ArrayList<BaseFragment> getBackStack() {
        return null;
    }

    protected void dismissDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }

    protected void showDialog(Dialog dialog) {
        dismissDialog();
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            currentDialog = dialog;
        }
    }

    void performCreate(Bundle savedInstanceState) {
//        if (mChildFragmentManager != null) {
//            mChildFragmentManager.noteStateNotSaved();
//        }
        mState = CREATED;
        mCalled = false;
        onCreate(savedInstanceState);
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onCreate()");
        }

        if (getBackStack() != null && !getBackStack().isEmpty()) {
            getChildFragmentManager().initBackStack(getBackStack());
        }

        if (getChildFragments() != null && !getChildFragments().isEmpty()) {
            getChildFragmentManager().initChildFragments(getChildFragments());
        }
    }

    View performCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
//        if (mChildFragmentManager != null) {
//            mChildFragmentManager.noteStateNotSaved();
//        }
        return onCreateView(inflater, container, savedInstanceState);
    }

    void performActivityCreated(Bundle savedInstanceState) {
//        if (mChildFragmentManager != null) {
//            mChildFragmentManager.noteStateNotSaved();
//        }
        mState = ACTIVITY_CREATED;
        mCalled = false;
        onActivityCreated(savedInstanceState);
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onActivityCreated()");
        }
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchActivityCreated();
        }
    }

    void performStart() {
//        if (mChildFragmentManager != null) {
//            mChildFragmentManager.noteStateNotSaved();
//            mChildFragmentManager.execPendingActions();
//        }
        mState = STARTED;
        mCalled = false;
        onStart();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onStart()");
        }
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchStart();
        }
    }

    void performResume() {
//        if (mChildFragmentManager != null) {
//            mChildFragmentManager.noteStateNotSaved();
//            mChildFragmentManager.execPendingActions();
//        }
        mState = RESUMED;
        mCalled = false;
        onResume();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onResume()");
        }
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchResume();
        }
    }

    void performMultiWindowModeChanged(boolean isInMultiWindowMode) {
        onMultiWindowModeChanged(isInMultiWindowMode);
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    void performPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
        }
    }

    void performConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchConfigurationChanged(newConfig);
        }
    }

    void performLowMemory() {
        onLowMemory();
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchLowMemory();
        }
    }

    void performPause() {
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchPause();
        }
        mState = STARTED;
        mCalled = false;
        onPause();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onPause()");
        }
    }

    void performStop() {
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchStop();
        }
        mState = STOPPED;
        mCalled = false;
        dismissDialog();
        if (actionBarMenu != null) {
            actionBarMenu.hideAllPopupMenus();
        }
        onStop();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onStop()");
        }
    }

    void performReallyStop() {
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchReallyStop();
        }
        mState = ACTIVITY_CREATED;
    }

    void performDestroyView() {
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchDestroyView();
        }
        mState = CREATED;
        mCalled = false;
        onDestroyView();
        actionBar = null;
        actionBarMenu = null;
        dismissLoadingLayout();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onDestroyView()");
        }
    }

    void performDestroy() {
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchDestroy();
        }
        mState = INITIALIZING;
        mCalled = false;
        onDestroy();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onDestroy()");
        }
        mChildFragmentManager = null;
    }

    void performDetach() {
        mCalled = false;
        onDetach();
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onDetach()");
        }
        // Destroy the child FragmentManager if we still have it here.
        // We won't unless we're retaining our instance and if we do,
        // our child FragmentManager instance state will have already been saved.
        if (mChildFragmentManager != null) {
            mChildFragmentManager.dispatchDestroy();
            mChildFragmentManager = null;
        }
    }

    final void restoreViewState(Bundle savedInstanceState) {
        if (mSavedViewState != null) {
            mView.restoreHierarchyState(mSavedViewState);
            mSavedViewState = null;
        }
        mCalled = false;
        onViewStateRestored(savedInstanceState);
        if (!mCalled) {
            throw new SuperNotCalledException("Fragment " + this
                    + " did not call through to super.onViewStateRestored()");
        }
    }

    public Resources getResources() {
        if (mActivity != null) {
            return mActivity.getResources();
        }
        return null;
    }

    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return mActivity.getLayoutInflater();
    }

    public void finish() {
        finish(true);
    }

    final public void finish(boolean animated) {
        if (getFragmentManager() != null) {
            if (mRemoving || mDetached) {
                return;
            }
            getFragmentManager().closeFragment(this, animated);
        }
    }
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
        }
    }
    void performSaveInstanceState(Bundle outState) {
        onSaveInstanceState(outState);
//        if (mChildFragmentManager != null) {
//            Parcelable p = mChildFragmentManager.saveAllState();
//            if (p != null) {
//                outState.putParcelable(FragmentActivity.FRAGMENTS_TAG, p);
//            }
//        }
    }

    protected Resources getResouces() {
        if (mActivity != null) {
            return mActivity.getResources();
        }
        return null;
    }

    public String getString(@StringRes int id) {
        return BaseApplication.applicationContext.getString(id);
    }

    public String getString(@StringRes int id, Object... formatArgs) {
        return BaseApplication.applicationContext.getString(id, formatArgs);
    }

    protected void startActivity(@NonNull Intent intent) {
        startActivityForResult(intent, 0);
    }

    protected void startActivityForResult(@NonNull Intent intent, int requestCode) {
        if (getActivity() != null) {
            getActivity().startActivityForResult(intent, requestCode);
        }
    }

    protected void postUiThread(@NonNull Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            Utilities.runOnUIThread(runnable);
        }
    }

    public interface HideViewOnly {

    }

    static public class InstantiationException extends RuntimeException {
        public InstantiationException(String msg, Exception cause) {
            super(msg, cause);
        }
    }
}
