package io.github.paul010.daleitvhome;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Pure Java navigation logic; contains no device identifiers or Android calls. */
final class NavigationState {
    enum Screen { HOME, MEDIA, WORKBENCH, APPS, SETTINGS, PICK_VIDEO }
    private static final class Frame {
        final Screen screen; final String key; final int index, first;
        Frame(Screen screen, String key, int index, int first) {
            this.screen=screen; this.key=key; this.index=index; this.first=first;
        }
    }
    final int visibleRows;
    Screen screen=Screen.HOME;
    int index=0, first=0;
    private String selectedKey="";
    private final List<String> keys=new ArrayList<String>();
    private final Deque<Frame> history=new ArrayDeque<Frame>();
    NavigationState(int visibleRows) {
        if (visibleRows<1) throw new IllegalArgumentException("visibleRows");
        this.visibleRows=visibleRows;
    }
    void setItems(List<String> items) {
        keys.clear(); keys.addAll(items);
        int existing=keys.indexOf(selectedKey);
        index=existing>=0 ? existing : Math.min(index,Math.max(0,keys.size()-1));
        sync();
    }
    void move(int delta) {
        index=Math.max(0,Math.min(keys.size()-1,index+delta)); sync();
    }
    void select(int next) { index=next; sync(); }
    void open(Screen next) {
        history.push(new Frame(screen,selectedKey,index,first));
        screen=next; index=0; first=0; selectedKey=""; keys.clear();
    }
    boolean back() {
        if(history.isEmpty()) return false;
        Frame previous=history.pop(); screen=previous.screen;
        selectedKey=previous.key; index=previous.index; first=previous.first;
        keys.clear(); return true;
    }
    int size() { return keys.size(); }
    private void sync() {
        index=Math.max(0,Math.min(index,Math.max(0,keys.size()-1)));
        selectedKey=keys.isEmpty()?"":keys.get(index);
        if(index<first) first=index;
        if(index>=first+visibleRows) first=index-visibleRows+1;
        first=Math.max(0,Math.min(first,Math.max(0,keys.size()-visibleRows)));
    }
}
