package io.github.paul010.daleitvhome;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public final class NavigationStateTest {
    private static void check(boolean value,String message) {
        if(!value) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        NavigationState n=new NavigationState(5);
        n.setItems(Arrays.asList("home","media","apps","settings")); n.move(1);
        n.open(NavigationState.Screen.MEDIA); n.setItems(Arrays.asList("all","pending"));
        n.open(NavigationState.Screen.APPS);
        List<String> apps=new ArrayList<String>();
        for(int i=0;i<80;i++) apps.add("app-"+i);
        n.setItems(apps);
        for(int i=0;i<70;i++) n.move(1);
        check(n.index==70 && n.first==66,"long list keeps focused row visible");
        n.move(1000); check(n.index==79 && n.first==75,"lower boundary clamps");
        n.move(-1000); check(n.index==0 && n.first==0,"upper boundary clamps");
        n.select(40); apps.add(0,"new-app"); n.setItems(apps);
        check(n.index==41,"stable component key survives insertion");
        apps.remove("app-40"); n.setItems(apps);
        check(n.index>=0 && n.index<n.size(),"removed entry falls back safely");
        check(n.back() && n.screen==NavigationState.Screen.MEDIA,"nested return restores parent");
        n.setItems(Arrays.asList("all","pending"));
        check(n.index==0,"parent focus restored");
        check(n.back() && n.screen==NavigationState.Screen.HOME,"return to home");
        n.setItems(Arrays.asList("home","media","apps","settings"));
        check(n.index==1,"home menu focus restored");
        check(!n.back(),"home back exits rather than inventing another screen");
        n.open(NavigationState.Screen.APPS);n.setItems(new ArrayList<String>());n.move(-1);n.move(1);
        check(n.index==0 && n.first==0 && n.size()==0,"empty list safe");
        NavigationState work=new NavigationState(5);
        work.setItems(Arrays.asList("home","video","workbench","apps","settings"));work.select(2);
        work.open(NavigationState.Screen.WORKBENCH);work.setItems(Arrays.asList("connection","usage","tasks","privacy"));work.select(2);
        check(work.back(),"workbench returns");work.setItems(Arrays.asList("home","video","workbench","apps","settings"));
        check(work.index==2,"workbench parent focus restored");
        System.out.println("PASS: long-list bounds, stable identity, nested return, focus restore, empty state");
    }
}
