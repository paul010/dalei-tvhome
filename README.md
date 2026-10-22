# Dalei TV Home

An original, local-first Android TV companion interface with a narrow left menu, clear remote focus, and room for your own wallpaper. This is **an additional app**, not a replacement system launcher. No Sony affiliation or branding.

[中文说明](README.zh-CN.md) · [MIT license](LICENSE) · [Source/asset notice](NOTICE)

## What works in the source

- D-pad / Enter / Back navigation with a visible outline and small focus scale.
- Long application lists: five visible rows, a position indicator, boundary clamping, and touch swipes.
- Nested Back restores the previous screen, selected component and scroll position.
- Unicode-aware ellipsis for long labels; consistent screen margins.
- Lists only installed, enabled, exported launcher activities. Opening an app requires an explicit user action.
- Optional video shortcut: choose an installed application locally. Choosing saves the component on this device; it does **not** launch or play it. Missing/unavailable shortcuts fall back to the application list.
- A procedural default background. An optional local wallpaper can be embedded at build time.

The only Android permission is INTERNET for an optional user-configured fixed JSON statistics endpoint. No storage/accessibility permission, analytics, root, boot receiver or `HOME` intent filter. Video shortcut, motion preference and the last whitelisted statistics snapshot are stored in app-private preferences. It is neither uploaded nor synced.

## Compatibility and current validation

`minSdkVersion 21`, `targetSdkVersion 28`; package `io.github.paul010.daleitvhome`, version `0.5.0`. This is a sideload-oriented experiment, not a claim of current Play Store compliance.

Public 0.5.0 is checked by local compilation and static APK checks, plus pure Java navigation tests. It has **not been installed or visually validated on a television**. Earlier private-prototype results do not establish compatibility of this version. Physical remotes, app handoff/return, accessibility screen-reader semantics, unusual aspect ratios and long-term performance require testing. The drawn UI has a selected-item content description, not a complete virtual accessibility tree.

HDMI/3D vendor controls, autoplay, embedded feeds, playback control, account access and firmware changes are **not implemented**. No player code or APK is included. A selected player's security, compatibility and license remain separate from this project; no particular player or installed version is endorsed.

## Build without Gradle

Prerequisites, installed separately from official sources:

- A JDK (tested with OpenJDK 21).
- Android SDK Platform 21 and Build-Tools 35.0.0.
- Bash and Python 3.

Accept the relevant upstream licenses yourself. This repository does not download tools, install packages or accept licenses.

```sh
export JAVA_HOME=/path/to/jdk
export ANDROID_HOME=/path/to/android-sdk
bash test.sh
bash build.sh
python3 verify.py output/dalei-tvhome-0.5.0-unsigned.apk --public
```

The build produces an **unsigned** APK. No key is generated and nothing is installed. Build outputs and signing material are ignored. Sign a reviewed local build with your own key, including **v1 signing for API 21**, before any deliberate sideload. Preserve your system launcher and use Back to exit this app.

## Optional private wallpaper

Place an image you have rights to use at `assets/local-wallpaper.png`, then build locally. That path is ignored by Git. This embeds the image in your local APK, so do not publish that APK if the image is private. `verify.py --public` deliberately rejects builds containing image assets. Omit `--public` when checking a private customization. Remove the image and rebuild to restore the procedural background. No on-device storage permission is requested.

## Development and publication boundaries

No private photos, screenshots, APKs, device addresses, authentication material, SDK binaries, or private history belong in this repository. Only clean original source and documentation are published. No CI, Pages or binary release is configured. See `PUBLICATION_REVIEW.md` for the initial tracked-file review.



## 0.5 native statistics

The APK draws quota bars, nullable numbers and a 14-date trend natively. It does not embed a dashboard screenshot or OCR numbers. Optional ignored `assets/local-dashboard.properties` uses `jsonUrl=` for a user-authorized HTTP host on port 8787 at exactly `/api/tv-codex`. Public builds have no configured address. Only the visible, resumed workbench requests every 60 seconds; leaving cancels requests. JSON is capped at 32 KiB, redirects disabled, connect/read timeouts 4 seconds, total deadline 12 seconds.

`adapters/memory-snapshot.mjs` is a pure whitelist transform of an already-existing in-memory cache; it cannot collect, read files, execute RPC or refresh data. Integrators must separately authorize an existing-service route restricted by real socket peer IP and validated Host, with no wildcard CORS or new listener. IP restrictions are not authentication and HTTP is unencrypted. No sample private address is included.

Source observation time is distinct from JSON generation/fetch time. Source age over 120 seconds is stale; upstream event freshness/provenance is not independently verified. Expired quota reset times invalidate the quota. Daily bucket timezone is unknown: today's total stays null, lifetime is explicitly cumulative, a 14-date trend is not a 30-day total, missing is not zero. The last successful snapshot is retained and labeled stale/failed on errors. See [schema and integration boundaries](docs/native-codex-memory-plan.md).

Optional ignored `local-wallpaper.png`, `wallpaper-01.png` etc. and transparent `local-portraits.png` are private build overlays. The portrait pair uses separate source halves, each at most about 22% screen height. Home-only rotation pauses offscreen and can be disabled in Settings; generated still images are not animated people. No private assets are published.
