package com.lumorixgame.borumt2;
import android.opengl.*; import javax.microedition.khronos.egl.EGLConfig; import javax.microedition.khronos.opengles.GL10; import java.nio.*;
public class GameRenderer implements GLSurfaceView.Renderer {
 float[] p=new float[16],v=new float[16],m=new float[16],mv=new float[16]; float yaw=0,pitch=.12f; float playerX=0,playerZ=0; int prog,pos,col,mat;
 FloatBuffer ground;
 public void rotate(float a,float b){yaw+=a;pitch=Math.max(-.15f,Math.min(.45f,pitch+b));}
 public void move(float dx,float dz){playerX+=dx;playerZ+=dz;}
 public void onSurfaceCreated(GL10 g,EGLConfig c){GLES20.glClearColor(.52f,.68f,.82f,1);GLES20.glEnable(GLES20.GL_DEPTH_TEST);
 String vs="uniform mat4 M;attribute vec3 P;void main(){gl_Position=M*vec4(P,1);}"; String fs="precision mediump float;uniform vec4 C;void main(){gl_FragColor=C;}";
 int a=sh(GLES20.GL_VERTEX_SHADER,vs),b=sh(GLES20.GL_FRAGMENT_SHADER,fs);prog=GLES20.glCreateProgram();GLES20.glAttachShader(prog,a);GLES20.glAttachShader(prog,b);GLES20.glLinkProgram(prog);
 pos=GLES20.glGetAttribLocation(prog,"P");col=GLES20.glGetUniformLocation(prog,"C");mat=GLES20.glGetUniformLocation(prog,"M");
 float[] q={-20,0,-20,20,0,-20,-20,0,20,20,0,-20,20,0,20,-20,0,20};ground=buf(q);}
 int sh(int t,String s){int x=GLES20.glCreateShader(t);GLES20.glShaderSource(x,s);GLES20.glCompileShader(x);return x;}
 FloatBuffer buf(float[] a){FloatBuffer b=ByteBuffer.allocateDirect(a.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();b.put(a).position(0);return b;}
 public void onSurfaceChanged(GL10 g,int w,int h){GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(p,0,55,(float)w/h,.1f,100);}
 public void onDrawFrame(GL10 g){GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);float cx=(float)Math.sin(yaw)*7,cz=(float)Math.cos(yaw)*7,cy=3.2f+pitch*2;Matrix.setLookAtM(v,0,cx,cy,cz,0,1,0,0,1,0);cube(playerX,.75f,playerZ,.55f,.75f,.35f,.75f,.55f,.35f);cube(playerX,1.75f,playerZ,.45f,.45f,.45f,.75f,.62f,.42f);cube(-3,.7f,-3,.5f,.7f,.35f,.55f,.45f,.72f);cube(3,.7f,-4,.5f,.7f,.35f,.55f,.45f,.72f);ground();}
 void ground(){Matrix.setIdentityM(m,0);draw(ground,6,.28f,.45f,.25f);}
 void cube(float x,float y,float z,float sx,float sy,float sz,float rr,float gg,float bb){float[] q={-1,-1,1,1,-1,1,-1,1,1,1,1,1,-1,-1,-1,-1,1,-1,1,-1,-1,1,1,-1};short[] ix={0,1,2,1,3,2,1,6,3,6,7,3,6,4,7,4,5,7,4,0,5,0,2,5,2,3,5,3,7,5,4,6,0,6,1,0};FloatBuffer b=buf(q);ByteBuffer ib=ByteBuffer.allocateDirect(ix.length*2).order(ByteOrder.nativeOrder());ib.asShortBuffer().put(ix).position(0);Matrix.setIdentityM(m,0);Matrix.translateM(m,0,x,y,z);Matrix.scaleM(m,0,sx,sy,sz);draw(b,36,rr,gg,bb,ib);}
 void draw(FloatBuffer b,int n,float r,float gg,float bb){draw(b,n,r,gg,bb,null);} void draw(FloatBuffer b,int n,float r,float gg,float bb,ByteBuffer ib){Matrix.multiplyMM(mv,0,v,m.length>0?0:0,m,0);Matrix.multiplyMM(mv,0,p,0,mv,0);GLES20.glUseProgram(prog);GLES20.glUniformMatrix4fv(mat,1,false,mv,0);GLES20.glUniform4f(col,r,gg,bb,1);GLES20.glEnableVertexAttribArray(pos);GLES20.glVertexAttribPointer(pos,3,GLES20.GL_FLOAT,false,0,b);if(ib==null)GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,n);else GLES20.glDrawElements(GLES20.GL_TRIANGLES,n,GLES20.GL_UNSIGNED_SHORT,ib.asShortBuffer());GLES20.glDisableVertexAttribArray(pos);}
}
