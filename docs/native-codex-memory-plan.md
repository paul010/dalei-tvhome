# Native statistics integration

Version 0.5 draws native quota/text/date-bucket bars and removes the old PNG reader from the workbench. Empty data remains null, not screenshots or invented usage.

Verified existing export point: KindleDashboard backend/codex-page.js module-local `last = {data, at}` populated by existing render activity. Add a synchronous `peekTvSnapshot()` export which only transforms `last` and never calls readCodex(), renderPage(), readAccountData(), collectAll(), filesystem, RPC or weather. `last == null` returns unavailable. A narrowly scoped cache getter and route require explicit operator approval.

Integration contract: GET /api/tv-codex on the already-running Mac HTTP8787 service; exact URL, no query, no new bind/port/token. Only the established TV IP plus loopback admitted; all other clients403. Content-Type application/json, Cache-Control no-store, GET only,32KiB budget. No account IDs, paths, model names, money, prompts, task content or raw API object. Plaintext unauthenticated LAN statistics remain observable to on-path clients; IP filtering is not cryptographic authentication. Existing listener remains unchanged.

New exposure vs PNG approval: machine-readable weekly remaining/reset, latest dated tokens, lifetime tokens and14 date-bucket counts, source sampling metadata/null states. The APK reader replaces the PNG path, not add another collector. Private source/TV addresses stay outside public source. Deployment of this JSON resource and source route change/reload requires explicit operator approval.

Mapping: rateLimitsByLimitId.codex or rateLimits ->10080-minute window usedPercent; remaining=100-usedPercent only in0..100 with future reset. usage.summary.lifetimeTokens is lifetime only. dailyUsageBuckets.startDate/tokens are dated records; latest daily is NOT today. Upstream bucket timezone is not verified: usageDayTimeZone=null and todayTokens=null. Display timezone Asia/Shanghai is presentation only. Trend period14 with null gaps, true0 retained;30-day aggregate stays null until coverage/day boundaries are verified. Existing partial/fallback provenance is not independently proven; declare it.

source.observedAt = last.at (existing successful acquisition time), not an upstream event time. generatedAt = transform/response generation time, never renews observedAt. stale on missing/older-than120s/future clock skew/partial source. Each metric group includes observedAt and stale. Failed transport must retain last snapshot with clear stale label. No 30-day claim for14 dates.

## Runtime boundary
The authorized private integration adds one GET route to an existing service and reads only its pre-existing in-memory cache. It does not invoke collectors. Source service configuration and addresses are private. Native 0.5 replaces the prior PNG display. Public source is generic and has no endpoint configured.
