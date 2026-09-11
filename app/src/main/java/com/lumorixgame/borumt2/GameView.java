package com.lumorixgame.borumt2;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.util.Log;
import android.app.AlertDialog;

public class GameView extends GLSurfaceView {
    GameRenderer r;
    float lastX, lastY;
    float touchStartX, touchStartY;
    boolean moving;

    public GameView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        r = new GameRenderer();
        setRenderer(r);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
                        post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(getContext())
                                    .setMessage(dialogue)
                                    .setPositiveButton("Kapat", null)
                                    .show();
                            }
                        });
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

        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            moving = false;
            return true;
        }

        return true;
    }
}
