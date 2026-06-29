# Build and package T-Slot CNC as a Windows app image.
# Run from the project root after ensuring mvn, jlink, and jpackage are on PATH.

$ErrorActionPreference = "Stop"

$JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
$FX_MODS   = "C:\Users\alexv\.m2\repository\org\openjfx"
$FX_VER    = "21.0.2"
$FX_PATH   = "$FX_MODS\javafx-base\$FX_VER\javafx-base-$FX_VER-win.jar;" +
             "$FX_MODS\javafx-graphics\$FX_VER\javafx-graphics-$FX_VER-win.jar;" +
             "$FX_MODS\javafx-controls\$FX_VER\javafx-controls-$FX_VER-win.jar"

Write-Host "==> Maven build"
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "==> Preparing input directory"
New-Item -ItemType Directory -Force target\input | Out-Null
Copy-Item target\t-slot-cnc-1.0.0.jar target\input\ -Force

Write-Host "==> Creating custom JRE with JavaFX (jlink)"
jlink `
  --module-path "$JAVA_HOME\jmods;$FX_PATH" `
  --add-modules java.se,jdk.unsupported,javafx.base,javafx.graphics,javafx.controls `
  --output target\runtime-fx `
  --no-header-files `
  --no-man-pages
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "==> Packaging EXE (jpackage)"
jpackage `
  --type app-image `
  --name "T-Slot CNC" `
  --app-version 1.0.0 `
  --vendor "Alex Vazquez" `
  --input target\input `
  --main-jar t-slot-cnc-1.0.0.jar `
  --runtime-image target\runtime-fx `
  --java-options "-Dapp.packaged=true" `
  --dest target\dist
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "Done: target\dist\T-Slot CNC\T-Slot CNC.exe"
