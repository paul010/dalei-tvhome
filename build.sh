#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
: "${JAVA_HOME:?Set JAVA_HOME to an existing JDK}"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
: "${SDK:?Set ANDROID_HOME or ANDROID_SDK_ROOT}"
BT="$SDK/build-tools/${ANDROID_BUILD_TOOLS:-35.0.0}"
ANDROID="$SDK/platforms/android-21/android.jar"
BUILD="$ROOT/build"
for tool in "$JAVA_HOME/bin/javac" "$BT/aapt2" "$BT/d8" "$BT/zipalign"; do
  test -x "$tool" || { echo "Missing approved build tool: $tool" >&2; exit 1; }
done
test -f "$ANDROID"
bash "$ROOT/test.sh"
mkdir -p "$BUILD/generated" "$BUILD/classes" "$BUILD/dex" "$ROOT/output"
"$BT/aapt2" compile --dir "$ROOT/res" -o "$BUILD/resources.zip"
"$BT/aapt2" link -o "$BUILD/resources.ap_" --manifest "$ROOT/AndroidManifest.xml" \
  -I "$ANDROID" --java "$BUILD/generated" --min-sdk-version 21 --target-sdk-version 28 \
  --version-code 8 --version-name 0.5.0 -A "$ROOT/assets" "$BUILD/resources.zip"
find "$ROOT/src" "$BUILD/generated" -name '*.java' ! -name '._*' -print0 | \
  xargs -0 "$JAVA_HOME/bin/javac" -encoding UTF-8 -source 8 -target 8 \
  -bootclasspath "$ANDROID" -Xlint:all,-options -d "$BUILD/classes"
"$JAVA_HOME/bin/jar" cf "$BUILD/classes.jar" -C "$BUILD/classes" .
JAVA_HOME="$JAVA_HOME" PATH="$JAVA_HOME/bin:$PATH" "$BT/d8" --min-api 21 --lib "$ANDROID" --output "$BUILD/dex" "$BUILD/classes.jar"
python3 - "$BUILD" <<'PY'
import pathlib,sys,zipfile,shutil
p=pathlib.Path(sys.argv[1]);shutil.copyfile(p/'resources.ap_',p/'unaligned-unsigned.apk')
with zipfile.ZipFile(p/'unaligned-unsigned.apk','a') as z:
 for dex in sorted((p/'dex').glob('*.dex')):z.write(dex,dex.name,compress_type=zipfile.ZIP_STORED)
PY
"$BT/zipalign" -f 4 "$BUILD/unaligned-unsigned.apk" "$ROOT/output/dalei-tvhome-0.5.0-unsigned.apk"
"$BT/zipalign" -c 4 "$ROOT/output/dalei-tvhome-0.5.0-unsigned.apk"
"$BT/aapt2" dump badging "$ROOT/output/dalei-tvhome-0.5.0-unsigned.apk" > "$ROOT/output/apk-badging.txt"
"$BT/aapt2" dump xmltree --file AndroidManifest.xml "$ROOT/output/dalei-tvhome-0.5.0-unsigned.apk" > "$ROOT/output/apk-manifest.txt"
sha256sum "$ROOT/output/dalei-tvhome-0.5.0-unsigned.apk" > "$ROOT/output/SHA256SUMS"
printf '%s\n' 'Unsigned APK built. No keystore created. No device deployment performed.'
