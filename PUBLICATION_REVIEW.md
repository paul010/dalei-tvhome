# Initial publication review

Scope: fresh source-only history for the original Dalei TV Home project.

| Tracked paths | Origin / license | Review |
| --- | --- | --- |
| `src/.../MainActivity.java` | Original project code, MIT | Procedural background, optional ignored local asset, local app resolver/preferences; no device-specific defaults |
| `src/.../NavigationState.java` | Original project code, MIT | Pure Java focus/scroll/history model |
| `test/.../NavigationStateTest.java` | Original project code, MIT | Boundary, stable identity, nested return and empty-list tests |
| `AndroidManifest.xml`, `res/values/strings.xml` | Original configuration, MIT | minAPI21; no permissions, HOME, services or receivers |
| `build.sh`, `test.sh`, `verify.py` | Original project tooling, MIT | Uses externally installed official tools; unsigned output only; no login, key generation or deployment |
| `.gitignore`, `assets/.gitkeep` | Original project scaffolding, MIT | Ignores local photos, build output and signing material |
| `README*`, `LICENSE`, `NOTICE`, this document | Original documentation / MIT license text | Explicit limitations and upstream boundary; no private test records |

No FLauncher/guizang source, skill text, vendor binaries, photos or branding were copied. Android APIs/build tools remain external dependencies. Functional/visual research references do not make this project a fork.

Before the first push: inspect every tracked file and all commits, scan for private material, verify the default APK contains no image assets or signature, and confirm the public owner and repository visibility. Do not publish private customization builds or device logs. This review is a bounded inspection, not a guarantee against every possible secret pattern.
