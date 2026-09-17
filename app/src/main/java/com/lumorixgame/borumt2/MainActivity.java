package com.lumorixgame.borumt2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.Gravity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView playerInfoView;
    private TextView combatInfoView;

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

        // Oyuncu bilgi paneli
        playerInfoView = new TextView(this);
        playerInfoView.setTextColor(Color.WHITE);
        playerInfoView.setTextSize(16);
        playerInfoView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        playerInfoView.setGravity(Gravity.LEFT);
        playerInfoView.setPadding(20, 16, 20, 16);
        playerInfoView.setBackgroundColor(Color.argb(170, 0, 0, 0));

        FrameLayout.LayoutParams playerParams =
                new FrameLayout.LayoutParams(
                        520,
                        145,
                        Gravity.TOP | Gravity.LEFT
                );

        playerParams.setMargins(20, 20, 0, 0);
        root.addView(playerInfoView, playerParams);

        // Seçili mob bilgi paneli
        combatInfoView = new TextView(this);
        combatInfoView.setTextColor(Color.YELLOW);
        combatInfoView.setTextSize(16);
        combatInfoView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        combatInfoView.setGravity(Gravity.RIGHT);
        combatInfoView.setPadding(20, 16, 20, 16);
        combatInfoView.setBackgroundColor(Color.argb(170, 0, 0, 0));

        FrameLayout.LayoutParams combatParams =
                new FrameLayout.LayoutParams(
                        420,
                        120,
                        Gravity.TOP | Gravity.RIGHT
                );

        combatParams.setMargins(0, 20, 20, 0);
        root.addView(combatInfoView, combatParams);

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

        // Bilgileri sürekli güncelle.
        final Runnable updateUi = new Runnable() {
            @Override
            public void run() {
                if (gameView.getRenderer() != null) {
                    GameRenderer r = gameView.getRenderer();

                    Player player = r.getPlayer();

                    if (player != null) {
                        playerInfoView.setText(
                                player.getDisplayName()
                                        + "  Lv." + player.getLevel()
                                        + "  " + player.getCharacterClass()
                                        + "\\nHP: " + player.data.hp
                                        + " / " + player.data.maxHp
                                        + "\\nEXP: " + player.data.exp
                                        + "    Yang: " + player.getYang()
                        );

                        Mob mob = r.getSelectedMob();

                        if (mob != null && mob.isAlive()) {
                            combatInfoView.setText(
                                    mob.name
                                            + "  Lv." + mob.level
                                            + "\\nHP: " + mob.hp
                                            + " / " + mob.maxHp
                            );
                        } else {
                            combatInfoView.setText("Hedef yok");
                        }
                    }
                }

                playerInfoView.postDelayed(this, 250);
            }
        };

        playerInfoView.post(updateUi);
    }
}
