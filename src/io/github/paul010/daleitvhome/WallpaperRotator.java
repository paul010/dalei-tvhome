package io.github.paul010.daleitvhome;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/** Optional local assets only. One decode at a time; caller owns current/previous frames. */
final class WallpaperRotator {
 interface Listener{void image(Bitmap bitmap);}
 final AssetManager assets;final Listener listener;final Handler main=new Handler(Looper.getMainLooper());
 final ExecutorService worker=Executors.newSingleThreadExecutor();final ArrayList<String> files=new ArrayList<String>();
 volatile int generation;volatile boolean active;int index=0;long lastAction=0;Future<?> pending;
 WallpaperRotator(AssetManager assets,Listener listener){this.assets=assets;this.listener=listener;try{for(String n:assets.list(""))if(n.matches("wallpaper-[0-9][0-9]\\.png"))files.add(n);Collections.sort(files);}catch(Exception ignored){}}
 final Runnable tick=new Runnable(){public void run(){if(!active||files.isEmpty())return;if(SystemClock.uptimeMillis()-lastAction<8000){main.postDelayed(this,5000);return;}load();main.postDelayed(this,45000);}};
 void action(){lastAction=SystemClock.uptimeMillis();}
 void start(){if(active||files.isEmpty())return;active=true;generation++;main.post(tick);}
 void stop(){active=false;generation++;main.removeCallbacks(tick);if(pending!=null)pending.cancel(true);}
 void close(){stop();worker.shutdownNow();}
 void load(){if(pending!=null&&!pending.isDone())return;final int token=generation;final String file=files.get(index);
  pending=worker.submit(new Runnable(){public void run(){Bitmap image=null;
   try{BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;try(InputStream in=assets.open(file)){BitmapFactory.decodeStream(in,null,o);}o.inSampleSize=DashboardPolicy.sample(o.outWidth,o.outHeight);o.inJustDecodeBounds=false;o.inPreferredConfig=Bitmap.Config.RGB_565;try(InputStream in=assets.open(file)){image=BitmapFactory.decodeStream(in,null,o);}}catch(Exception ignored){}
   final Bitmap result=image;main.post(new Runnable(){public void run(){if(active&&token==generation&&result!=null&&SystemClock.uptimeMillis()-lastAction>=8000){index=(index+1)%files.size();listener.image(result);}}});
  }});
 }
}
