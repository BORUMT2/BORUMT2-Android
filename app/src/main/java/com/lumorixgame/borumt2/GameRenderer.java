package com.lumorixgame.borumt2;
import android.opengl.*; import android.content.Context; import com.lumorixgame.borumt2.model.ObjModel; import javax.microedition.khronos.egl.EGLConfig; import javax.microedition.khronos.opengles.GL10; import java.nio.*;
public class GameRenderer implements GLSurfaceView.Renderer {
 private String sessionToken; private Context context; private volatile ObjModel characterModel; private volatile ObjModel mapTerrain; private volatile ObjModel mapVillage; private volatile ObjModel mapForest; private volatile ObjModel mapRocks; public void setSessionToken(String token){sessionToken=token; loadCharacterState();}
 float[] p=new float[16],v=new float[16],m=new float[16],mv=new float[16]; float yaw=0,pitch=.12f; long lastFrameTime=0; Player player=new Player();
 NPC npc1=new NPC("Ticaret Yöneticisi",-3,0.7f,-3);
 NPC npc2=new NPC("Emlakçı",3,0.7f,-4);
 NPC npc3=new NPC("Satıcı",-5,0.7f,-3);
 NPC npc4=new NPC("Demirci",-5,0.7f,-5);
 NPC npc5=new NPC("Zırhçı",-7,0.7f,-3);
 NPC npc6=new NPC("Silahçı",-7,0.7f,-5);
 NPC npc7=new NPC("Lonca Yöneticisi",5,0.7f,-3);
 NPC npc8=new NPC("Şehir Bekçisi",7,0.7f,-4);
 NPC npc9=new NPC("Köy Gardiyanı",7,0.7f,-6);
 NPC npc10=new NPC("Depocu",5,0.7f,-6);
 NPC npc11=new NPC("İksirci",3,0.7f,-6);

 // Metin2 tarzı başlangıç bölgesi mobları.
 private final Mob mob1 = new Mob(
         "Yabani Köpek", Mob.Type.NORMAL,
         10, -13f, 0.5f, -12f,
         250, 25, 10, 50, 100
 );

 private final Mob mob2 = new Mob(
         "Yabani Köpek", Mob.Type.NORMAL,
         10, -10f, 0.5f, -14f,
         250, 25, 10, 50, 100
 );

 private final Mob mob3 = new Mob(
         "Yabani Köpek", Mob.Type.NORMAL,
         11, -14f, 0.5f, -15f,
         300, 30, 12, 65, 120
 );

 private final Mob mob4 = new Mob(
         "Yabani Köpek", Mob.Type.NORMAL,
         11, -8f, 0.5f, -12f,
         300, 30, 12, 65, 120
 );

 private final Mob[] mobs = {mob1, mob2, mob3, mob4};

 private Mob selectedMob = null;
 private long lastMobAttackTime = 0L;

 int prog,pos,col,mat;
 FloatBuffer ground;
 private void loadCharacterState(){ if(sessionToken==null||sessionToken.isEmpty()) return; new Thread(()->{ ServerClient client=new ServerClient(); if(!client.connect("10.0.2.2",5000)) return; try{ org.json.JSONObject req=new org.json.JSONObject(); req.put("command","GET_CHARACTER_STATE"); req.put("session_token",sessionToken); org.json.JSONObject res=client.request(req); if(res!=null&&res.optBoolean("ok")){ org.json.JSONObject c=res.optJSONObject("character"); if(c!=null){ player.data.name=c.optString("name",player.data.name); player.data.level=c.optInt("level",player.data.level); player.data.yang=c.optLong("yang",player.data.yang); try{ player.data.characterClass=CharacterClass.valueOf(c.optString("class","SAVASCI")); player.data.gender=Gender.valueOf(c.optString("gender","MALE")); }catch(Exception ignored){}  } } }catch(Exception ignored){} finally{client.close();} }).start(); }
 public GameRenderer(Context context){this.context=context;}
 public void rotate(float a,float b){yaw+=a;pitch=Math.max(-.15f,Math.min(.45f,pitch+b));}
 public void move(float dx,float dz){float sx=(float)Math.sin(yaw),cz=(float)Math.cos(yaw);player.move(dx*cz+dz*sx,dx*sx-dz*cz);}

 public String getInteractableNPC(){
     NPC[] npcs = {
         npc1,npc2,npc3,npc4,npc5,npc6,npc7,npc8,npc9,npc10,npc11
     };

     for(NPC npc : npcs){
         if(npc.canInteract(player.x,player.z)) return npc.name;
     }

     return null;
 }

 public Player getPlayer(){
     return player;
 }

 private Mob findNearestMob(){
     Mob nearest = null;
     float best = Float.MAX_VALUE;

     for(Mob mob : mobs){
         if(!mob.isAlive()) continue;

         float d = mob.distanceTo(player.x, player.z);

         if(d < best){
             best = d;
             nearest = mob;
         }
     }

     return nearest;
 }

 public Mob getSelectedMob(){
     return selectedMob;
 }


 public boolean attackNearestMob(){
     Mob target = findNearestMob();

     if(target == null) return false;

     if(target.distanceTo(player.x, player.z) > 3.2f){
         selectedMob = target;
         return false;
     }

     selectedMob = target;

     int damage = target.receiveDamage(
                Math.max(1, player.data.attackPower)
        );

     android.util.Log.d(
             "BORUMT2_COMBAT",
             "Oyuncu -> " + target.name + " hasar=" + damage + " HP=" + target.hp
     );

     if(!target.isAlive()){
         player.data.exp += target.expReward;
         player.data.yang += target.yangReward;

         android.util.Log.d(
                 "BORUMT2_COMBAT",
                 target.name + " öldü | EXP=" + target.expReward
                         + " | Yang=" + target.yangReward
                         + " | Toplam EXP=" + player.data.exp
                         + " | Toplam Yang=" + player.data.yang
         );

         selectedMob = null;
     }

     return true;
 }

 private void updateMobCombat(long now){
     if(selectedMob == null || !selectedMob.isAlive()) return;

     float distance = selectedMob.distanceTo(player.x, player.z);

     // Mob oyuncudan uzaktaysa saldırmasın.
     if(distance > 2.8f) return;

     // Mob saniyede bir kez saldırır.
     if(lastMobAttackTime != 0L && now - lastMobAttackTime < 1000L) return;

     lastMobAttackTime = now;

     int damage = Math.max(
             1,
             selectedMob.attackPower - player.data.defense
     );

     player.data.hp = Math.max(
             0,
             player.data.hp - damage
     );

     android.util.Log.d(
             "BORUMT2_COMBAT",
             selectedMob.name + " -> oyuncu hasar=" + damage
                     + " HP=" + player.data.hp
     );

     if(player.data.hp <= 0){
         player.data.hp = player.data.maxHp;
         player.returnToCity();
         selectedMob = null;

         android.util.Log.d(
                 "BORUMT2_COMBAT",
                 "Oyuncu öldü, şehre döndü."
         );
     }
 }

 public String getPlayerInfo(){
     return player.getDisplayName()
         + " | Lv." + player.getLevel()
         + " | " + player.getCharacterClass()
         + " | Yang: " + player.getYang();
 }

 public String getNPCDialogue(){
     String npc = getInteractableNPC();
     if(npc == null) return null;

     switch(npc){
         case "Ticaret Yöneticisi":
             return "Ticaret Yöneticisi\\n\\nTicaret işlemleri ve özel malzemeler için buradayım.";

         case "Emlakçı":
             return "Emlakçı\\n\\nLonca arsaları ve emlak işlemleri hakkında yardımcı olabilirim.";

         case "Satıcı":
             return "Satıcı\\n\\nİhtiyacın olan temel eşyaları burada bulabilirsin.";

         case "Demirci":
             return "Demirci\\n\\nEşyalarını yükseltmek için hazırım.";

         case "Zırhçı":
             return "Zırhçı\\n\\nSavaş için gerekli zırh ve ekipmanlar burada.";

         case "Silahçı":
             return "Silahçı\\n\\nSavaşçılar için çeşitli silahlarım var.";

         case "Lonca Yöneticisi":
             return "Lonca Yöneticisi\\n\\nLonca kurma ve lonca işlemleri hakkında yardımcı olabilirim.";

         case "Şehir Bekçisi":
             return "Şehir Bekçisi\\n\\nŞehir düzeninden ve şehir girişlerinden sorumluyum.";

         case "Köy Gardiyanı":
             return "Köy Gardiyanı\\n\\nKöy ve çevresi hakkında bilgi verebilirim.";

         case "Depocu":
             return "Depocu\\n\\nEşyalarını güvenle depolayabilirsin.";

         case "İksirci":
             return "İksirci\\n\\nCan ve diğer ihtiyaçların için iksirlerim var.";

         default:
             return npc;
     }
 }
 public void onSurfaceCreated(GL10 g,EGLConfig c){
     GLES20.glClearColor(.52f,.68f,.82f,1);
     GLES20.glEnable(GLES20.GL_DEPTH_TEST);

     String vs="uniform mat4 M;attribute vec3 P;void main(){gl_Position=M*vec4(P,1);}";
     String fs="precision mediump float;uniform vec4 C;void main(){gl_FragColor=C;}";

     int a=sh(GLES20.GL_VERTEX_SHADER,vs);
     int b=sh(GLES20.GL_FRAGMENT_SHADER,fs);

     prog=GLES20.glCreateProgram();
     GLES20.glAttachShader(prog,a);
     GLES20.glAttachShader(prog,b);
     GLES20.glLinkProgram(prog);

     pos=GLES20.glGetAttribLocation(prog,"P");
     col=GLES20.glGetUniformLocation(prog,"C");
     mat=GLES20.glGetUniformLocation(prog,"M");

     float[] q={
         -20,0,-20,
         20,0,-20,
         -20,0,20,
         20,0,-20,
         20,0,20,
         -20,0,20
     };
     ground=buf(q);

     try {
         String modelPath = getCharacterModelPath();
         characterModel = ObjModel.load(context, modelPath);
         android.util.Log.d("BORUMT2_MODEL", "Model yüklendi: " + modelPath
                 + " vertices=" + characterModel.vertexCount);

         mapTerrain = ObjModel.load(context, "map/terrain.obj");
         mapVillage = ObjModel.load(context, "map/village.obj");
         mapForest = ObjModel.load(context, "map/forest.obj");
         mapRocks = ObjModel.load(context, "map/rocks.obj");

         android.util.Log.d("BORUMT2_MAP", "Harita modelleri yüklendi");
     } catch(Exception e) {
         characterModel = null;
         android.util.Log.e("BORUMT2_MODEL", "Model yüklenemedi: "
                 + getCharacterModelPath(), e);
     }
 }
 int sh(int t,String s){int x=GLES20.glCreateShader(t);GLES20.glShaderSource(x,s);GLES20.glCompileShader(x);return x;}
 FloatBuffer buf(float[] a){FloatBuffer b=ByteBuffer.allocateDirect(a.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();b.put(a).position(0);return b;}
 public void onSurfaceChanged(GL10 g,int w,int h){GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(p,0,55,(float)w/h,.1f,100);}
 public void onDrawFrame(GL10 g){
     long now = System.currentTimeMillis();
     float deltaTime = lastFrameTime == 0 ? 0f : (now - lastFrameTime) / 1000000000f;
     lastFrameTime = now;
     deltaTime = Math.min(deltaTime, 0.05f);
     player.update(deltaTime);
     updateMobCombat(now);

     GLES20.glClear(
             GLES20.GL_COLOR_BUFFER_BIT |
             GLES20.GL_DEPTH_BUFFER_BIT
     );

     float cx=(float)Math.sin(yaw)*7;
     float cz=(float)Math.cos(yaw)*7;
     float cy=3.2f+pitch*2;

     Matrix.setLookAtM(
             v,0,
             player.x+cx,cy,player.z+cz,
             player.x,1,player.z,
             0,1,0
     );

     drawMap();
     drawCharacterModel(player.x,0,player.z);
     cube(npc1.x,npc1.y,npc1.z,.5f,.7f,.35f,.55f,.45f,.72f);
     cube(npc2.x,npc2.y,npc2.z,.5f,.7f,.35f,.55f,.45f,.72f);
     cube(npc3.x,npc3.y,npc3.z,.5f,.7f,.35f,.72f,.55f,.30f);
     cube(npc4.x,npc4.y,npc4.z,.5f,.7f,.35f,.45f,.45f,.45f);
     cube(npc5.x,npc5.y,npc5.z,.5f,.7f,.35f,.30f,.40f,.72f);
     cube(npc6.x,npc6.y,npc6.z,.5f,.7f,.35f,.55f,.35f,.20f);
     cube(npc7.x,npc7.y,npc7.z,.5f,.7f,.35f,.55f,.30f,.65f);
     cube(npc8.x,npc8.y,npc8.z,.5f,.7f,.35f,.35f,.35f,.35f);
     cube(npc9.x,npc9.y,npc9.z,.5f,.7f,.35f,.25f,.50f,.30f);
     cube(npc10.x,npc10.y,npc10.z,.5f,.7f,.35f,.55f,.35f,.18f);
     cube(npc11.x,npc11.y,npc11.z,.5f,.7f,.35f,.20f,.55f,.45f);
     // Moblar.
     for(Mob mob : mobs){
         if(!mob.isAlive()) continue;

         float rr = mob.isBoss() ? .65f : .55f;
         float gg = mob.isMetin() ? .55f : .25f;
         float bb = mob.isMetin() ? .20f : .15f;

         cube(
                 mob.x, mob.y, mob.z,
                 .45f,.55f,.45f,
                 rr,gg,bb
         );

         // Seçili mobu biraz daha büyük çizerek hedefi belli et.
         if(mob == selectedMob){
             cube(
                     mob.x, mob.y + .75f, mob.z,
                     .52f,.08f,.52f,
                     1.0f,0.85f,0.10f
             );
         }
     }
 }
 void drawMap(){
     Matrix.setIdentityM(m,0);

     if(mapTerrain!=null)
         draw(mapTerrain.vertices,mapTerrain.vertexCount,0.28f,0.45f,0.25f);

     if(mapVillage!=null)
         draw(mapVillage.vertices,mapVillage.vertexCount,0.48f,0.31f,0.16f);

     if(mapForest!=null)
         draw(mapForest.vertices,mapForest.vertexCount,0.12f,0.38f,0.12f);

     if(mapRocks!=null)
         draw(mapRocks.vertices,mapRocks.vertexCount,0.34f,0.34f,0.34f);
 }

 void ground(){Matrix.setIdentityM(m,0);draw(ground,6,.28f,.45f,.25f);}

 private String getCharacterModelPath(){
     String cls=player.data.characterClass.name().toLowerCase();
     String gender=player.data.gender==Gender.FEMALE ? "kadin" : "erkek";

     return "characters/"+cls+"/"+gender+"/character.obj";
 }

 void drawCharacterModel(float x,float y,float z){
     if(characterModel==null) return;

     Matrix.setIdentityM(m,0);
     Matrix.translateM(m,0,x,y,z);
     Matrix.scaleM(m,0,1.2f,1.2f,1.2f);

     draw(characterModel.vertices,characterModel.vertexCount,
             0.72f,0.48f,0.20f);
 }

 void cube(float x,float y,float z,float sx,float sy,float sz,float rr,float gg,float bb){float[] q={-1,-1,1,1,-1,1,-1,1,1,1,1,1,-1,-1,-1,-1,1,-1,1,-1,-1,1,1,-1};short[] ix={0,1,2,1,3,2,1,6,3,6,7,3,6,4,7,4,5,7,4,0,5,0,2,5,2,3,5,3,7,5,4,6,0,6,1,0};FloatBuffer b=buf(q);ByteBuffer ib=ByteBuffer.allocateDirect(ix.length*2).order(ByteOrder.nativeOrder());ib.asShortBuffer().put(ix).position(0);Matrix.setIdentityM(m,0);Matrix.translateM(m,0,x,y,z);Matrix.scaleM(m,0,sx,sy,sz);draw(b,36,rr,gg,bb,ib);}
 void draw(FloatBuffer b,int n,float r,float gg,float bb){draw(b,n,r,gg,bb,null);} void draw(FloatBuffer b,int n,float r,float gg,float bb,ByteBuffer ib){Matrix.multiplyMM(mv,0,v,m.length>0?0:0,m,0);Matrix.multiplyMM(mv,0,p,0,mv,0);GLES20.glUseProgram(prog);GLES20.glUniformMatrix4fv(mat,1,false,mv,0);GLES20.glUniform4f(col,r,gg,bb,1);GLES20.glEnableVertexAttribArray(pos);GLES20.glVertexAttribPointer(pos,3,GLES20.GL_FLOAT,false,0,b);if(ib==null)GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,n);else GLES20.glDrawElements(GLES20.GL_TRIANGLES,n,GLES20.GL_UNSIGNED_SHORT,ib.asShortBuffer());GLES20.glDisableVertexAttribArray(pos);}
}
