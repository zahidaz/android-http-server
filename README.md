# Android HTTP Server

A sample Android application that demonstrates how to run an embedded HTTP server inside an Android app using Ktor CIO engine.

[View App Screenshot](docs/screenshot.png)


## Features

- Embedded HTTP server running on Android device
- RESTful API with status endpoint
- Auto-generated Swagger UI documentation
- Static file serving from assets
- CORS support
- Custom error handling
- Foreground service for background operation
- Real-time server status updates

## Tech Stack

- **Kotlin** - Primary programming language
- **Ktor CIO** - Embedded HTTP server engine
- **Jetpack Compose** - Modern Android UI toolkit
- **Kotlinx Serialization** - JSON serialization
- **OpenAPI/Swagger** - API documentation

## Dependencies

The project uses the following key dependencies:

### Project level build.gradle.kts
```kotlin
plugins {
    // add for serialization support (optional)
    id ("org.jetbrains.kotlin.plugin.serialization") version "2.2.10"
}

```

### Ktor Server Components
```kotlin
implementation("io.ktor:ktor-server-cio:3.2.3")
implementation("io.ktor:ktor-server-core:3.2.3")
implementation("io.ktor:ktor-server-content-negotiation:3.2.3") // (optional)
implementation("io.ktor:ktor-server-call-logging:3.2.3") // (optional)
implementation("io.ktor:ktor-server-cors:3.2.3") // (optional)
implementation("io.ktor:ktor-server-status-pages:3.2.3") // (optional)
implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3") // (optional)
```

### OpenAPI and Swagger (optional)
```kotlin
implementation("io.github.smiley4:ktor-openapi:5.2.0")
implementation("io.github.smiley4:ktor-swagger-ui:5.2.0")
```
## Required Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

## Service Configuration

Register the HTTP server service in your manifest:

```xml
<service
    android:name=".HttpServerService"
    android:enabled="true"
    android:exported="false"
    android:foregroundServiceType="specialUse" />
```

## Project Structure

```
app/src/main/java/com/dihax/androidhttpserver/
├── MainActivity.kt              # Main activity with Compose UI
├── HttpServerService.kt         # Foreground service for HTTP server
├── MyApplication.kt             # Application class
└── server/
    ├── Server.kt               # Ktor server configuration
    ├── Models.kt               # Data models and API responses
    └── Utils.kt                # Utility functions

app/src/main/assets/web/
├── index.html                  # Landing page
└── style.css                   # Styles for web interface
```

## Key Components

### 1. HTTP Server Service (HttpServerService.kt)

Manages the Ktor server lifecycle as a foreground service:

- Starts/stops the HTTP server on demand
- Runs as foreground service with notifications
- Communicates server status to UI via SharedFlow
- Handles service binding for UI interaction

### 2. Server Configuration (Server.kt)

Configures the Ktor server with:

- **Content Negotiation**: JSON serialization with Kotlinx
- **CORS**: Cross-origin resource sharing
- **Status Pages**: Custom error handling
- **Call Logging**: Request logging for API endpoints
- **OpenAPI**: Auto-generated API documentation
- **Static Files**: Serves files from assets/web directory

### 3. API Endpoints

- `GET /api/status` - Health check endpoint
- `GET /api/json` - OpenAPI JSON specification
- `GET /api/swagger` - Swagger UI interface
- `GET /` - Redirects to index.html
- `GET /{file...}` - Static file serving

## Installation and Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd android-http-server
   ```

2. **Open in Android Studio**
   - Import the project in Android Studio
   - Sync Gradle files

3. **Build and run**
   - Connect Android device or start emulator
   - Build and install the app

## Usage

1. **Grant Permissions**
   - Open the app
   - Tap "Grant Permissions" to allow notifications

2. **Start Server**
   - Enter desired port number (default: 8080)
   - Tap "Start Server"
   - Server runs in background with notification

3. **Access Server**
   - Copy the server address shown in the app
   - Open in any web browser on the same network
   - Access API documentation at `/api/swagger`

4. **Stop Server**
   - Tap "Stop Server" when finished

## Network Access

The server binds to all network interfaces, making it accessible from:
- The device itself (localhost)
- Other devices on the same WiFi network
- Any client that can reach the device's IP address

## API Documentation

When the server is running, visit `/api/swagger` for interactive API documentation generated from OpenAPI specifications.

## Building for Production

The app includes proper resource exclusions for Ktor OpenAPI library to ensure successful builds:

```kotlin
packaging {
    resources {
        excludes += arrayOf(
            "META-INF/ASL-2.0.txt",
            "draftv4/schema",
            "META-INF/DEPENDENCIES",
            // ... other exclusions
        )
    }
}
```

## Security Considerations

- Server runs on local network only
- No authentication implemented (for demo purposes)
- Consider adding authentication for production use
- Firewall rules may affect external access

## Requirements

- Android API Level 24+ (Android 7.0)
- Network connectivity (WiFi or mobile data)
- Storage for temporary files (if needed)

## Contributing

This is a sample project demonstrating embedded HTTP server capabilities in Android. Feel free to fork and modify for your specific use case.

## License

This project is provided as-is for educational and demonstration purposes.