[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'

New-Item -Path 'lib' -ItemType Directory | Out-Null

Write-Output "Downloading gson-2.8.5.jar to lib/gson-2.8.5.jar ..."
(New-Object System.Net.WebClient).DownloadFile(
    'http://central.maven.org/maven2/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar',
    'lib/gson-2.8.5.jar')

Write-Output "Downloading httpcore-4.4.11.jar to lib/httpcore-4.4.11.jar ..."
(New-Object System.Net.WebClient).DownloadFile(
    'http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.11/httpcore-4.4.11.jar',
    'lib/httpcore-4.4.11.jar')

Write-Output "Downloading httpclient-4.5.7.jar to lib/httpclient-4.5.7.jar ..."
(New-Object System.Net.WebClient).DownloadFile(
    'http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.7/httpclient-4.5.7.jar',
    'lib/httpclient-4.5.7.jar')

Write-Output "Downloading consul-api-1.4.2.jar to lib/consul-api-1.4.2.jar ..."
(New-Object System.Net.WebClient).DownloadFile(
    'http://central.maven.org/maven2/com/ecwid/consul/consul-api/1.4.2/consul-api-1.4.2.jar',
    'lib/consul-api-1.4.2.jar')

Write-Output "Downloading snakeyaml-1.24.jar to lib/snakeyaml-1.24.jar ..."
(New-Object System.Net.WebClient).DownloadFile(
    'http://central.maven.org/maven2/org/yaml/snakeyaml/1.24/snakeyaml-1.24.jar',
    'lib/snakeyaml-1.24.jar')

Write-Output "All dependencies downloaded"