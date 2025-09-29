# Test script for findById endpoint
Write-Host "Testing findById endpoint..."

# Wait for application to be ready
Start-Sleep -Seconds 5

# Create a test contact first
$createBody = @{
    firstName = "John"
    lastName = "Doe"
    email = "john.doe@example.com"
    phoneNumber = "123-456-7890"
    primaryPhone = "123-456-7890"
    primaryEmail = "john.doe@example.com"
} | ConvertTo-Json

Write-Host "Creating test contact..."
try {
    $createdContact = Invoke-RestMethod -Uri "http://localhost:8081/api/contacts" -Method POST -Body $createBody -ContentType "application/json"
    Write-Host "✅ Created contact with ID: $($createdContact.id)"
    
    # Test GET by ID
    Write-Host "Testing GET by ID..."
    $retrievedContact = Invoke-RestMethod -Uri "http://localhost:8081/api/contacts/$($createdContact.id)" -Method GET
    Write-Host "✅ Retrieved contact: $($retrievedContact.firstName) $($retrievedContact.lastName)"
    
    # Test GET with non-existent ID
    Write-Host "Testing GET with non-existent ID (999)..."
    try {
        Invoke-RestMethod -Uri "http://localhost:8081/api/contacts/999" -Method GET
        Write-Host "❌ Should have returned 404"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 404) {
            Write-Host "✅ Correctly returned 404 for non-existent ID"
        } else {
            Write-Host "❌ Unexpected error: $($_.Exception.Message)"
        }
    }
    
    # Test GET all contacts
    Write-Host "Testing GET all contacts..."
    $allContacts = Invoke-RestMethod -Uri "http://localhost:8081/api/contacts" -Method GET
    Write-Host "✅ Retrieved $($allContacts.Length) contacts"
    
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)"
}