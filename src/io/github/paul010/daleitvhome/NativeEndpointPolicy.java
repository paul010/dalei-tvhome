package io.github.paul010.daleitvhome;
import java.net.URI;
final class NativeEndpointPolicy {
 static String endpoint(String value){try{URI u=new URI(value);return "http".equals(u.getScheme())&&u.getHost()!=null&&u.getPort()==8787&&"/api/tv-codex".equals(u.getRawPath())&&u.getRawQuery()==null&&u.getRawFragment()==null&&u.getRawUserInfo()==null?u.toASCIIString():null;}catch(Exception e){return null;}}
}
