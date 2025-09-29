# High TPS Load Test Script for Customer-Contact API
param(
    [int]$RequestCount = 100,
    [int]$ConcurrentUsers = 10,
    [string]$BaseUrl = "http://localhost:8081"
)

Write-Host "Starting High TPS Load Test" -ForegroundColor Green
Write-Host "Request Count: $RequestCount" -ForegroundColor Yellow
Write-Host "Concurrent Users: $ConcurrentUsers" -ForegroundColor Yellow
Write-Host "Base URL: $BaseUrl" -ForegroundColor Yellow
Write-Host "===========================================" -ForegroundColor Green

# Test data templates
$testContacts = @(
    @{
        firstName = "John"
        lastName = "Doe"
        email = "john.doe"
        phone = "555-0123"
        address = "123 Main St"
        primaryPhone = "555-0199"
        primaryEmail = "john.primary"
    },
    @{
        firstName = "Jane"
        lastName = "Smith"
        email = "jane.smith"
        phone = "555-0456"
        address = "456 Oak Ave"
        primaryPhone = "555-0789"
        primaryEmail = "jane.primary"
    },
    @{
        firstName = "Bob"
        lastName = "Johnson"
        email = "bob.johnson"
        phone = "555-0789"
        address = "789 Pine St"
        primaryPhone = "555-0012"
        primaryEmail = "bob.primary"
    }
)

$results = @()
$startTime = Get-Date

# Function to create a contact
function Create-Contact {
    param($index)
    
    $contactTemplate = $testContacts[$index % $testContacts.Count]
    $contact = $contactTemplate.Clone()
    $contact.email = "$($contact.email)$index@example.com"
    $contact.primaryEmail = "$($contact.primaryEmail)$index@example.com"
    
    $body = $contact | ConvertTo-Json
    
    try {
        $requestStart = Get-Date
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/contacts" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 30
        $requestEnd = Get-Date
        $duration = ($requestEnd - $requestStart).TotalMilliseconds
        
        return @{
            Success = $true
            Duration = $duration
            StatusCode = 201
            Response = $response
        }
    }
    catch {
        $requestEnd = Get-Date
        $duration = ($requestEnd - $requestStart).TotalMilliseconds
        
        return @{
            Success = $false
            Duration = $duration
            Error = $_.Exception.Message
            StatusCode = if ($_.Exception.Response) { $_.Exception.Response.StatusCode } else { "Unknown" }
        }
    }
}

# Run concurrent requests
$jobs = @()
for ($i = 0; $i -lt $RequestCount; $i++) {
    $jobs += Start-Job -ScriptBlock {
        param($baseUrl, $index, $functionDefinition, $contactData)
        
        # Recreate the function in this job scope
        Invoke-Expression $functionDefinition
        
        Create-Contact -index $index
    } -ArgumentList $BaseUrl, $i, ${function:Create-Contact}.ToString(), $testContacts
    
    # Limit concurrent jobs
    if ($jobs.Count -ge $ConcurrentUsers) {
        $completedJob = $jobs | Wait-Job -Any
        $result = Receive-Job $completedJob
        $results += $result
        Remove-Job $completedJob
        $jobs = $jobs | Where-Object { $_.Id -ne $completedJob.Id }
        
        Write-Progress -Activity "Running Load Test" -Status "Completed $($results.Count)/$RequestCount requests" -PercentComplete (($results.Count / $RequestCount) * 100)
    }
}

# Wait for remaining jobs
$jobs | Wait-Job | ForEach-Object {
    $result = Receive-Job $_
    $results += $result
    Remove-Job $_
}

$endTime = Get-Date
$totalDuration = ($endTime - $startTime).TotalSeconds

Write-Host ""
Write-Host "===========================================" -ForegroundColor Green
Write-Host "Load Test Results" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green

$successfulRequests = $results | Where-Object { $_.Success }
$failedRequests = $results | Where-Object { -not $_.Success }

Write-Host "Total Requests: $($results.Count)" -ForegroundColor White
Write-Host "Successful Requests: $($successfulRequests.Count)" -ForegroundColor Green
Write-Host "Failed Requests: $($failedRequests.Count)" -ForegroundColor Red
Write-Host "Success Rate: $([math]::Round(($successfulRequests.Count / $results.Count) * 100, 2))%" -ForegroundColor Yellow

if ($successfulRequests.Count -gt 0) {
    $avgResponseTime = ($successfulRequests | Measure-Object -Property Duration -Average).Average
    $minResponseTime = ($successfulRequests | Measure-Object -Property Duration -Minimum).Minimum
    $maxResponseTime = ($successfulRequests | Measure-Object -Property Duration -Maximum).Maximum
    
    Write-Host "Average Response Time: $([math]::Round($avgResponseTime, 2)) ms" -ForegroundColor Yellow
    Write-Host "Min Response Time: $([math]::Round($minResponseTime, 2)) ms" -ForegroundColor Yellow
    Write-Host "Max Response Time: $([math]::Round($maxResponseTime, 2)) ms" -ForegroundColor Yellow
}

Write-Host "Total Test Duration: $([math]::Round($totalDuration, 2)) seconds" -ForegroundColor Yellow
Write-Host "Requests Per Second (TPS): $([math]::Round($results.Count / $totalDuration, 2))" -ForegroundColor Cyan

if ($failedRequests.Count -gt 0) {
    Write-Host ""
    Write-Host "Failed Request Details:" -ForegroundColor Red
    $failedRequests | ForEach-Object {
        Write-Host "  Status: $($_.StatusCode), Error: $($_.Error)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Load test completed successfully!" -ForegroundColor Green