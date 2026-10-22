package io.github.paul010.daleitvhome;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/** Offline companion UI. No HOME registration, service, storage or network access. */
public final class MainActivity extends Activity {
    private HomeView home;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        home=new HomeView();setContentView(home);home.requestFocus();
    }
    @Override public void onBackPressed() { if(!home.back())finish(); }
    private static final class Entry {
        final String key,label,action;final Intent launch;
        Entry(String key,String label,String action,Intent launch){
            this.key=key;this.label=label;this.action=action;this.launch=launch;
        }
    }
    private final class HomeView extends View {
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        final TextPaint text=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        final NavigationState nav=new NavigationState(5);
        final List<Entry> entries=new ArrayList<Entry>();
        final Bitmap wallpaper;
        String notice="";
        final android.content.SharedPreferences preferences=getSharedPreferences("home-preferences",MODE_PRIVATE);
        long focusAt=0;
        float downX,downY;
        HomeView(){
            super(MainActivity.this);setFocusable(true);setFocusableInTouchMode(true);
            Bitmap image=null;
            try(InputStream input=getAssets().open("local-wallpaper.png")){
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.RGB_565;
                image=BitmapFactory.decodeStream(input,null,options);
            }catch(IOException ignored){/* Optional, ignored local overlay; no external storage read. */}
            wallpaper=image;
            rebuild();
        }
        void rebuild(){
            entries.clear();
            switch(nav.screen){
                case HOME:
                    entry("home","主页","welcome");entry("video","视频 · "+videoLabel(),"media");
                    entry("workbench","Codex 工作台","workbench");
                    entry("apps","应用","apps");entry("settings","设置","settings");break;
                case MEDIA:
                    entry("open-video","打开 · "+videoLabel()+" · 首选","video");
                    entry("video-home","SmartTube 主页 · 外部页面","smart-home");
                    entry("video-subscriptions","SmartTube 订阅 · 外部页面","smart-subscriptions");
                    entry("video-search","SmartTube 搜索 · 外部页面","smart-search");
                    entry("video-help","返回桌面的方法","video-help");break;
                case WORKBENCH:
                    entry("weekly","每周剩余 / 重置 · 未接入","dashboard-status");
                    entry("today","今日 Token · 未接入","dashboard-status");
                    entry("month","30 天 Token / 趋势 · 未接入","dashboard-status");
                    entry("updated","更新时间 · 尚未同步","dashboard-status");
                    entry("estimate","目录价等值 · 非订阅账单","dashboard-estimate");break;
                case APPS:loadApps();break;
                case PICK_VIDEO:loadApps();break;
                case SETTINGS:
                    entry("choose-video","选择视频入口","pick-video");
                    entry("wallpaper","本地壁纸说明","wallpaper");
                    entry("help","遥控使用说明","help");entry("exit","退出附加桌面","exit");break;
                default:break;
            }
            List<String> keys=new ArrayList<String>();for(Entry e:entries)keys.add(e.key);
            nav.setItems(keys);changed();
        }
        void entry(String key,String label,String action){entries.add(new Entry(key,label,action,null));}
        void changed(){
            focusAt=SystemClock.uptimeMillis();
            setContentDescription(title()+"，"+(entries.isEmpty()?"列表为空":entries.get(nav.index).label)+"，上下选择，确认进入，返回上一级");
            invalidate();
        }
        void open(NavigationState.Screen screen){nav.open(screen);notice="";rebuild();}
        boolean back(){if(!nav.back())return false;notice="";rebuild();return true;}
        String title(){
            switch(nav.screen){case MEDIA:return "影音时光";case APPS:return "我的应用";
                case WORKBENCH:return "Codex 工作台";case SETTINGS:return "桌面设置";case PICK_VIDEO:return "选择视频入口";default:return "回家，好好放松。";}
        }
        void loadApps(){
            PackageManager pm=getPackageManager();HashSet<String> seen=new HashSet<String>();
            for(String category:new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER,Intent.CATEGORY_LAUNCHER}){
                Intent query=new Intent(Intent.ACTION_MAIN);query.addCategory(category);
                for(ResolveInfo resolved:pm.queryIntentActivities(query,0)){
                    ActivityInfo info=resolved.activityInfo;
                    if(info==null||!info.exported||!info.enabled||!info.applicationInfo.enabled
                            ||info.packageName.equals(getPackageName()))continue;
                    ComponentName component=new ComponentName(info.packageName,info.name);
                    String key=component.flattenToString();if(!seen.add(key))continue;
                    Intent launch=new Intent(Intent.ACTION_MAIN).addCategory(category).setComponent(component);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    entries.add(new Entry(key,resolved.loadLabel(pm).toString(),nav.screen==NavigationState.Screen.PICK_VIDEO?"pin-video":"launch",launch));
                }
            }
            final Collator collator=Collator.getInstance(Locale.getDefault());
            Collections.sort(entries,new Comparator<Entry>(){
                @Override public int compare(Entry a,Entry b){int n=collator.compare(a.label,b.label);return n!=0?n:a.key.compareTo(b.key);}
            });
            if(entries.isEmpty())entry("empty","暂无可启动应用","empty");
        }
        String videoLabel(){
            String value=preferences.getString("video-component","");
            ComponentName component=ComponentName.unflattenFromString(value);
            if(component==null)return "选择应用";
            try{return getPackageManager().getActivityInfo(component,0).loadLabel(getPackageManager()).toString();}
            catch(PackageManager.NameNotFoundException ex){return "重新选择";}
        }
        boolean available(Intent launch){
            if(launch==null||launch.getComponent()==null)return false;
            try{
                ActivityInfo info=getPackageManager().getActivityInfo(launch.getComponent(),0);
                return info.enabled&&info.exported&&info.applicationInfo.enabled;
            }catch(PackageManager.NameNotFoundException ex){return false;}
        }
        void openVideo(){
            ComponentName component=ComponentName.unflattenFromString(preferences.getString("video-component",""));
            if(component==null){open(NavigationState.Screen.PICK_VIDEO);notice="选择只保存在本机；选中后不会自动播放。";return;}
            Intent launch=new Intent(Intent.ACTION_MAIN).setComponent(component).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(!available(launch)){open(NavigationState.Screen.APPS);notice="原视频入口不可用，请从已安装应用重新选择。";return;}
            try{startActivity(launch);}catch(RuntimeException ex){open(NavigationState.Screen.APPS);notice="视频应用未能打开，请重新选择。";}
        }
        void openSmartSection(String section){
            ComponentName selected=ComponentName.unflattenFromString(preferences.getString("video-component",""));
            if(selected==null||!"org.smarttube.stable".equals(selected.getPackageName())){
                openVideo();return;
            }
            String activity="com.liskovsoft.smartyoutubetv2.tv.launchers."+section+"LauncherActivity";
            Intent launch=new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(selected.getPackageName(),activity)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(!available(launch)){openVideo();return;}
            try{startActivity(launch);}catch(RuntimeException ex){openVideo();}
        }
        void activate(){
            if(entries.isEmpty())return;
            Entry e=entries.get(nav.index);notice="";
            if("video".equals(e.action))openVideo();
            else if("smart-home".equals(e.action))openSmartSection("Home");
            else if("smart-subscriptions".equals(e.action))openSmartSection("Subscriptions");
            else if("smart-search".equals(e.action))openSmartSection("Search");
            else if("pick-video".equals(e.action))open(NavigationState.Screen.PICK_VIDEO);
            else if("pin-video".equals(e.action)){
                if(!available(e.launch)){rebuild();notice="应用已变化，请重新选择。";}
                else {preferences.edit().putString("video-component",e.key).apply();back();notice="视频入口已保存；未启动或播放内容。";rebuild();}
            }
            else if("media".equals(e.action))open(NavigationState.Screen.MEDIA);
            else if("workbench".equals(e.action))open(NavigationState.Screen.WORKBENCH);
            else if("video-help".equals(e.action))notice="首选标准入口。先返回侧栏，再快速返回两次。\n外部页面可能回到系统桌面；返回路径由播放器决定。";
            else if("video-data".equals(e.action))notice="尚未接入受支持的内容接口。\n此处不显示虚构封面或订阅流。";
            else if("dashboard-status".equals(e.action))notice="Codex 数据未连接；不使用截图中的静态数值。";
            else if("dashboard-estimate".equals(e.action))notice="按 Token 与目录价估算，不是实际订阅账单。\n尚未接入，不显示金额。";
            else if("dashboard-privacy".equals(e.action))notice="仅计划复用既有脱敏状态；尚未联网。\n不读取凭据、会话存储或终端内容。";
            else if("apps".equals(e.action))open(NavigationState.Screen.APPS);
            else if("settings".equals(e.action))open(NavigationState.Screen.SETTINGS);
            else if("exit".equals(e.action))finish();
            else if("launch".equals(e.action)){
                try{
                    ActivityInfo info=getPackageManager().getActivityInfo(e.launch.getComponent(),0);
                    if(!info.enabled||!info.exported||!info.applicationInfo.enabled)
                        throw new IllegalStateException("Unavailable activity");
                    startActivity(e.launch);
                }catch(PackageManager.NameNotFoundException ex){rebuild();notice="应用已变化，请重新选择。";}
                catch(RuntimeException ex){notice="暂时无法打开该应用，请返回重试。";}
            }else if("help".equals(e.action))notice="上下选择，确认进入。返回保留上级焦点。\n首页再次返回会退出，不改变系统桌面。";
            else if("wallpaper".equals(e.action))notice="可在本地构建放入 local-wallpaper.png。\n不会读取或更改系统壁纸。";
            else if("pending".equals(e.action))notice="此功能尚未适配。请使用原系统入口。";
            else if("empty".equals(e.action))notice="未找到当前可启动的已安装应用。";
            else notice="愿每一次回家，都有好心情。";
            invalidate();
        }
        void label(Canvas c,String value,float x,float baseline,float size,int color,float width){
            text.setShader(null);text.setTextSize(size);text.setColor(color);
            c.drawText(TextUtils.ellipsize(value,text,width,TextUtils.TruncateAt.END).toString(),x,baseline,text);
        }
        void button(Canvas c,Entry entry,int index,float y){
            boolean active=nav.index==index;float x=96,width=nav.screen==NavigationState.Screen.HOME?402:690;
            float scale=active?1f+.035f*Math.min(1f,(SystemClock.uptimeMillis()-focusAt)/160f):1f;
            c.save();c.scale(scale,scale,x+width/2,y+36);
            paint.setShader(null);paint.setColor(active?0xE0224B72:0x9B081728);
            RectF box=new RectF(x,y,x+width,y+72);c.drawRoundRect(box,10,10,paint);
            if(active){
                paint.setColor(0xFFF2FAFF);paint.setStrokeWidth(3);paint.setStyle(Paint.Style.STROKE);
                c.drawRoundRect(box,10,10,paint);paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xFFB4E6FF);c.drawRoundRect(new RectF(x-9,y+19,x-4,y+53),2,2,paint);
            }
            label(c,entry.label,x+25,y+47,30,active?Color.WHITE:0xFFD0DCE9,width-50);
            c.restore();
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);float w=getWidth(),h=getHeight();
            if(wallpaper!=null){
                float scale=Math.max(w/wallpaper.getWidth(),h/wallpaper.getHeight());
                float pw=wallpaper.getWidth()*scale,ph=wallpaper.getHeight()*scale;
                c.drawBitmap(wallpaper,null,new RectF((w-pw)/2,(h-ph)/2,(w+pw)/2,(h+ph)/2),null);
            }else{
                paint.setShader(new LinearGradient(0,h,w,0,new int[]{0xFF06101D,0xFF173C58,0xFF416578},null,Shader.TileMode.CLAMP));
                c.drawRect(0,0,w,h,paint);paint.setShader(null);
            }
            c.save();c.scale(w/1920,h/1080);
            paint.setShader(new LinearGradient(0,0,1040,0,new int[]{0xE0020B1B,0xB0041426,0x00041426},new float[]{0,.52f,1},Shader.TileMode.CLAMP));
            c.drawRect(0,0,1040,1080,paint);paint.setShader(null);
            label(c,"DaLei TV Home",96,100,26,Color.WHITE,700);
            label(c,"TV companion · 0.3",96,133,18,0xFFBECCDD,700);
            if(nav.screen==NavigationState.Screen.HOME){
                paint.setColor(0xB0081728);c.drawRoundRect(new RectF(96,151,786,202),10,10,paint);
                label(c,"Codex 数据未连接 · 工作台查看详情",116,185,24,0xFFE1EDF9,650);
            }
            float top;
            if(nav.screen==NavigationState.Screen.HOME){
                label(c,"回家，好好放松。",96,242,44,Color.WHITE,700);
                paint.setColor(0xB0081728);c.drawRoundRect(new RectF(96,270,786,362),12,12,paint);
                label(c,"热门与新片",120,308,27,Color.WHITE,640);
                label(c,"尚未接入真实内容源 · 不自动播放",120,340,20,0xFFBECCDD,640);top=390;
            }else{
                label(c,"‹ 返回上一级",96,232,24,0xFFD5E5F4,700);
                label(c,title(),96,305,50,Color.WHITE,700);top=365;
            }
            for(int i=nav.first;i<Math.min(entries.size(),nav.first+nav.visibleRows);i++)button(c,entries.get(i),i,top+(i-nav.first)*86);
            if(nav.screen!=NavigationState.Screen.HOME&&entries.size()>nav.visibleRows){
                float track=nav.visibleRows*86-14,thumb=Math.max(24,track*nav.visibleRows/entries.size());
                float position=(track-thumb)*nav.first/(entries.size()-nav.visibleRows);
                paint.setColor(0x40D1E5F9);c.drawRoundRect(new RectF(814,top,817,top+track),2,2,paint);
                paint.setColor(0xFFCAE9FF);c.drawRoundRect(new RectF(812,top+position,819,top+position+thumb),3,3,paint);
                label(c,(nav.index+1)+" / "+entries.size(),96,top+track+32,18,0xFFBECCDD,700);
            }
            label(c,"↑ ↓ 选择    确认 进入    ← / 返回 上一级",96,855,20,0xFFBED0E2,740);
            String[] lines=notice.split("\n");for(int i=0;i<Math.min(2,lines.length);i++)label(c,lines[i],96,900+i*29,21,Color.WHITE,740);
            label(c,new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),96,1000,38,Color.WHITE,700);
            c.restore();if(SystemClock.uptimeMillis()-focusAt<160)postInvalidateDelayed(16);else postInvalidateDelayed(30000);
        }
        @Override public boolean onKeyDown(int key,KeyEvent event){
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){nav.move(key==KeyEvent.KEYCODE_DPAD_DOWN?1:-1);changed();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER||key==KeyEvent.KEYCODE_NUMPAD_ENTER){if(event.getRepeatCount()==0)activate();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT&&nav.screen==NavigationState.Screen.HOME){if(event.getRepeatCount()==0)activate();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_ESCAPE){if(event.getRepeatCount()==0&&!back())finish();return true;}
            return super.onKeyDown(key,event);
        }
        @Override public boolean onTouchEvent(MotionEvent e){
            float x=e.getX()*1920/getWidth(),y=e.getY()*1080/getHeight();
            if(e.getAction()==MotionEvent.ACTION_DOWN){downX=x;downY=y;return true;}
            if(e.getAction()!=MotionEvent.ACTION_UP)return true;
            if(nav.screen!=NavigationState.Screen.HOME&&Math.abs(y-downY)>55){nav.move(y<downY?nav.visibleRows:-nav.visibleRows);changed();return true;}
            if(Math.abs(x-downX)>35||Math.abs(y-downY)>35)return true;
            if(nav.screen!=NavigationState.Screen.HOME&&x>=70&&x<=490&&y>=190&&y<=255){back();return true;}
            float top=nav.screen==NavigationState.Screen.HOME?390:365;
            int slot=(int)((y-top)/86),index=nav.first+slot;
            float right=nav.screen==NavigationState.Screen.HOME?498:786;
            if(x>=96&&x<=right&&y>=top&&slot>=0&&slot<nav.visibleRows&&index<entries.size()&&y-top-slot*86<=72){nav.select(index);changed();activate();}
            return true;
        }
    }
}
