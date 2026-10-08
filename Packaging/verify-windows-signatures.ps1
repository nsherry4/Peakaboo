<#
.SYNOPSIS
Checks that a Peakaboo MSI and the launchers inside it are validly signed and timestamped.

.DESCRIPTION
Runs as the last signing step in release.yml, and is also worth running by hand on a downloaded
MSI before publishing it. Unpacking the MSI (rather than checking the app image) also confirms
that jpackage carried the signed launchers through unchanged.

The MSI and launchers are ours and must pass. Everything else (the bundled runtime) is Temurin's
and should already carry their signatures; if any of it doesn't, we warn rather than fail, since
it's not something we sign or can fix at release time.

.EXAMPLE
./verify-windows-signatures.ps1 Peakaboo-6.3.msi
#>
param(
    [Parameter(Mandatory)]
    [string] $Msi
)

$ErrorActionPreference = 'Stop'
$Msi = (Resolve-Path $Msi).Path

# an administrative install just unpacks the MSI's files, nothing gets registered with Windows
$extract = Join-Path ([System.IO.Path]::GetTempPath()) ("msi-verify-" + [guid]::NewGuid())
try {
    $p = Start-Process msiexec.exe -ArgumentList '/a', "`"$Msi`"", '/qn', "TARGETDIR=`"$extract`"" -Wait -PassThru
    if ($p.ExitCode -ne 0) { throw "msiexec /a failed with exit code $($p.ExitCode)" }

    # jpackage's launchers are the only exes outside the runtime dir
    $binaries = Get-ChildItem $extract -Recurse -File -Include *.exe,*.dll
    $launchers = $binaries | Where-Object { $_.Extension -eq '.exe' -and $_.FullName -notmatch '\\runtime\\' }
    if (@($launchers).Count -lt 2) { throw "Expected the Peakaboo and CLI launchers in the MSI, found: $($launchers.Name -join ', ')" }
    $others = $binaries | Where-Object { $launchers.FullName -notcontains $_.FullName }

    $ours = @(Get-AuthenticodeSignature $Msi) + ($launchers | ForEach-Object { Get-AuthenticodeSignature $_.FullName })
    $ours | Format-Table Status, @{n='Signer'; e={$_.SignerCertificate.Subject}}, @{n='Timestamped'; e={$null -ne $_.TimeStamperCertificate}}, Path -AutoSize -Wrap

    $signer = $ours[0].SignerCertificate.Subject
    # timestamps matter because the certs only live ~3 days; without one the signature expires with it
    $problems = $ours | Where-Object { $_.Status -ne 'Valid' -or $null -eq $_.TimeStamperCertificate -or $_.SignerCertificate.Subject -ne $signer }
    if ($problems) { throw "Not validly signed and timestamped by $($signer):`n$($problems.Path -join "`n")" }

    $unsigned = $others | Where-Object { (Get-AuthenticodeSignature $_.FullName).Status -ne 'Valid' }
    foreach ($f in $unsigned) {
        $rel = $f.FullName.Substring($extract.Length + 1)
        # an annotation shows up on the run summary, where a plain log line would get missed
        if ($env:GITHUB_ACTIONS -eq 'true') { Write-Host "::warning title=Unsigned runtime file::$rel" }
        else { Write-Warning "Unsigned runtime file: $rel" }
    }

    Write-Host "MSI and $(@($launchers).Count) launchers signed by $signer; $(@($others).Count - @($unsigned).Count) of $(@($others).Count) runtime binaries carry valid signatures"
}
finally {
    Remove-Item $extract -Recurse -Force -ErrorAction SilentlyContinue
}
