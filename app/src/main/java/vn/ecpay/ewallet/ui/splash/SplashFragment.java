package vn.ecpay.ewallet.ui.splash;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.ui.home.HomeFragment;
import vn.ecpay.fragmentcommon.BaseFragment;
import vn.ecpay.fragmentcommon.FragmentTransaction;
import vn.ecpay.fragmentcommon.util.Utilities;


public class SplashFragment extends BaseFragment {
    private static final int MIN_TIME_LOAD_SPLASH_SCREEN = 6 * 1000;

    private FrameLayout fragmentView;
    private Runnable showHomeRunnable;
    private long startTime;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = new FrameLayout(getActivity());
        fragmentView.setBackgroundResource(R.drawable.splash_background);
        Point screenSize = Utilities.getRealScreenSize();
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(screenSize.x, screenSize.y));
        return fragmentView;
    }

    private void startApp() {

        long runningTime = System.currentTimeMillis() - startTime;
        long delay = 0;
        if (runningTime < MIN_TIME_LOAD_SPLASH_SCREEN) {
            delay = MIN_TIME_LOAD_SPLASH_SCREEN - runningTime;
        }
        Utilities.runOnUIThread(showHomeRunnable = () -> {
            showHomeRunnable = null;
            if (isRemoving() || isDetached()) {
                return;
            }
            if (getFragmentManager() != null) {
                String nameFragment;
                nameFragment = HomeFragment.class.getName();
                FragmentTransaction transaction = new FragmentTransaction();
                transaction.setPresentFragmentName(nameFragment);
                transaction.setAnimated(true);
                transaction.setRemoveLast(true);
                Bundle bundle = new Bundle();
                bundle.putBoolean("FromSplashScreen", true);
                transaction.setArguments(bundle);
                getFragmentManager().presentFragment(transaction);

            }
        }, delay);
    }


    @Override
    public void onResume() {
        super.onResume();
        startApp();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showHomeRunnable != null) {
            Utilities.cancelRunOnUIThread(showHomeRunnable);
            showHomeRunnable = null;
        }
    }
}
