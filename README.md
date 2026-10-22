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

The only Android permission is INTERNET for an optional user-configured fixed PNG endpoint. No storage/accessibility permission, analytics, root, boot receiver or `HOME` intent filter. The only saved preference is the video shortcut component in app-private local preferences. It is neither uploaded nor synced.

## Compatibility and current validation

`minSdkVersion 21`, `targetSdkVersion 28`; package `io.github.paul010.daleitvhome`, version `0.4.1`. This is a sideload-oriented experiment, not a claim of current Play Store compliance.

Public 0.4.1 is checked by local compilation and static APK checks, plus pure Java navigation tests. It has **not been installed or visually validated on a television**. Earlier private-prototype results do not establish compatibility of this version. Physical remotes, app handoff/return, accessibility screen-reader semantics, unusual aspect ratios and long-term performance require testing. The drawn UI has a selected-item content description, not a complete virtual accessibility tree.

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
python3 verify.py output/dalei-tvhome-0.4.1-unsigned.apk --public
```

The build produces an **unsigned** APK. No key is generated and nothing is installed. Build outputs and signing material are ignored. Sign a reviewed local build with your own key, including **v1 signing for API 21**, before any deliberate sideload. Preserve your system launcher and use Back to exit this app.

## Optional private wallpaper

Place an image you have rights to use at `assets/local-wallpaper.png`, then build locally. That path is ignored by Git. This embeds the image in your local APK, so do not publish that APK if the image is private. `verify.py --public` deliberately rejects builds containing image assets. Omit `--public` when checking a private customization. Remove the image and rebuild to restore the procedural background. No on-device storage permission is requested.

## Development and publication boundaries

No private photos, screenshots, APKs, device addresses, authentication material, SDK binaries, or private history belong in this repository. Only clean original source and documentation are published. No CI, Pages or binary release is configured. See `PUBLICATION_REVIEW.md` for the initial tracked-file review.


## 0.4 fixed dashboard PNG

Optional ignored `assets/local-dashboard.properties` contains `url=` followed by a user-authorized HTTP host on port 8787 with the exact path `/dash.png`. No sample private address is shipped. Missing configuration means no request. No tokens, account credentials, collectors, usage/render endpoints, redirects, query parameters or arbitrary paths. Only the visible/resumed workbench fetches, immediately then every 60 seconds; leaving cancels scheduling and active I/O.

Maximum PNG response 1 MiB, connection/read timeout 4 seconds each and a 12-second total deadline, asynchronous bounded decode (RGB565, sampled long edge at most 1280). Last successful image is retained in memory on error and labeled failed/stale. Retrieval time is NOT the source data time: the full image preserves its own data-time labels. A 120-second retrieval-age threshold does not prove source freshness. The home shows connection state only, never OCR-derived quota numbers. Confirm zooms the full image; arrows pan while zoomed, Back exits. No screenshot or account statistics are committed.

This is unauthenticated, unencrypted HTTP intended only for an explicitly trusted local network; do not expose the source service publicly. Modern Android cleartext restrictions may require separate supported configuration; API21 is the initial runtime target. No new server, port, token or control channel is created. The YouTube home card is an external browsing entry, not a real video thumbnail or recommendation. Public and private builds remain distinct; public generic build has no configured endpoint or personal wallpaper.


Optional ignored assets `wallpaper-01.png`, `wallpaper-02.png`, etc. enable local home-only rotation every45s,600ms fade capped near15fps. Only current/previous decoded frames are retained, with one bounded background decode. Interaction defers the next change; leaving the home or app stops rotation. Settings can freeze the current background. These are static generated images rotated by the app, not animated people or pet substitutes. No image assets are included in public builds.

Optional SmartTube Home/Subscriptions/empty Search public section launchers use standard ACTION_MAIN with per-click availability checks and main-entry fallback. They are external page links; return behavior belongs to the player and is not guaranteed. See [navigation observations](docs/player-navigation.md).
