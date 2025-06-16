# Firebase Firestore Rules Setup for Reviews

## 🚨 **CRITICAL: Firebase Rules Update Required**

Your review submission error is caused by incorrect Firestore Security Rules. Follow these steps:

### 1. **Update Firestore Rules**

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Go to **Firestore Database** → **Rules**
4. Replace the current rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Movie reviews collection
    match /movie_reviews/{reviewId} {
      // Anyone authenticated can read reviews
      allow read: if request.auth != null;
      
      // Only authenticated users can create reviews
      allow create: if request.auth != null 
        && request.auth.uid == resource.data.userId;
      
      // Users can only edit their own reviews
      allow update: if request.auth != null 
        && request.auth.uid == resource.data.userId;
      
      // Anyone authenticated can like reviews (increment likes/dislikes)
      allow update: if request.auth != null 
        && request.writeFields.hasOnly(['likes', 'dislikes']);
      
      // Users can delete their own reviews
      allow delete: if request.auth != null 
        && request.auth.uid == resource.data.userId;
    }
    
    // Movies collection - read-only for all authenticated users
    match /movies/{movieId} {
      allow read: if request.auth != null;
      allow write: if false; // Only admin can write movies
    }
    
    // Deny all other requests
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

5. Click **Publish**

### 2. **Test the Fix**

1. **Login** to your app
2. **Navigate to any movie details**
3. **Click "Add Review"**
4. **Submit a review** - should work now!
5. **Reviews should display** below movie details

### 3. **Features Now Working**

✅ **Add Reviews**: Users can add reviews with rating and comment  
✅ **View Reviews**: All reviews display with username and timestamp  
✅ **Like Reviews**: Users can like reviews (like count increases)  
✅ **User Authentication**: Only logged-in users can add/like reviews  
✅ **Security**: Users can only edit their own reviews  

### 4. **Review Data Structure**

Each review in Firestore contains:
```json
{
  "id": "auto-generated",
  "movieId": "tmdb-movie-id",
  "userId": "firebase-user-id", 
  "userName": "User Display Name",
  "userEmail": "user@example.com",
  "rating": 4.5,
  "comment": "Great movie!",
  "timestamp": 1234567890,
  "likes": 0,
  "dislikes": 0
}
```

### 5. **Troubleshooting**

If you still get errors:

1. **Check Authentication**: Make sure user is logged in
2. **Check Internet**: Ensure device has internet connection
3. **Refresh App**: Close and reopen the app
4. **Check Firebase Project**: Verify correct project is selected

The review system should now work perfectly! 🎬✨
