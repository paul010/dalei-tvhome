package io.github.paul010.daleitvhome;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
/** Nullable statistics only. No account identifiers, raw RPC or network operations. */
final class NativeCodexSnapshot {
 static final class Point {final String date;final Long tokens;Point(String date,Long tokens){this.date=date;this.tokens=tokens;}}
 final Double remaining;final Long resetAt,todayTokens,latestTokens,lifetimeTokens;
 final String latestDate,usageTimeZone;final long sourceAt;final boolean stale;final List<Point> points;
 NativeCodexSnapshot(Double remaining,Long resetAt,Long today,Long latest,Long lifetime,String latestDate,String usageTimeZone,long sourceAt,boolean stale,List<Point> points){this.remaining=remaining;this.resetAt=resetAt;todayTokens=today;latestTokens=latest;lifetimeTokens=lifetime;this.latestDate=latestDate;this.usageTimeZone=usageTimeZone;this.sourceAt=sourceAt;this.stale=stale;this.points=Collections.unmodifiableList(points);}
 static NativeCodexSnapshot empty(){return new NativeCodexSnapshot(null,null,null,null,null,null,null,0,true,new ArrayList<Point>());}
 static Long count(Object v){if(!(v instanceof Number))return null;double d=((Number)v).doubleValue();return Double.isNaN(d)||Double.isInfinite(d)||d<0||d>9007199254740991d||d!=Math.floor(d)?null:Long.valueOf((long)d);}
 static Long instant(String s){if(s==null||s.length()!=24)return null;SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);f.setLenient(false);f.setTimeZone(TimeZone.getTimeZone("UTC"));ParsePosition p=new ParsePosition(0);java.util.Date d=f.parse(s,p);return d!=null&&p.getIndex()==s.length()?d.getTime():null;}
 static boolean validDate(String s){if(s==null||!s.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))return false;SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd",Locale.US);f.setLenient(false);ParsePosition p=new ParsePosition(0);return f.parse(s,p)!=null&&p.getIndex()==s.length();}
 static String text(JSONObject o,String key){Object v=o==null?null:o.opt(key);return v instanceof String?(String)v:null;}
 static JSONObject obj(JSONObject o,String key){return o==null?null:o.optJSONObject(key);}
 static Long metric(JSONObject o){return o==null?null:count(o.opt("value"));}
 static NativeCodexSnapshot parse(JSONObject root,long now){
  if(root==null||root.optInt("schemaVersion",-1)!=1)return empty();
  JSONObject source=obj(root,"source"),weekly=obj(root,"weekly"),tokens=obj(root,"tokens"),trend=obj(root,"trend");
  Long observed=instant(text(source,"observedAt"));long at=observed==null?0:observed;
  boolean stale=source==null||source.optBoolean("stale",true)||at<=0||at>now+60000||now-at>120000;
  Long reset=instant(text(weekly,"resetsAt"));Double remaining=null;Object n=weekly==null?null:weekly.opt("remainingPercent");
  if(n instanceof Number&&reset!=null&&reset>now&&weekly.optInt("windowMinutes",-1)==10080){double d=((Number)n).doubleValue();if(!Double.isNaN(d)&&!Double.isInfinite(d)&&d>=0&&d<=100)remaining=d;}
  String zone=text(source,"usageDayTimeZone");JSONObject latest=obj(tokens,"latestDaily");
  List<Point> points=new ArrayList<Point>();JSONArray array=trend==null?null:trend.optJSONArray("points");
  if(trend!=null&&trend.optInt("periodDays",-1)==14&&array!=null)for(int i=0;i<Math.min(14,array.length());i++){JSONObject p=array.optJSONObject(i);String date=text(p,"date");if(validDate(date))points.add(new Point(date,count(p.opt("tokens"))));}
  return new NativeCodexSnapshot(remaining,reset,zone==null?null:metric(obj(tokens,"today")),metric(latest),metric(obj(tokens,"lifetime")),validDate(text(latest,"date"))?text(latest,"date"):null,zone,at,stale,points);
 }
}
