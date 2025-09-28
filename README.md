# customer-contact

Spring Boot REST service for managing customer contact information.

## Quick start (build & run)

1. Build with Maven (Windows PowerShell):

```powershell
mvn -e -DskipTests package
```

2. Run the fat jar:

```powershell
java -jar target/customer-contact-0.0.1-SNAPSHOT.jar
```

## API endpoints

- GET /api/contacts/{id} — returns `ContactDto` or 404
- POST /api/contacts — creates or updates a contact by email (upsert). Expects JSON body matching `ContactDto` with validation.

## Example POST body (JSON):

```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@email.com",
  "phone": "123-456-7890",
  "address": "123 Main St"
}
```

## Notes

- The project uses Spring Data JPA and expects a datasource configured via application properties or environment variables.
- `email` is treated as a unique identifier and used by the service to upsert records.
- Validation errors return HTTP 400 with a JSON `ErrorResponse` (see `controller/GlobalExceptionHandler.java` and `dto/ErrorResponse.java`).
- Tests use H2 in-memory database.

## Files of interest

- `src/main/java/com/keviny/customercontact/controller/ContactController.java`
- `src/main/java/com/keviny/customercontact/service/ContactService.java`
- `src/main/java/com/keviny/customercontact/repository/ContactRepository.java`
- `src/main/java/com/keviny/customercontact/dto/ContactDto.java`
- `src/main/java/com/keviny/customercontact/mapper/ContactMapper.java`