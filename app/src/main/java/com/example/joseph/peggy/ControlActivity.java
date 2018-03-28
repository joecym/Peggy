package com.example.joseph.peggy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ControlActivity extends AppCompatActivity {
    // Tag activity for debugging in log
    private static final String TAG = "ControlActivity";

    @BindView(R.id.control_unlock_button)
    Button unlock_button;

    @BindView(R.id.control_connection_status)
    TextView connection_status;

    @BindView(R.id.control_bikeID)
    TextView Bike_ID;

    @BindView(R.id.control_switch_light_mode)
    Switch light_mode;

    @BindView(R.id.control_switch_light_state)
    Switch light_state;

    @BindView(R.id.control_riding_history)
    Button history_button;

    @BindView(R.id.control_gameview)
    Button gameview_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //Butter
        ButterKnife.bind(this);
        // Toast when moving to new activity
        Toast.makeText(this, "Currently in Control Activity.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent History = new Intent(this, HistoryActivity.class);
        final Intent JoyControl = new Intent(this, JoyControlActivity.class);

        // Declare all the components of the control activity
        final Dialog dialog = new Dialog(this);

        // Specify behavior of unlock button
        unlock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Clicking on Unlock Button");
                dialog.setTitle("Unlock Dialog.");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.control_dialog);
                final EditText Bike_ID_edittext = (EditText) dialog.findViewById(R.id.control_dialog_bikeID);

                // Specify behavior of accept button on click
                Button accept_button = (Button) dialog.findViewById(R.id.control_dialog_accept);
                accept_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Accept Button");
                        final String bike = Bike_ID_edittext.getText().toString();
                        // I hardcoded a string here because it is a unique combination of
                        // a string and an edittext converted to a string
                        Bike_ID.setText("Bike ID: " + bike);
                        Toast.makeText(ControlActivity.this, "You entered: " + bike, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                // Specify behavior of cancel button on click
                Button cancel_button = (Button) dialog.findViewById(R.id.control_dialog_cancel);
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Cancel Button");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // Specify behavior of Light Mode switch
        light_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new mode
                String current = (String) light_mode.getText();
                if (current.equals(getText(R.string.control_light_mode_switch_on))) {
                    light_mode.setText(R.string.control_light_mode_switch_off);
                } else {
                    light_mode.setText(R.string.control_light_mode_switch_on);

                }
            }
        });

        // Specify behavior of Light State switch
        light_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new state
                String current = (String) light_state.getText();
                if (current.equals(getText(R.string.control_light_state_switch_on))) {
                    light_state.setText(R.string.control_light_state_switch_off);
                } else {
                    light_state.setText(R.string.control_light_state_switch_on);

                }
            }
        });

        // Specify behavior of history button
        history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Clicking on history Button");
                startActivity(History);
            }
        });

        // Specify behavior of JoyControl button
        gameview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Clicking on JoyControl Button");
                startActivity(JoyControl);
            }
        });
    }
}

