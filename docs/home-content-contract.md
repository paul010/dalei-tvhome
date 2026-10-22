# Planned content contract

Not connected or implemented. FeedSnapshot: schemaVersion, sourceId/name/url, classification (latest/popular/curated/search), scope, fetchedAt, expiresAt, cards (max 5). Card: stableVideoId, title, channelName, publishedAt (nullable), observedAt, thumbnailUrl, watchUrl, evidenceUrl, rank (nullable). Latest requires publication time; popular requires ranking evidence and region/category. Never relabel search/curated as popular or persist SmartTube temporary search IDs.

Expired/empty sources fall back to wallpaper. Planned rotation every 8–10 seconds only while resumed and unfocused; focus pauses, left/right selects, Back preserves focus, confirmation alone launches. No media preload. Planned HTTPS images max 5, 1MB each and decoded long edge 1280px. Test offline/expiry/404, missing provenance, deduplication, truncation, focus stability, stopped background timers, single launch and API21 memory limits.

Private pet overlays are future work: reserve an optional lower-left area only when it does not overlap navigation or portrait subjects. No pet assets, identities or fabricated substitutes are bundled. No animation timer is implemented in this version.
