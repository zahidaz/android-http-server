# Android HTTP Server Template

This is a complete HTTP server implementation for Android apps using Ktor framework.

## File Structure

```
server/
├── Server.kt      ← Main server logic and configuration
├── Models.kt      ← Data classes and response models
├── Utils.kt       ← Utility functions
└── README.md      ← This documentation
```

## Quick Start

### Basic Usage in Your Android App

```kotlin
// In your Activity or Service
val server = buildServer(8080)
server.start(wait = false)

// Get server URL to share
val ip = getLocalIpAddress()
val serverUrl = "http://$ip:8080"

// Stop server when done
server.stop(1000, 2000)
```

### Assets Folder Structure

```
app/src/main/assets/
└── web/              ← Put your web files here
    ├── index.html
    ├── style.css
    ├── script.js
    └── images/
        └── logo.png
```

## Code Breakdown

### Models.kt - Data Classes and Response Handling

**What they do:** Define the structure for API responses

```kotlin
ApiResponse<T>     // Base class for all responses
Success<T>         // For successful API calls
Failure            // For error responses
```

**When to modify:**

- Keep these - they provide consistent API responses
- Add more response types if needed
- Don't remove unless you handle JSON responses differently

**What it does:** Serves static files (HTML, CSS, JS, images) from your Android app's assets folder

```kotlin
AssetsResourceProvider  // Loads files from assets
AssetResource          // Represents a loaded file
respondAsset()         // Serves file with caching
respondAssetNoCache()  // Serves file without caching
```

**When to modify:**

- Keep if you want to serve a web interface
- Change `"web"` parameter to match your assets subfolder
- Use `respondAssetNoCache()` for development
- Remove entirely if you only need API endpoints

### Utils.kt - Network Utilities

**What it does:** Gets your device's local IP address

```kotlin
getLocalIpAddress() // Returns device IP like "192.168.1.100"
```

**When to modify:**

- Keep - useful for showing users the server URL
- Remove if you only use localhost/127.0.0.1

### Server.kt - Main Server Logic

#### Server Builder

**What it does:** Main entry point - creates and configures your server

```kotlin
buildServer(port) // Creates server on specified port
```

**When to modify:**

- Always keep this - it's your server's main function
- Add custom configuration if needed

#### Server Configuration

**What it does:** Sets up server features and middleware

#### CORS Configuration

- **Purpose:** Allows web browsers to access your API
- **Remove if:** Your app doesn't need web browser access
- **Security note:** Currently allows all origins - restrict in production

#### OpenAPI Documentation

- **Purpose:** Generates API documentation
- **Remove if:** You don't need Swagger docs

#### Error Handling

- **Purpose:** Catches errors and returns proper JSON responses
- **Keep:** Essential for robust API
- **Modify:** Add specific error types as needed

#### JSON Configuration

- **Purpose:** Handles JSON serialization/deserialization
- **Keep:** Required for JSON APIs
- **Modify:** Adjust settings (set `prettyPrint = false` for production)

#### Request Logging

- **Purpose:** Logs API requests for debugging
- **Remove if:** You don't need request logs
- **Modify:** Change log level or format

#### URL Routing

**What it does:** Defines URL endpoints and what they return

#### API Routes

```
GET /api/status     → Health check
GET /api/json       → OpenAPI schema
GET /api/swagger    → Swagger UI
```

**Add your endpoints here:**

```kotlin
route("/api") {
    get("/users") { /* get all users */ }
    post("/users") { /* create user */ }
    get("/users/{id}") { /* get specific user */ }
    // ... more endpoints
}
```

#### Static File Routes

- **Root redirect:** `/` → `/index.html`
- **File serving:** `/{file...}` → serves from assets/web/

**When to modify:**

- Always add your API endpoints in the `/api` route
- Change redirect target or remove if not needed
- Remove static file serving if you don't serve web content

## Customization Guide

### Adding New API Endpoints

```kotlin
route("/api") {
    // Your existing endpoints...
    
    get("/hello") {
        call.respond(Success(data = "Hello World!"))
    }
    
    post("/users") {
        // Get request body, validate, save to database
        val user = call.receive<User>()
        // ... business logic ...
        call.respond(Success(data = user))
    }
}
```

### Changing Static File Location

Change the `AssetsResourceProvider` parameter:

```kotlin
val webFiles = AssetsResourceProvider("webapp") // assets/webapp/ instead of assets/web/
```

### Adding Authentication

```kotlin
// In configureServer()
install(Authentication) {
    bearer("auth-bearer") {
        authenticate { tokenCredential ->
            // Validate token and return Principal or null
        }
    }
}

// In routes
authenticate("auth-bearer") {
    get("/protected") {
        call.respond(Success(data = "Protected data"))
    }
}
```

### Production Considerations

1. **Security:**
   ```kotlin
   install(CORS) {
       allowHost("yourdomain.com", schemes = listOf("https"))
       // Remove anyHost() and allowCredentials for production
   }
   ```

2. **Performance:**
   ```kotlin
   json(Json {
       prettyPrint = false  // Smaller responses
       ignoreUnknownKeys = true
   })
   ```

3. **Logging:**
   ```kotlin
   install(CallLogging) {
       level = Level.WARN  // Less verbose logging
   }
   ```

## Available Endpoints

Once your server is running:

- **Health Check:** `GET http://YOUR_IP:8080/api/status`
- **API Documentation:** `http://YOUR_IP:8080/api/swagger`
- **Web Interface:** `http://YOUR_IP:8080/` (if you have assets/web/index.html)

## Security Notes

- Server is accessible to any device on your local network
- No authentication is implemented by default
- CORS allows all origins (restrict for production)
- Don't expose sensitive data without proper security measures

## Dependencies

The code uses these Ktor dependencies (add to your `build.gradle`):

```kotlin
implementation("io.ktor:ktor-server-core:$ktor_version")
implementation("io.ktor:ktor-server-cio:$ktor_version")
implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
implementation("io.ktor:ktor-server-cors:$ktor_version")
implementation("io.ktor:ktor-server-call-logging:$ktor_version")
implementation("io.ktor:ktor-server-status-pages:$ktor_version")
implementation("io.github.smiley4:ktor-swagger-ui:$swagger_version")
```

## Common Use Cases

1. **API-only server:** Remove static file serving and assets handling
2. **Web app server:** Keep everything, focus on the `/api` routes
3. **Development server:** Use `respondAssetNoCache()` for immediate file updates
4. **Production server:** Add authentication, restrict CORS, disable pretty printing

## Troubleshooting

- **Server won't start:** Check if port is already in use
- **Can't access from other devices:** Ensure devices are on same network
- **404 for static files:** Check assets/web/ folder exists with files
- **CORS errors:** Adjust CORS configuration for your client's domain