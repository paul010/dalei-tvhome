#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
: "${JAVA_HOME:?Set JAVA_HOME to an existing JDK (tested with JDK21)}"
mkdir -p "$ROOT/build/tests"
"$JAVA_HOME/bin/javac" --release 8 -encoding UTF-8 -d "$ROOT/build/tests" \
 "$ROOT/src/io/github/paul010/daleitvhome/NavigationState.java" \
 "$ROOT/test/io/github/paul010/daleitvhome/NavigationStateTest.java"
"$JAVA_HOME/bin/java" -cp "$ROOT/build/tests" io.github.paul010.daleitvhome.NavigationStateTest

"$JAVA_HOME/bin/javac" --release 8 -encoding UTF-8 -d "$ROOT/build/tests" \
 "$ROOT/src/io/github/paul010/daleitvhome/DashboardPolicy.java" \
 "$ROOT/test/io/github/paul010/daleitvhome/DashboardPolicyTest.java"
"$JAVA_HOME/bin/java" -cp "$ROOT/build/tests" io.github.paul010.daleitvhome.DashboardPolicyTest

"$JAVA_HOME/bin/javac" --release 8 -encoding UTF-8 -d "$ROOT/build/tests" "$ROOT/src/io/github/paul010/daleitvhome/NativeEndpointPolicy.java" "$ROOT/test/io/github/paul010/daleitvhome/NativeEndpointPolicyTest.java"
"$JAVA_HOME/bin/java" -cp "$ROOT/build/tests" io.github.paul010.daleitvhome.NativeEndpointPolicyTest
