# Dalei TV Home

An original, offline Android TV companion interface with a narrow left menu, clear remote focus, and room for your own wallpaper. This is **an additional app**, not a replacement system launcher. No Sony affiliation or branding.

[中文说明](README.zh-CN.md) · [MIT license](LICENSE) · [Source/asset notice](NOTICE)

## What works in the source

- D-pad / Enter / Back navigation with a visible outline and small focus scale.
- Long application lists: five visible rows, a position indicator, boundary clamping, and touch swipes.
- Nested Back restores the previous screen, selected component and scroll position.
- Unicode-aware ellipsis for long labels; consistent screen margins.
- Lists only installed, enabled, exported launcher activities. Opening an app requires an explicit user action.
- Optional video shortcut: choose an installed application locally. Choosing saves the component on this device; it does **not** launch or play it. Missing/unavailable shortcuts fall back to the application list.
- A procedural default background. An optional local wallpaper can be embedded at build time.

There are no network, storage, accessibility-service or other Android permissions. No analytics, downloads, root, boot receiver or `HOME` intent filter. The only saved preference is the video shortcut component in app-private local preferences. It is neither uploaded nor synced.

## Compatibility and current validation

`minSdkVersion 21`, `targetSdkVersion 28`; package `io.github.paul010.daleitvhome`, version `0.2.0`. This is a sideload-oriented experiment, not a claim of current Play Store compliance.

Public 0.2.0 is checked by local compilation and static APK checks, plus pure Java navigation tests. It has **not been installed or visually validated on a television**. Earlier private-prototype results do not establish compatibility of this version. Physical remotes, app handoff/return, accessibility screen-reader semantics, unusual aspect ratios and long-term performance require testing. The drawn UI has a selected-item content description, not a complete virtual accessibility tree.

HDMI/3D vendor controls, autoplay, search/deep links, playback control, account access and firmware changes are **not implemented**. No player code or APK is included. A selected player's security, compatibility and license remain separate from this project; no particular player or installed version is endorsed.

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
python3 verify.py output/dalei-tvhome-0.2.0-unsigned.apk --public
```

The build produces an **unsigned** APK. No key is generated and nothing is installed. Build outputs and signing material are ignored. Sign a reviewed local build with your own key, including **v1 signing for API 21**, before any deliberate sideload. Preserve your system launcher and use Back to exit this app.

## Optional private wallpaper

Place an image you have rights to use at `assets/local-wallpaper.png`, then build locally. That path is ignored by Git. This embeds the image in your local APK, so do not publish that APK if the image is private. `verify.py --public` deliberately rejects builds containing image assets. Omit `--public` when checking a private customization. Remove the image and rebuild to restore the procedural background. No on-device storage permission is requested.

## Development and publication boundaries

No private photos, screenshots, APKs, device addresses, authentication material, SDK binaries, or private history belong in this repository. Only clean original source and documentation are published. No CI, Pages or binary release is configured. See `PUBLICATION_REVIEW.md` for the initial tracked-file review.


## 0.3.1 status

Home, Video, Codex dashboard, Apps and Settings share focus-preserving navigation. Optional SmartTube public Home/Subscriptions/Search components are checked at click time with fallback to the selected main entry. These are external page links, not embedded content. No provider query, automatic playback, voice recording, or network permissions.

CodexBar-style remaining/reset, daily tokens, 30-day tokens/trend and update-time cards explicitly show unconnected. Catalog-price estimates are not subscription bills. No screenshot values are hardcoded. Popular/latest covers and live usage data remain planned, without a verified feed or bridge.
