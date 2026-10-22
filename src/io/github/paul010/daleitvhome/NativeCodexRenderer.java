package io.github.paul010.daleitvhome;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
/** Draws native text, quota bar and date-bucket bars; never renders a dashboard screenshot. */
final class NativeCodexRenderer {
 final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
 void text(Canvas c,String s,float x,float y,float size,int color){p.setColor(color);p.setTextSize(size);c.drawText(s,x,y,p);}
 void box(Canvas c,float x,float y,float w,float h){p.setColor(0xCE081728);c.drawRoundRect(new RectF(x,y,x+w,y+h),12,12,p);}
 String tokens(Long v){if(v==null)return "—";if(v>=1000000000L)return String.format(Locale.US,"%.2fB",v/1e9);if(v>=1000000L)return String.format(Locale.US,"%.1fM",v/1e6);if(v>=1000L)return String.format(Locale.US,"%.1fK",v/1e3);return String.valueOf(v);}
 String clock(Long v){if(v==null||v<=0)return "—";SimpleDateFormat f=new SimpleDateFormat("MM-dd HH:mm",Locale.US);f.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));return f.format(new Date(v));}
 void draw(Canvas c,NativeCodexSnapshot s){
  boolean stale=s.stale||s.sourceAt<=0||System.currentTimeMillis()-s.sourceAt>120000;
  boolean validQuota=s.remaining!=null&&s.resetAt!=null&&s.resetAt>System.currentTimeMillis();
  int ink=Color.WHITE,muted=0xFFC8D8E8,accent=stale?0xFFFFD18A:0xFFB4E6FF;
  text(c,"‹ 返回主页",96,176,24,muted);text(c,"Codex",96,233,46,ink);
  text(c,s.sourceAt==0?"结构化数据尚未接入":stale?"缓存已过期 · 非实时额度":"看板统计",96,272,23,accent);
  box(c,96,302,690,182);text(c,"每周剩余",120,340,25,muted);
  text(c,!validQuota?"—":String.format(Locale.US,"%.0f%%",s.remaining),120,403,56,ink);
  text(c,"重置 "+clock(s.resetAt),400,398,23,muted);
  p.setColor(0xFF35475B);c.drawRoundRect(new RectF(120,434,760,449),7,7,p);
  if(validQuota){p.setColor(accent);c.drawRoundRect(new RectF(120,434,120+640*s.remaining.floatValue()/100,449),7,7,p);}
  box(c,96,502,332,153);box(c,446,502,340,153);
  text(c,"今日 Token",120,539,24,muted);text(c,tokens(s.todayTokens),120,596,42,ink);
  text(c,s.usageTimeZone==null?"源日界未核，暂不合计":"按源日界统计",120,631,19,muted);
  text(c,"最近记录日",470,539,24,muted);text(c,tokens(s.latestTokens),470,596,42,ink);text(c,s.latestDate==null?"暂无日记录":s.latestDate,470,631,20,muted);
  box(c,96,675,690,218);int known=0;long peak=0;for(NativeCodexSnapshot.Point point:s.points)if(point.tokens!=null){known++;peak=Math.max(peak,point.tokens);}
  text(c,"近14个日期 · "+known+"日有记录",120,714,25,muted);
  for(int i=0;i<14;i++){float x=122+i*45;Long n=i<s.points.size()?s.points.get(i).tokens:null;
   if(n==null){text(c,"—",x,827,21,muted);}else if(n==0){text(c,"0",x,827,21,muted);}else{p.setColor(accent);float h=peak>0?(float)Math.max(2,n.doubleValue()/peak*85f):0;c.drawRoundRect(new RectF(x,829-h,x+27,829),3,3,p);}}
  text(c,"缺失不等于0 · 30天统计待完整日界与覆盖",120,870,20,muted);
  text(c,"累计 Token  "+tokens(s.lifetimeTokens),96,940,25,ink);
  text(c,"看板采样 "+clock(s.sourceAt)+" · 上海时间",96,979,20,muted);
  text(c,"采样时间不保证上游事件最新；未读取账户或会话",96,1016,19,muted);
 }
}
