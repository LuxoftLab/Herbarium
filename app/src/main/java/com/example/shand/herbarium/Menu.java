package com.example.shand.herbarium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity implements View.OnClickListener {

Button buttonMoveToCatchLeaves;
    Button buttonMoveToHistory;
    Button buttonMoveToShareFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonMoveToCatchLeaves = (Button)findViewById(R.id.buttonCatchLeaves);
        buttonMoveToHistory = (Button)findViewById(R.id.buttonHistory);
        buttonMoveToShareFriends = (Button)findViewById(R.id.buttonShareToFriends);

        buttonMoveToCatchLeaves.setOnClickListener(this);
        buttonMoveToHistory.setOnClickListener(this);
        buttonMoveToShareFriends.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.buttonCatchLeaves):
                Intent myIntent1 = new Intent(Menu.this, CatchLeaves.class);
                Menu.this.startActivity(myIntent1);
                break;
            case (R.id.buttonHistory):
                Intent myIntent2 = new Intent(Menu.this, History.class);
                Menu.this.startActivity(myIntent2);
                break;
            case (R.id.buttonShareToFriends):
                Intent myIntent3 = new Intent(Menu.this, ShareToFriends.class);
                Menu.this.startActivity(myIntent3);
                break;
        }

    }
}
