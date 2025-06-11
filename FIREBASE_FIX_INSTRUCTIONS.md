# Firebase Configuration Fix

## Problem
Your Firebase authentication is failing because the `google-services.json` file contains placeholder/dummy data instead of real Firebase project configuration.

## Steps to Fix:

### 1. Create a Real Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or use an existing project
3. Enter project name (e.g., "movie-mobile-project")
4. Enable Google Analytics (optional)
5. Click "Create project"

### 2. Add Android App to Firebase Project
1. In your Firebase project, click "Add app" and select Android
2. Enter your package name: `com.example.moviemobileproject`
3. Enter app nickname (optional): "Movie Mobile App"
4. Download the `google-services.json` file
5. Replace the existing `app/google-services.json` with the downloaded file

### 3. Enable Authentication
1. In Firebase Console, go to "Authentication" > "Get Started"
2. Go to "Sign-in method" tab
3. Enable "Email/Password" authentication
4. Click "Save"

### 4. Enable Firestore Database
1. In Firebase Console, go to "Firestore Database" > "Create database"
2. Choose "Start in test mode" for now
3. Select a location close to your users
4. Click "Done"

### 5. Configure Firestore Rules (for development)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write access to all documents for authenticated users
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 6. Test the Configuration
After replacing the `google-services.json` file, rebuild your app and try authentication.

## Important Notes
- Never commit real Firebase configuration to public repositories
- Use environment-specific configurations for production
- The current `google-services.json` has dummy data and won't work with real Firebase services
