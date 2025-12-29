# e2e-test.ps1
$base = "http://127.0.0.1:8080"
$resourceId = 1
$from = "2025-12-29T08:00:00Z"
$to   = "2025-12-29T10:00:00Z"
$customer = "Mariem"
$durationMinutes = 45

$availUrl = "$base/api/resources/$resourceId/availability?from=$from&to=$to"

Write-Host "1) Availability..."
$avail = Invoke-RestMethod -Uri $availUrl -Proxy $null
$avail.freeSlots | Select-Object -First 5 | ForEach-Object { Write-Host "   - $_" }

$start = [datetimeoffset]$avail.freeSlots[0]
$end   = $start.AddMinutes($durationMinutes)

$body = @{
  resourceId   = $resourceId
  customerName = $customer
  startAt      = $start.ToString("o")
  endAt        = $end.ToString("o")
} | ConvertTo-Json

Write-Host "`n2) Create booking..."
$created = Invoke-RestMethod -Method Post -Uri "$base/api/bookings" -ContentType "application/json" -Body $body -Proxy $null
$bookingId = $created.bookingId
Write-Host "   Created bookingId = $bookingId"

Write-Host "`n3) Read booking..."
Invoke-RestMethod -Uri "$base/api/bookings/$bookingId" -Proxy $null | Format-List

Write-Host "`n4) Try duplicate (expect 409)..."
try {
  Invoke-RestMethod -Method Post -Uri "$base/api/bookings" -ContentType "application/json" -Body $body -Proxy $null
  Write-Host "   Unexpected: duplicate created "
} catch {
  Write-Host "   Expected conflict "
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}

Write-Host "`n5) Cancel booking..."
Invoke-RestMethod -Method Post -Uri "$base/api/bookings/$bookingId/cancel" -Proxy $null | Out-Null
Invoke-RestMethod -Uri "$base/api/bookings/$bookingId" -Proxy $null | Format-List

Write-Host "`n6) Availability restored (first 8):"
(Invoke-RestMethod -Uri $availUrl -Proxy $null).freeSlots | Select-Object -First 8

Write-Host "`nDONE "
