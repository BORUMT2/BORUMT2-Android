package com.lumorixgame.borumt2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameView extends GLSurfaceView {
    private GameRenderer r;
    private String sessionToken;

    private float lastX, lastY;
    private float touchStartX, touchStartY;
    private boolean moving;

    private AlertDialog npcDialog;

    public GameView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        r = new GameRenderer(this.getContext());
        setRenderer(r);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public GameRenderer getRenderer() {
        return r;
    }

    public void returnToCity() {
        if (r != null && r.getPlayer() != null) {
            r.getPlayer().returnToCity();
        }
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        loadCharacters();
        r.setSessionToken(sessionToken);
    }

    private void loadCharacters() {
        if (sessionToken == null || sessionToken.isEmpty()) {
            Log.d("BORUMT2_CHARACTERS", "Session token yok");
            return;
        }

        new Thread(() -> {
            ServerClient client = new ServerClient();

            if (!client.connect("10.0.2.2", 5000)) {
                Log.d("BORUMT2_CHARACTERS", "Sunucuya baglanilamadi");
                return;
            }

            try {
                JSONObject request = new JSONObject();
                request.put("command", "LIST_CHARACTERS");
                request.put("session_token", sessionToken);

                JSONObject response = client.request(request);

                if (response != null && response.optBoolean("ok")) {
                    JSONArray characters = response.optJSONArray("characters");
                    int count = characters == null ? 0 : characters.length();
                    Log.d("BORUMT2_CHARACTERS", "Karakter sayisi: " + count);
                } else {
                    Log.d("BORUMT2_CHARACTERS", "Karakter listesi alinamadi");
                }
            } catch (Exception e) {
                Log.d("BORUMT2_CHARACTERS", "Karakter listesi hatasi");
            } finally {
                client.close();
            }
        }).start();
    }

    private void showNPCDialogue(String dialogue) {
        if (npcDialog != null && npcDialog.isShowing()) {
            return;
        }

        String[] parts = dialogue.split("\\\\n\\\\n", 2);
        String title = parts.length > 0 ? parts[0] : "NPC";
        String message = parts.length > 1 ? parts[1] : dialogue;

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 16);

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(Color.rgb(90, 55, 25));
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView messageView = new TextView(getContext());
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(Color.DKGRAY);
        messageView.setPadding(0, 20, 0, 20);

        Button closeButton = new Button(getContext());
        closeButton.setText("Kapat");

        layout.addView(titleView);
        layout.addView(messageView);
        layout.addView(closeButton);

        npcDialog = new AlertDialog.Builder(getContext())
                .setView(layout)
                .create();

        closeButton.setOnClickListener(v -> npcDialog.dismiss());

        npcDialog.setOnDismissListener(d -> npcDialog = null);
        npcDialog.show();
    }

    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = x;
            lastY = y;
            touchStartX = x;
            touchStartY = y;
            moving = x < getWidth() * 0.45f;

            if (!moving) {

                // Önce yakındaki mob hedeflenir.
                if (r.attackNearestMob()) {
                    Log.d("BORUMT2_COMBAT", "MOB_SALDIRISI");
                } else {

                    // Yakında mob yoksa normal NPC etkileşimi.
                    String npc = r.getInteractableNPC();

                    if (npc != null) {
                        Log.d("BORUMT2_NPC", "NPC_ETKILESIM: " + npc);

                        if ("Ticaret Yöneticisi".equals(npc)) {
                            post(() -> TradeManagerMarketDialog.show(
                                    getContext(),
                                    r.getPlayer()
                            ));
                        } else {
                            final String dialogue = r.getNPCDialogue();

                            if (dialogue != null) {
                                post(() -> showNPCDialogue(dialogue));
                            }
                        }
                    }
                }
            }

            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = x - lastX;
            float dy = y - lastY;

            if (moving) {
                r.move(dx * 0.012f, dy * 0.012f);
            } else {
                r.rotate(dx * 0.008f, dy * 0.004f);
            }

            lastX = x;
            lastY = y;
            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_CANCEL) {
            moving = false;
            return true;
        }

        return true;
    }
}
