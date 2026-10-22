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
