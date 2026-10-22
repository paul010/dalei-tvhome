package io.github.paul010.daleitvhome;
public final class DashboardPolicyTest {
 static void check(boolean v){if(!v)throw new AssertionError();}
 public static void main(String[] args){
  check(DashboardPolicy.endpoint("http://localhost:8787/dash.png")!=null);
  for(String s:new String[]{"https://localhost:8787/dash.png","http://localhost:8787/api/usage","http://localhost:8787/dash.png?x=1","http://user:pass@localhost:8787/dash.png","http://localhost:8788/dash.png","http://localhost:8787/dash.png#x","http://localhost:8787/a/../dash.png"})check(DashboardPolicy.endpoint(s)==null);
  check(DashboardPolicy.sample(600,800)==1);check(DashboardPolicy.sample(2048,2048)==2);
  for(int[] d:new int[][]{{0,1},{-1,100},{8193,1},{8000,8000}}){boolean bad=false;try{DashboardPolicy.sample(d[0],d[1]);}catch(IllegalArgumentException e){bad=true;}check(bad);}
  check(DashboardPolicy.stale(0,100));check(!DashboardPolicy.stale(100000,219999));check(DashboardPolicy.stale(100000,220001));
  System.out.println("PASS: fixed endpoint, credentials/query/path rejection, image budgets, expiry");
 }
}
