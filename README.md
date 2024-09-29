# NewsNest - News Aggregator Application

## Introduction
Welcome to the NewsNest Application! NewsNest is a dynamic news aggregation platform designed to keep users up-to-date with the latest headlines, categorized topics, and personalized news feeds. This README file will guide you through the steps for installing, running, and navigating the application.
## Getting Started

### What's Required
- Android Studio (latest version)
- Android device with at least Android version 11 or higher

Minimum hardware requirements:
1. x86_64 CPU architecture; 2nd generation Intel Core or newer, or AMD CPU with support for a Windows Hypervisor
2. 8 GB RAM or more
3. 8 GB of available disk space minimum (IDE + Android SDK + Android Emulator)

### How to Install on Your Device
1. Clone Repository:
- Open the NewsNest GitHub repository.
- Click on the green dropdown labeled "Code" and select the "Open with GitHub Desktop" option, or copy the repository URL and clone it using Git.

2. Open in Android Studio:
- Open Android Studio and select "Open an existing project."
- Navigate to the cloned repository and select it to open.

Sync Project:
- Wait for the project to sync and ensure all necessary dependencies are installed. If prompted, restore any missing dependencies.

Build Project:
- Click the "Build" option in the top menu of Android Studio.
- Select "Build Project" and wait for the build process to complete. Ensure there are no errors before proceeding.

Running the Application
- Connect your Android device to your computer using a USB cable.
- Ensure that USB debugging is enabled on your device.
- Or alternatively use a emulator provided by android studio (please note this is not advisable as it slows down the application)

Run Application:
- In Android Studio, select the device from the list of available devices.
- Click the "Run" button (green play icon) in Android Studio.
- Choose whether to run the application with or without debugging (preferably without for faster performance).

Launch Application:
- The application will be installed and launched on your connected device. You will see the ShiftMate main screen once the app is running.

## 2 Features provided by our application:

### 1. Latest News Feed
The Latest News Feed ensures users always have access to breaking news and top stories. This feature includes:

- Real-time Updates: News articles are refreshed frequently from reliable sources such as News24.
- User-Friendly Display: Articles are shown with headlines, images, and brief descriptions.
- Full Article View: Tapping on an article directs users to the full news content.

### 2. News Categories
Users can explore different topics based on their interests using the News Categories feature:

- Customizable Categories: Users can choose from categories such as Technology, Sports, Politics, Entertainment, and more.
- Filtered News: Each category displays news relevant to that topic.

### 3. User Profile and Settings
Personalize your experience with the Profile and Settings features:

- User Profile: Displays user information like name, email, and profile image.
- Dark Mode: Users can switch between Light Mode and Dark Mode based on their preference.
- Terms and Conditions: Easily accessible in the settings for user reference.

### 4. Chat Messaging
NewsNest features a real-time chat messaging system that enables logged-in users to communicate with others. The chat feature is designed to allow users to:

- Discuss news in their local area.
- Exchange updates about specific categories of news.
- Share insights and opinions with the community.
- Messages are securely stored in the Firebase database, ensuring safe and reliable communication.





# OPSC7312-Backend API Documentation

## Version: 1.0.0  
OAS 3.0  
API Documentation: `/api/doc`

## Introduction
This API supports a simple Android application that enables users to view, search, and save news articles to favorites, and access account information. It uses **Hono.js**, a lightweight, high-performance framework hosted on Vercel, ensuring ultra-low latency by serving requests close to users.

## News Source
The API retrieves news from public RSS feeds, with a focus on South African news (currently sourced from News24). The RSS feeds are parsed using **Cheerio** and returned in structured JSON format.

## Pros and Cons
### Pros:
- Efficiency: Uses RSS feeds with minimal setup.
- Flexibility: Cheerio adapts to various feed structures.
- Scalability: Easy to add more sources.
- Focused: South African news enhances user engagement.

### Cons:
- Data Quality: Inconsistent formats.
- Dependency: Relies on third-party feeds.
- Control: Limited over content updates.
- Complexity: Managing multiple feeds can be tricky.

## Tech Stack
- **Hono.js**: Web framework
- **Node.js**: Runtime environment
- **Vercel**: Hosting platform
- **Zod**: Schema validation
- **Cheerio**: RSS feed parsing
- **Jest**: Testing
- **Firebase Auth**: Authentication
- **Firestore**: NoSQL database

## Data Flow
The API communicates via HTTP, receiving and sending JSON. It uses **Zod** for schema validation, ensuring strict type safety and error reduction.

- **Requests**: Handled through URL parameters and request bodies (e.g., user actions like adding to favorites).
- **Responses**: Structured JSON (e.g., articles, confirmation messages).

## Architecture and Design
- **CRUD Operations**: For fetching articles, managing favorites, and accessing user info.
- **Authentication**: Backend authentication ensures secure data access.
- **Business Service Layer**: Manages core business logic and interactions with Firebase.
- **TDD**: Test-driven development ensures stable features.
- **Rate Limiting**: Prevents abuse and ensures API security.

## Hosting
The API is hosted on **Vercel** for its edge network and seamless integration, ensuring fast response times. Firebase services are used for authentication and database management.

- **Simplicity**: Streamlined deployment via Vercel.
- **Efficiency**: Fast and secure data management with Firebase and Vercel’s edge network.
- **Cost-Effective**: Low operational costs through Vercel and Firebase.

## Endpoints
- `GET /api/hello`
- `GET /api/news`
- `GET /api/news/:title`

## Schemas
- **HomeRes**
- **NewsSuccessRes**
- **NewsErrorRes**




## Unit Testing and GitHub Actions

### Overview
This project uses **JUnit** for unit testing and **GitHub Actions** for continuous integration (CI). The CI pipeline builds the project, runs tests, and generates APK/AAB files for both debug and release versions. The project requires **JDK 17**.

### How to Run Tests Locally
1. Ensure you have JDK 17 installed.
2. Open a terminal in the project root.
3. Run the following command to execute unit tests:
   ```bash
   ./gradlew test
## Unit Testing and GitHub Actions

### Running Tests
4. The tests will run, and results will be displayed in the terminal.

### GitHub Actions Workflow
The GitHub Actions workflow is set up to trigger on pushes to the `release` branch or manually from the Actions tab. Here's what the workflow does:

#### Set up Java Environment:
- Uses **JDK 17**.
- Caches **Gradle** for faster builds.

#### Build and Test:
- Runs unit tests with `./gradlew test`.
- Builds the project using `./gradlew build`.

#### APK and AAB Build:
- Generates debug APK with `./gradlew assembleDebug`.
- Generates release APK and AAB with `./gradlew assemble` and `./gradlew bundleRelease`.

#### Upload Artifacts:
- Uploads the generated APK and AAB files as build artifacts for further use.

### Unit Testing
- Unit tests are located in the test directory.
- The testing framework used is **JUnit**.
- Example test classes include:
  - **MainActivityTest**: Tests preference management (e.g., dark mode settings).
  - **MainActivityUITest**: Verifies that MainActivity launches correctly.

### Example Unit Test

#### MainActivityTest
This test validates that the dark mode preference is correctly saved and retrieved:
```kotlin
@Test
fun testDarkModePreference() {
    preferenceManager.putBoolean(Constants.KEY_IS_DARK_MODE, true)
    val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
    assertEquals(true, isDarkMode)
}
```

#### MainActivityUITest
This test ensures that the MainActivity is launched successfully:

```kotlin
@Test
fun testActivityLaunch() {
    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
        scenario.onActivity { activity ->
            assert(activity != null)
        }
    }
}
```


The workflow automatically runs the following steps:

1. **Checkout code**.
2. **Set up JDK 17**.
3. **Run tests** using `./gradlew test`.
4. **Build APK/AAB files** for both debug and release configurations.
5. **Upload APK/AAB artifacts** for download.

### Requirements for CI Pipeline

- **JDK 17** for compilation and testing.
- **Gradle** for project build and dependency management.
- **GitHub Actions** for running the CI pipeline.

  
  
## Conclusion
We hope you enjoy your experience with NewsNest! For any issues or questions, please refer to the documentation or video provided with our submission.