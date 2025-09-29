import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DatabaseEndToEndTest {
    private static final String BASE_URL = "http://localhost:8082/api/contacts";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Customer Contact API - Database End-to-End Test ===");
        System.out.println("Target: " + BASE_URL);
        System.out.println();
        
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        
        try {
            // Test 1: GET all contacts (should return empty array initially)
            System.out.println("Test 1: GET all contacts...");
            HttpResponse<String> getAllResponse = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + getAllResponse.statusCode());
            System.out.println("Response: " + getAllResponse.body());
            System.out.println();
            
            // Test 2: POST create new contact with primaryPhone and primaryEmail
            System.out.println("Test 2: POST create new contact...");
            String createJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phone": "555-0001",
                    "primaryPhone": "555-1001",
                    "address": "123 Main St, Anytown, USA",
                    "primaryEmail": "john.primary@example.com"
                }
                """;
            
            HttpResponse<String> createResponse = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createJson))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + createResponse.statusCode());
            System.out.println("Response: " + createResponse.body());
            System.out.println("Location Header: " + createResponse.headers().firstValue("Location").orElse("N/A"));
            System.out.println();
            
            // Test 3: POST create another contact
            System.out.println("Test 3: POST create second contact...");
            String createJson2 = """
                {
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "email": "jane.smith@example.com",
                    "phone": "555-0002",
                    "primaryPhone": "555-1002",
                    "address": "456 Oak Ave, Springfield, USA",
                    "primaryEmail": "jane.primary@example.com"
                }
                """;
            
            HttpResponse<String> createResponse2 = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createJson2))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + createResponse2.statusCode());
            System.out.println("Response: " + createResponse2.body());
            System.out.println();
            
            // Test 4: GET all contacts (should now return the created contacts)
            System.out.println("Test 4: GET all contacts after creation...");
            HttpResponse<String> getAllResponse2 = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + getAllResponse2.statusCode());
            System.out.println("Response: " + getAllResponse2.body());
            System.out.println();
            
            // Test 5: GET specific contact by ID
            System.out.println("Test 5: GET contact by ID=1...");
            HttpResponse<String> getByIdResponse = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/1"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + getByIdResponse.statusCode());
            System.out.println("Response: " + getByIdResponse.body());
            System.out.println();
            
            // Test 6: Test validation with invalid data
            System.out.println("Test 6: POST with invalid data (should fail validation)...");
            String invalidJson = """
                {
                    "firstName": "",
                    "lastName": "",
                    "email": "invalid-email"
                }
                """;
            
            HttpResponse<String> invalidResponse = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Status: " + invalidResponse.statusCode());
            System.out.println("Response: " + invalidResponse.body());
            System.out.println();
            
            System.out.println("=== Database Test Summary ===");
            boolean allTestsPassed = true;
            
            if (getAllResponse.statusCode() == 200) {
                System.out.println("+ GET all contacts: PASSED");
            } else {
                System.out.println("- GET all contacts: FAILED");
                allTestsPassed = false;
            }
            
            if (createResponse.statusCode() == 201) {
                System.out.println("+ POST create contact: PASSED");
            } else {
                System.out.println("- POST create contact: FAILED");
                allTestsPassed = false;
            }
            
            if (getAllResponse2.statusCode() == 200 && getAllResponse2.body().contains("john.doe")) {
                System.out.println("+ Data persistence: PASSED");
            } else {
                System.out.println("- Data persistence: FAILED");
                allTestsPassed = false;
            }
            
            if (getByIdResponse.statusCode() == 200) {
                System.out.println("+ GET by ID: PASSED");
            } else {
                System.out.println("- GET by ID: FAILED");
                allTestsPassed = false;
            }
            
            if (invalidResponse.statusCode() == 400) {
                System.out.println("+ Validation handling: PASSED");
            } else {
                System.out.println("- Validation handling: FAILED");
                allTestsPassed = false;
            }
            
            System.out.println();
            if (allTestsPassed) {
                System.out.println(">>> ALL TESTS PASSED - Database integration successful!");
            } else {
                System.out.println("X Some tests failed - Check application and database configuration");
            }
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}