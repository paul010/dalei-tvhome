package io.github.paul010.daleitvhome;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/** GETs one configured PNG only while the caller is visible. No collector endpoint. */
final class DashboardClient {
 interface Listener { void result(Bitmap image,boolean success,long fetched); }
 final String endpoint;final Listener listener;final Handler main=new Handler(Looper.getMainLooper());
 final ExecutorService worker=Executors.newSingleThreadExecutor();
 volatile boolean visible;volatile int generation;volatile HttpURLConnection connection;Future<?> pending;Runnable watchdog;
 DashboardClient(String endpoint,Listener listener){this.endpoint=DashboardPolicy.endpoint(endpoint);this.listener=listener;}
 final Runnable tick=new Runnable(){public void run(){if(!visible||endpoint==null)return;fetch();main.postDelayed(this,60000);}};
 void start(){if(visible||endpoint==null)return;visible=true;generation++;main.post(tick);}
 void stop(){visible=false;generation++;main.removeCallbacks(tick);if(watchdog!=null)main.removeCallbacks(watchdog);if(pending!=null)pending.cancel(true);HttpURLConnection c=connection;if(c!=null)c.disconnect();}
 void close(){stop();worker.shutdownNow();}
 void fetch(){
  if(pending!=null&&!pending.isDone())return;final int request=generation;
  final java.util.concurrent.atomic.AtomicBoolean delivered=new java.util.concurrent.atomic.AtomicBoolean();
  final Runnable deadline=new Runnable(){public void run(){if(visible&&request==generation&&delivered.compareAndSet(false,true)){HttpURLConnection c=connection;if(c!=null)c.disconnect();if(pending!=null)pending.cancel(true);listener.result(null,false,System.currentTimeMillis());}}};
  watchdog=deadline;main.postDelayed(deadline,12000);
  pending=worker.submit(new Runnable(){public void run(){Bitmap decoded=null;boolean success=false;HttpURLConnection c=null;
   try{
    if(!visible||request!=generation)return;
    c=(HttpURLConnection)new URL(endpoint).openConnection();connection=c;
    if(!visible||request!=generation||Thread.currentThread().isInterrupted()){c.disconnect();return;}
    c.setInstanceFollowRedirects(false);c.setConnectTimeout(4000);c.setReadTimeout(4000);c.setUseCaches(false);c.setRequestMethod("GET");
    if(c.getResponseCode()!=200||c.getContentLength()>DashboardPolicy.MAX_BYTES)throw new java.io.IOException("Response rejected");
    byte[] bytes;
    try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
     byte[] chunk=new byte[8192];int n;
     while((n=in.read(chunk))!=-1){if(!visible||request!=generation||Thread.currentThread().isInterrupted())return;if(out.size()+n>DashboardPolicy.MAX_BYTES)throw new java.io.IOException("Too large");out.write(chunk,0,n);}
     bytes=out.toByteArray();
    }
    byte[] magic={(byte)137,80,78,71,13,10,26,10};if(bytes.length<8)throw new java.io.IOException("Not PNG");for(int i=0;i<8;i++)if(bytes[i]!=magic[i])throw new java.io.IOException("Not PNG");
    BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
    options.inSampleSize=DashboardPolicy.sample(options.outWidth,options.outHeight);options.inJustDecodeBounds=false;options.inPreferredConfig=Bitmap.Config.RGB_565;
    decoded=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);success=decoded!=null;
   }catch(Exception ex){/* Do not log URLs or account content. */}finally{if(c!=null)c.disconnect();if(connection==c)connection=null;}
   final Bitmap result=decoded;final boolean ok=success;final long fetched=System.currentTimeMillis();
   main.post(new Runnable(){public void run(){if(visible&&request==generation&&delivered.compareAndSet(false,true)){main.removeCallbacks(deadline);listener.result(result,ok,fetched);}}});
  }});
 }
}
