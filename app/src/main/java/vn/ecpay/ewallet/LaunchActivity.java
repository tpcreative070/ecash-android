package vn.ecpay.ewallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.ecpay.ewallet.ui.account.AccountActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(LaunchActivity.this, AccountActivity.class);
        startActivity(intent);
        finish();
    }
}
