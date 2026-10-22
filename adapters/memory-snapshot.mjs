// Pure whitelist transform only. Never imports collectors, filesystem, credentials or RPC.
const DAY=86400000;
const safeCount=x=>Number.isSafeInteger(x)&&x>=0?x:null;
const iso=x=>Number.isFinite(x)?new Date(x).toISOString():null;
function validDate(s){return typeof s==='string'&&/^\d{4}-\d{2}-\d{2}$/.test(s)&&Number.isFinite(Date.parse(s))&&new Date(s).toISOString().slice(0,10)===s;}
function shanghaiDate(ms){return new Date(ms+8*3600000).toISOString().slice(0,10);}
export function memorySnapshot(data,observedAt,now=Date.now()){
 const stamp=Number.isFinite(observedAt)&&observedAt>0&&observedAt<=now+60000?observedAt:null;
 const stale=stamp===null||now-stamp>120000||!!data?.partial;
 const main=data?.rateLimitsByLimitId?.codex||data?.rateLimits;
 const window=[main?.primary,main?.secondary].find(w=>w?.windowDurationMins===10080);
 const validWindow=typeof window?.usedPercent==='number'&&Number.isFinite(window.usedPercent)&&window.usedPercent>=0&&window.usedPercent<=100&&Number.isFinite(window.resetsAt)&&window.resetsAt*1000>now;
 const buckets=new Map();for(const b of Array.isArray(data?.usage?.dailyUsageBuckets)?data.usage.dailyUsageBuckets:[]){if(validDate(b?.startDate)&&safeCount(b.tokens)!==null)buckets.set(b.startDate,b.tokens);}
 const today=shanghaiDate(now),end=Date.parse(today+'T00:00:00Z');
 const points=Array.from({length:14},(_,i)=>{const date=new Date(end-(13-i)*DAY).toISOString().slice(0,10);return {date,tokens:buckets.has(date)?buckets.get(date):null};});
 const latest=[...buckets].filter(([date])=>date<=today).sort((a,b)=>b[0].localeCompare(a[0]))[0];
 // Upstream bucket timezone is not established by the existing source. Do not call latest day today.
 return {schemaVersion:1,status:stamp===null?'unavailable':stale?'partial':'ok',generatedAt:iso(now),
  source:{kind:'existing-memory-cache',observedAt:iso(stamp),stale,upstreamProvenance:'not-independently-verified',displayTimeZone:'Asia/Shanghai',usageDayTimeZone:null},
  weekly:{remainingPercent:validWindow?100-window.usedPercent:null,resetsAt:validWindow?iso(window.resetsAt*1000):null,windowMinutes:10080,observedAt:iso(stamp),stale:stale||!validWindow},
  tokens:{observedAt:iso(stamp),stale,today:{date:today,value:null,reason:'upstream-day-timezone-unverified'},latestDaily:{date:latest?.[0]??null,value:latest?.[1]??null},lifetime:{value:safeCount(data?.usage?.summary?.lifetimeTokens)},last30Days:{value:null,complete:false,reason:'coverage-and-day-boundary-not-verified'}},
  trend:{observedAt:iso(stamp),stale,periodDays:14,usageDayTimeZone:null,knownDays:points.filter(p=>p.tokens!==null).length,points}};
}
