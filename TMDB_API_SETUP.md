# TMDB API Setup Instructions

## Getting Your TMDB API Key

1. **Create a TMDB Account**
   - Go to [https://www.themoviedb.org/](https://www.themoviedb.org/)
   - Click "Sign Up" and create an account
   - Verify your email address

2. **Get Your API Key**
   - Go to [https://www.themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)
   - Click "Create" under "Request an API Key"
   - Choose "Use for Educational Purpose or Personal Use"
   - Fill out the form with your application details
   - Accept the terms and conditions
   - Your API key will be generated

3. **Add API Key to Your App**
   - Open `app/src/main/java/com/example/moviemobileproject/utils/Constants.kt`
   - Replace `YOUR_TMDB_API_KEY_HERE` with your actual API key:
   ```kotlin
   const val TMDB_API_KEY = "your_actual_api_key_here"
   ```

## Features Added

### 1. TMDB API Integration
- **Popular Movies**: Fetches real popular movies from TMDB
- **Category Movies**: Gets movies by genre using TMDB API
- **Search**: Searches movies using TMDB database
- **Fallback**: Falls back to local Firebase data if API fails

### 2. Layout Improvements
- **Dashboard**: Popular movies now show in 2 columns grid instead of horizontal scroll
- **Saved Movies**: Already uses 2 columns grid layout
- **Category Screen**: Movies display in 2 columns per row

### 3. Save/Unsave Functionality
- **Auto-Refresh**: When you unlike/unsave a movie, it automatically removes from saved movies screen
- **Real-time Updates**: Saved movies list updates immediately when movies are added or removed
- **Proper State Management**: Uses ViewModel to manage saved movies state

## API Endpoints Used

- `GET /movie/popular` - Get popular movies
- `GET /movie/top_rated` - Get top rated movies
- `GET /movie/now_playing` - Get now playing movies
- `GET /movie/upcoming` - Get upcoming movies
- `GET /discover/movie` - Get movies by genre
- `GET /search/movie` - Search movies
- `GET /movie/{id}` - Get movie details

## Movie Categories Mapping

The app maps TMDB genre IDs to our categories:
- Action (28)
- Comedy (35)
- Drama (18)
- Horror (27)
- Romance (10749)
- Sci-Fi (878)
- Thriller (53)
- Animation (16)
- Adventure (12)
- Crime (80)

## Error Handling

The app implements a 3-tier fallback system:
1. **First**: Try TMDB API
2. **Second**: Fall back to Firebase data
3. **Third**: Use local sample data

This ensures the app works even when:
- No internet connection
- TMDB API is down
- Invalid API key
- Firebase is unavailable

## Testing

To test the implementation:
1. Make sure you have a valid TMDB API key set in Constants.kt
2. Run the app
3. Check the Dashboard - you should see real movies from TMDB
4. Try searching for movies
5. Browse different categories
6. Save/unsave movies and check the Saved Movies screen

## Notes

- The app requires internet connection to fetch movies from TMDB
- Images are loaded using the TMDB image base URL
- Movie data is cached locally in Firebase when possible
- The layout is optimized for 2 columns display on mobile devices
