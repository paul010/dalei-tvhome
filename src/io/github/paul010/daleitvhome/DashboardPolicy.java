package io.github.paul010.daleitvhome;
import java.net.URI;
/** Fixed-resource policy. No credentials, query parameters, redirects or arbitrary paths. */
final class DashboardPolicy {
 static final int MAX_BYTES=1024*1024;
 static String endpoint(String value) {
  try { URI u=new URI(value);
   if(!"http".equals(u.getScheme())||u.getHost()==null||u.getPort()!=8787||!"/dash.png".equals(u.getPath())||u.getRawQuery()!=null||u.getRawFragment()!=null||u.getRawUserInfo()!=null) return null;
   return u.toASCIIString();
  }catch(Exception ex){return null;}
 }
 static int sample(int w,int h){
  if(w<=0||h<=0||w>8192||h>8192||(long)w*h>8000000)throw new IllegalArgumentException("Image dimensions rejected");
  int n=1;while(Math.max(w,h)/n>1280)n*=2;return n;
 }
 static boolean stale(long fetched,long now){return fetched<=0||now-fetched>120000;}
}
