package com.lumorixgame.borumt2;
import android.app.Activity; import android.os.Bundle;
import android.content.Intent; import android.view.Window; import android.view.WindowManager;
public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b);
String sessionToken = getIntent().getStringExtra("session_token");requestWindowFeature(Window.FEATURE_NO_TITLE);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);GameView gameView = new GameView(this);
gameView.setSessionToken(sessionToken);
setContentView(gameView);}
}
