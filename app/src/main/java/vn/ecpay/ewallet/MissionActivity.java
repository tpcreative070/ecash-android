package vn.ecpay.ewallet;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MissionActivity extends AppCompatActivity {
    public static final String ACCEPT_ACTION = "Accept";
    public static final String REJECT_ACTION = "Reject";
    public static final String SHOW_ACTION = "Show";

    private Intent intent;
    private TextView tvMissionContent;
    private TextView tvMissionStatus;
    private Button btnAccept;
    private Button btnReject;
    private LinearLayout layoutAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        mappingWidgets();
        hideNotification();
        process();
    }

    private void mappingWidgets() {
        intent = getIntent();
        tvMissionContent = findViewById(R.id.tvMissionContent);
        tvMissionStatus = findViewById(R.id.tvMissionStatus);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        tvMissionContent.setText(intent.getStringExtra("MISSION"));
        layoutAction = findViewById(R.id.layoutAction);
        layoutAction.setVisibility(LinearLayout.INVISIBLE);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAction.setVisibility(LinearLayout.INVISIBLE);
                acceptMission();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAction.setVisibility(LinearLayout.INVISIBLE);
                rejectMission();
            }
        });
    }

    private void process() {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACCEPT_ACTION:
                acceptMission();
                break;
            case SHOW_ACTION:
                showMission();
                break;
            case REJECT_ACTION:
                rejectMission();
                break;

            default:
                finish();
                break;
        }
    }

    private void hideNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra("NOTIFICATION_ID", -1));
    }

    private void acceptMission()
    {
        tvMissionStatus.setText("Accepted");
        tvMissionStatus.setTextColor(Color.GREEN);
        Toast.makeText(this, "You have been accepted this mission!", Toast.LENGTH_LONG).show();
    }

    private void showMission()
    {
        layoutAction.setVisibility(LinearLayout.VISIBLE);
        tvMissionStatus.setText("Waiting");
        tvMissionStatus.setTextColor(Color.CYAN);
    }

    private void rejectMission()
    {
        tvMissionStatus.setText("Rejected");
        tvMissionStatus.setTextColor(Color.RED);
        Toast.makeText(this, "You have been rejected this mission!", Toast.LENGTH_LONG).show();
    }
}