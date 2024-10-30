# NewsNest - News Aggregator Application

## Introduction
Welcome to the NewsNest Application! NewsNest is a dynamic news aggregation platform designed to keep users up-to-date with the latest headlines, categorized topics, and personalized news feeds. This README file will guide you through the steps for installing, running, and navigating the application.

## Youtube Link:
https://youtu.be/-z-qD_MgUbE

## API Documentation Link:
https://opsc7312.nerfdesigns.com/

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
- The application will be installed and launched on your connected device. You will see the NewsNest applications main screen once the app is running.

## 3 Features provided by our application:

### 1. News Feed with Categories Feature
The News Feed provides users with the latest news, tailored to their interests:

- Real-time Updates: Users receive frequent updates from reliable sources such as News24.
- Customizable Categories: Users can explore news from categories like Technology, Sports, Politics, and Entertainment.
- User-Friendly Display: Articles are presented with headlines, images, and brief descriptions.
- Full Article View: Tapping on an article takes users to the full content for more details.

### 2. User Profile Preferences Feature
NewsNest allows users to personalize their experience:

- User Profile: Displays information such as name, email, and profile image which can easily be changed or updated to users wants.
- Dark Mode: Users can switch between Light Mode and Dark Mode based on their preference.
- Terms and Conditions: Easily accessible in the settings for user reference.
- Language Preference: Users can switch between their preferred language for better user engagement.

### 3. Chat Messaging Feature
NewsNest features a real-time chat messaging system that enables logged-in users to communicate with others. The chat feature is designed to allow users to:

- Discuss news in their local area.
- Exchange updates about specific categories of news.
- Share insights and opinions with the community.
- Messages are securely stored in the Firebase database, ensuring safe and reliable communication.

### 4. Push Notifications
Stay up-to-date with real-time push notifications:

- Article Alerts: Users receive notifications about newly released articles in their selected categories.
- Firebase Cloud Messaging (FCM): Notifications are pushed directly to the user's device, even when the app is closed.

### 5. Offline Viewing Mode
Users can now access articles offline:

- Cached Articles: News articles are stored locally, allowing users to view content even without internet connectivity.
- Automatic Syncing: Articles are synced and updated when the device reconnects to the internet, ensuring content is up-to-date.

### 6. Fingerprint Verification for Added Security
NewsNest enhances user security with fingerprint authentication:

- Secure Access: Users can enable fingerprint verification to quickly and securely access the app.
- Convenient Login: Once registered, users can log in with a single touch.

### 7. Multi-language Support
Catering to a diverse user base, NewsNest offers multi-language support:

- Language Toggle: Users can switch between languages (currently supports English and Afrikaans) in the settings.
- Localized Experience: The interface and content adapt instantly to the selected language.

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

###vUnit Testing Images
![Screenshot 2024-09-27 203703](https://github.com/user-attachments/assets/21206a5c-ac75-4b3d-ac57-9069e0f3079c)
![Screenshot 2024-09-27 203904](https://github.com/user-attachments/assets/b3f4eea6-e56f-4698-82bb-fb1aa1b37977)
![Screenshot 2024-09-27 204752](https://github.com/user-attachments/assets/ef0e7641-e386-41b5-bd9b-674d55825211)
![Screenshot 2024-09-27 204814](https://github.com/user-attachments/assets/824a92d0-81de-4e53-bcf0-440af3141af0)
![Screenshot 2024-09-27 204831](https://github.com/user-attachments/assets/5dcc6ea1-2098-423a-8333-47c58940ba47)
![Screenshot 2024-09-27 205040](https://github.com/user-attachments/assets/96681f58-40f4-4f33-bd15-06e672fcf620)
![Screenshot 2024-09-27 205114](https://github.com/user-attachments/assets/e0554ee5-baf3-42f7-8e13-80248efa7ba7)
![Screenshot 2024-09-27 205202](https://github.com/user-attachments/assets/31f64563-71de-495b-a75c-f439fb2b6ea0)

  
  
## Conclusion
We hope you enjoy your experience with NewsNest! For any issues or questions, please refer to the documentation or video provided with our submission.
