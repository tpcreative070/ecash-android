package vn.ecpay.ewallet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import butterknife.OnClick;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;

public class TestSocketActivity extends ECashBaseActivity {

    ArrayList<String> listResponseMessSockets;
    int index;

    @Override
    protected int getLayoutResId() {
        return R.layout.test_socket;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listResponseMessSockets = new ArrayList<>();
        index = 0;
    }

    @OnClick({R.id.add, R.id.delete, R.id.get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                index = index + 1;
                listResponseMessSockets.add(String.valueOf(index));
                Log.e("ahioi", "ahi");
                break;
            case R.id.delete:
                listResponseMessSockets.remove(0);
                Log.e("ahioi", "ahi");
                break;
            case R.id.get:
                String a = listResponseMessSockets.get(0);
                Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}