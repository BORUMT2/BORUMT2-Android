package com.lumorixgame.borumt2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        String sessionToken = getIntent().getStringExtra("session_token");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        GameView gameView = new GameView(this);
        gameView.setSessionToken(sessionToken);

        FrameLayout root = new FrameLayout(this);
        root.addView(gameView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        Button cityButton = new Button(this);
        cityButton.setText("Şehre Dön");

        FrameLayout.LayoutParams buttonParams =
                new FrameLayout.LayoutParams(
                        280,
                        80,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
                );

        buttonParams.setMargins(0, 0, 0, 24);

        root.addView(cityButton, buttonParams);

        cityButton.setOnClickListener(v -> gameView.returnToCity());

        setContentView(root);
    }
}
