package com.lumorixgame.borumt2;
import android.content.Context; import android.opengl.GLSurfaceView; import android.view.MotionEvent;
public class GameView extends GLSurfaceView {
 GameRenderer r; float x,y;
 public GameView(Context c){super(c);setEGLContextClientVersion(2);r=new GameRenderer();setRenderer(r);setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);}
 public boolean onTouchEvent(MotionEvent e){if(e.getAction()==0){x=e.getX();y=e.getY();return true;}if(e.getAction()==2){r.rotate((e.getX()-x)*.008f,(e.getY()-y)*.004f);x=e.getX();y=e.getY();}return true;}
}
