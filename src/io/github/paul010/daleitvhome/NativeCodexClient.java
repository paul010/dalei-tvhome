package io.github.paul010.daleitvhome;
import android.os.Handler;
import android.os.Looper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;
/** One approved read-only JSON resource, never an account collector or RPC. */
final class NativeCodexClient {
 interface Listener{void result(NativeCodexSnapshot snapshot,String json,boolean success);}
 final String endpoint;final Listener listener;final Handler main=new Handler(Looper.getMainLooper());final ExecutorService worker=Executors.newSingleThreadExecutor();
 volatile boolean visible;volatile int generation;volatile HttpURLConnection connection;Future<?> pending;Runnable watchdog;
 NativeCodexClient(String url,Listener listener){endpoint=endpoint(url);this.listener=listener;}
 static String endpoint(String value){return NativeEndpointPolicy.endpoint(value);}
 final Runnable tick=new Runnable(){public void run(){if(!visible||endpoint==null)return;fetch();main.postDelayed(this,60000);}};
 void start(){if(visible||endpoint==null)return;visible=true;generation++;main.post(tick);}
 void stop(){visible=false;generation++;main.removeCallbacks(tick);if(watchdog!=null)main.removeCallbacks(watchdog);if(pending!=null)pending.cancel(true);disconnect(connection);}
 void close(){stop();worker.shutdownNow();}
 static void disconnect(HttpURLConnection c){if(c!=null)try{c.disconnect();}catch(RuntimeException ignored){}}
 void fetch(){
  if(pending!=null&&!pending.isDone())return;final int token=generation;final AtomicBoolean delivered=new AtomicBoolean();
  final Runnable deadline=new Runnable(){public void run(){if(visible&&token==generation&&delivered.compareAndSet(false,true)){disconnect(connection);if(pending!=null)pending.cancel(true);listener.result(null,null,false);}}};watchdog=deadline;main.postDelayed(deadline,12000);
  pending=worker.submit(new Runnable(){public void run(){HttpURLConnection c=null;NativeCodexSnapshot snapshot=null;String text=null;boolean success=false;
   try{
    if(!visible||token!=generation)return;c=(HttpURLConnection)new URL(endpoint).openConnection();connection=c;if(!visible||token!=generation||Thread.currentThread().isInterrupted())return;
    c.setInstanceFollowRedirects(false);c.setConnectTimeout(4000);c.setReadTimeout(4000);c.setUseCaches(false);c.setRequestMethod("GET");
    if(c.getResponseCode()!=200||c.getContentLength()>32768)throw new java.io.IOException("Response rejected");
    try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
     byte[] chunk=new byte[4096];int n;while((n=in.read(chunk))!=-1){if(!visible||token!=generation||Thread.currentThread().isInterrupted())return;if(out.size()+n>32768)throw new java.io.IOException("Response too large");out.write(chunk,0,n);}text=new String(out.toByteArray(),"UTF-8");
    }
    JSONObject root=new JSONObject(text);snapshot=NativeCodexSnapshot.parse(root,System.currentTimeMillis());success=root.optInt("schemaVersion",-1)==1&&snapshot.sourceAt>0;
   }catch(Exception ignored){/* No account content or URLs in logs. */}finally{disconnect(c);if(connection==c)connection=null;}
   final NativeCodexSnapshot result=snapshot;final String json=text;final boolean ok=success;
   main.post(new Runnable(){public void run(){if(visible&&token==generation&&delivered.compareAndSet(false,true)){main.removeCallbacks(deadline);listener.result(result,json,ok);}}});
  }});
 }
}
