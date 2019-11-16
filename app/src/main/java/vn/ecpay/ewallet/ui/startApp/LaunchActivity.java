package vn.ecpay.ewallet.ui.startApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.ui.account.AccountActivity;

public class LaunchActivity extends ECashBaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.circle)
    CircleIndicator circleIndicator;
    private SlidePager myPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String isIntro = SharedPrefs.getInstance().get(SharedPrefs.INTRO, String.class);
        if (!isIntro.isEmpty()) {
            startMainScreen();
        } else {
            setViewPager();
        }
    }

    private void setViewPager() {
        myPager = new SlidePager(this);
        viewPager.setAdapter(myPager);
        circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    tvNext.setText(getResources().getString(R.string.str_start));
                } else {
                    tvNext.setText(getResources().getString(R.string.str_continute));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.slide_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @OnClick({R.id.tv_skip, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                startMainScreen();
                break;
            case R.id.tv_next:
                int currPos = viewPager.getCurrentItem();
                if (currPos == 3) {
                    startMainScreen();
                } else {
                    viewPager.setCurrentItem(currPos + 1);
                }
                break;
        }
    }

    private void startMainScreen() {
        SharedPrefs.getInstance().put(SharedPrefs.INTRO, Constant.ON);
        Intent intent = new Intent(LaunchActivity.this, AccountActivity.class);
        startActivity(intent);
        finish();
    }
}
