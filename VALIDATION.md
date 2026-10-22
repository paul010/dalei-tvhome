# Validation of public 0.2.0

Performed on 2026-10-22 using externally installed OpenJDK21.0.2,
Android SDK Platform21 revision2, and Build-Tools35.0.0.

- Source/resource compilation, DEX conversion and ZIP alignment: passed.
- Pure Java navigation test: passed for an 80-item list, end boundaries,
  component insertion/removal, nested return, restored focus and empty list.
- Default unsigned APK: 21776 bytes.
- SHA256: `a6397eaf3b58a68d60f435ba14dc75d9ed68c39f1630732b7306edf34a3b235c`.
- DEX035, DEX SHA1/Adler32 and ZIP CRC: passed.
- No image assets, native libraries or APK signature in the public build.
- Manifest: minSDK21/targetSDK28, no permissions or HOME intent category;
  one launcher/Leanback activity, no service/receiver.

The APK is not published here. Source compilation emitted only obsolete
Java8 source/target warnings from the pure-Java test compiler. No runtime
or television testing of this public build has been performed. Video shortcut
selection, actual player launch/return, visual layout and physical remote use
still require runtime validation. No playback or third-party app was started
as part of this validation. No player security verdict is implied.


## 0.3.1 status

Home, Video, Codex dashboard, Apps and Settings share focus-preserving navigation. Optional SmartTube public Home/Subscriptions/Search components are checked at click time with fallback to the selected main entry. These are external page links, not embedded content. No provider query, automatic playback, voice recording, or network permissions.

CodexBar-style remaining/reset, daily tokens, 30-day tokens/trend and update-time cards explicitly show unconnected. Catalog-price estimates are not subscription bills. No screenshot values are hardcoded. Popular/latest covers and live usage data remain planned, without a verified feed or bridge.


### 0.3.2 navigation clarification
The standard video entry is preferred; external section links may exit to the system launcher or stay inside the player. No task-flag workaround or guaranteed return is claimed. A home-only Codex unconnected strip links conceptually to the existing workbench; no system overlay or network feed is added. See docs/player-navigation.md for bounded observations.


## 0.4.0 fixed PNG reader
- Generic and private-overlay builds compiled with API21 platform; navigation and fixed-endpoint/image-budget/expiry tests passed.
- Public unsigned ZIP CRC/DEX035 checksums passed, with no custom images or native libraries.
- INTERNET is now the only requested permission; no HOME, microphone, storage, service or control bridge.
- Review found and fixed slow-response total-timeout and connection-registration cancellation risks before private deployment.
- Error retention and background scheduling are covered by static review; failure injection and prolonged runtime remain to be tested. Public generic APK itself has no configured endpoint and is not deployed.


## 0.4.1 runtime boundaries
A private-overlay API21 build successfully fetched and displayed the existing fixed PNG resource on a television. The full source-image time and separate retrieval time were visible. This confirms transport/display, not freshness or provenance of every upstream statistic. No usage/render collector endpoint was invoked by this app.

Navigation, endpoint restrictions, dimension budgets and expiry tests pass. Generic0.4.1 unsigned build passes DEX035/checksums/ZIP CRC with no bundled images or native code. Private imagery/configuration and runtime captures are intentionally excluded. Offline failure injection, slow-network watchdog behavior and long-term operation are not physically tested.

Private0.4.1 visual regression: day background changed to sunset after48-second no-input observation with unchanged home selection; fixed PNG displayed with separate retrieval/source-time labels; confirmation zoom/restore and Back to home worked. Static-mode toggle was exercised then rotation restored. Screenshot assets and actual account values remain private.
