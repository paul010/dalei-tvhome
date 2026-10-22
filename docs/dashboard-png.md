# Fixed read-only dashboard PNG

Only an explicit local configuration enables network access. Exact HTTP port8787 path /dash.png, no redirects/credentials/query, GET only, no collector or account-storage access. The caller must authorize the plaintext unauthenticated local-network source. No new listener or source-service setting.

A single worker downloads at most1MiB with4s connect/read waits and12s total deadline. Decode is off the UI thread, validates PNG/dimensions and samples long edge to1280 RGB565. Main-thread bitmap replacement keeps the previous successful image on failure. The visible workbench refreshes immediately and every60s; leaving/onPause cancels periodic work, invalidates old callbacks and disconnects. The home does not fetch.

Network retrieval time is separate from the data time printed in the image. No OCR or fake live quotas. A120s retrieval-age warning cannot establish upstream data freshness. The full source image remains available, with zoom/pan for small labels; the home only shows connection state. Failures show stale/unavailable alongside last retrieval time. Image is held in memory only, not persisted across process death.

No feed, automatic video playback, microphone, role animation or external control is implemented. Optional private wallpaper rotation uses current/previous bounded frames, stops off-home/onPause and has a static-mode setting; no personal assets belong in this repository.
