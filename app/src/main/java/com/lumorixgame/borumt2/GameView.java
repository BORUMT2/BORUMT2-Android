package com.lumorixgame.borumt2;

import android.app.AlertDialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameView extends GLSurfaceView {
    private GameRenderer r;
    private String sessionToken;

    private float lastX, lastY;
    private float touchStartX, touchStartY;
    private boolean moving;

    public GameView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        r = new GameRenderer(this.getContext());
        setRenderer(r);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        loadCharacters();
        loadCharacterState();
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
                String npc = r.getInteractableNPC();

                if (npc != null) {
                    Log.d("BORUMT2_NPC", "NPC_ETKILESIM: " + npc);

                    final String dialogue = r.getNPCDialogue();

                    if (dialogue != null) {
                        post(() -> new AlertDialog.Builder(getContext())
                                .setMessage(dialogue)
                                .setPositiveButton("Kapat", null)
                                .show());
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
    private void loadCharacterState() { Log.d("BORUMT2_CHARACTER", "Aktif karakter durumu bekleniyor"); }
}
