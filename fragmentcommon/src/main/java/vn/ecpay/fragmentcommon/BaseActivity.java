package vn.ecpay.fragmentcommon;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.ecpay.fragmentcommon.util.Utilities;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    FragmentManager fragmentManager = new FragmentManager();
    boolean mCreated;
    boolean activated;
    boolean fromFinish;

    protected ArrayList<BaseFragment> getBackStack() {
        return null;
    }

    protected ArrayList<BaseFragment> getChildFragments() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        fragmentManager.attachContainer(this, new FragmentContainer() {
            @Nullable
            @Override
            public View findViewById(int id) {
                return BaseActivity.this.findViewById(id);
            }
        }, null);
        super.onCreate(savedInstanceState);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            Utilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        fragmentManager.initBackStack(getBackStack());
        fragmentManager.initChildFragments(getChildFragments());
        fragmentManager.dispatchCreate();

    }

    @Override
    public void finish() {
        fromFinish = true;
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mCreated) {
            mCreated = true;
            fragmentManager.dispatchActivityCreated();
        }
        fragmentManager.dispatchStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activated = true;
        fragmentManager.dispatchResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activated = false;
        fragmentManager.dispatchPause();
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    protected void onStop() {
        super.onStop();
        fragmentManager.dispatchStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentManager.dispatchReallyStop();
        fragmentManager.dispatchDestroy();
        if (fromFinish) {
            if (getBackStack() != null) {
                getBackStack().clear();
            }
        }
    }

    public void destroyViewForAnotherResources() {
        fragmentManager.dispatchDestroyView();
        fragmentManager.dispatchResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        fragmentManager.dispatchLowMemory();
    }

    public FragmentManager getBaseFragmentManager() {
        return fragmentManager;
    }

    @Override
    public void onBackPressed() {
        if (!fragmentManager.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fragmentManager.dispatchConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentManager.dispatchActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fragmentManager.dispatchRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        fragmentManager.dispatchMultiWindowModeChanged(isInMultiWindowMode);
    }

    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        fragmentManager.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
    }

}
