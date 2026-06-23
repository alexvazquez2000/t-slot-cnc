param([string]$OutDir = "build_out")
$cp = (Get-Content "$env:TEMP\tslot_cp.txt" -Raw) -replace "\\", "/"
$srcFiles = Get-Content "$env:TEMP\tslot_sources.txt" | ForEach-Object { $_ -replace "\\", "/" }
$lines = @("-encoding","UTF-8","-cp","`"$cp`"","-d","`"$OutDir`"")
foreach ($f in $srcFiles) { $lines += "`"$f`"" }
$lines | Out-File -FilePath "$env:TEMP\tslot_javac_argfile.txt" -Encoding ascii
