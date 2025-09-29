import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTest {
    private static final String BASE_URL = "http://localhost:8082/api/contacts";
    private static final int TOTAL_REQUESTS = 200;
    private static final int CONCURRENT_THREADS = 25;
    
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Customer Contact API Load Test ===");
        System.out.println("Target: " + BASE_URL);
        System.out.println("Total Requests: " + TOTAL_REQUESTS);
        System.out.println("Concurrent Threads: " + CONCURRENT_THREADS);
        System.out.println("Expected TPS: ~" + (TOTAL_REQUESTS / 10) + " requests/second");
        System.out.println();
        
        // Create HTTP client with connection pooling
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        
        long startTime = System.currentTimeMillis();
        
        // Submit all requests
        CompletableFuture<?>[] futures = new CompletableFuture[TOTAL_REQUESTS];
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int requestId = i + 1;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    sendRequest(client, requestId);
                } catch (Exception e) {
                    System.err.println("Request " + requestId + " failed: " + e.getMessage());
                    errorCount.incrementAndGet();
                }
            }, executor);
        }
        
        // Wait for all requests to complete
        CompletableFuture.allOf(futures).join();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double actualTPS = (double) TOTAL_REQUESTS / (duration / 1000.0);
        
        // Results
        System.out.println();
        System.out.println("=== Load Test Results ===");
        System.out.println("Total Requests: " + TOTAL_REQUESTS);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Failed: " + errorCount.get());
        System.out.println("Duration: " + duration + " ms");
        System.out.println("Actual TPS: " + String.format("%.2f", actualTPS));
        System.out.println("HikariCP Performance: " + 
            (actualTPS > 20 ? "EXCELLENT" : actualTPS > 10 ? "GOOD" : "NEEDS_IMPROVEMENT"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    private static void sendRequest(HttpClient client, int requestId) throws Exception {
        String jsonBody = String.format("""
            {
                "firstName": "Load%d",
                "lastName": "Test%d",
                "email": "load%d@example.com",
                "primaryPhone": "555-010-%04d",
                "primaryEmail": "primary%d@example.com"
            }
            """, requestId, requestId, requestId, requestId, requestId);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            successCount.incrementAndGet();
            System.out.print("✓");
        } else {
            errorCount.incrementAndGet();
            System.err.println("Request " + requestId + " failed with status: " + response.statusCode());
        }
    }
}