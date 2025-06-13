package com.example.moviemobileproject.data.model

import com.example.moviemobileproject.utils.Constants
import com.google.gson.annotations.SerializedName

data class TmdbMovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<TmdbMovie>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class TmdbMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("video")
    val video: Boolean
)

data class TmdbMovieDetailsResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("genres")
    val genres: List<TmdbGenre>,
    @SerializedName("production_companies")
    val productionCompanies: List<TmdbProductionCompany>,
    @SerializedName("budget")
    val budget: Long,
    @SerializedName("revenue")
    val revenue: Long,
    @SerializedName("tagline")
    val tagline: String?
)

data class TmdbGenre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class TmdbProductionCompany(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("origin_country")
    val originCountry: String
)

data class MovieDetails(
    val id: String,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String,
    val rating: Double,
    val voteCount: Int,
    val runtime: Int,
    val genres: List<String>,
    val trailerUrl: String?,
    val cast: List<CastMember>,
    val tagline: String?
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)

// TMDB API Response Models for Videos and Credits
data class TmdbVideoResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<TmdbVideo>
)

data class TmdbVideo(
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("site")
    val site: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("official")
    val official: Boolean
)

data class TmdbCreditsResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<TmdbCast>,
    @SerializedName("crew")
    val crew: List<TmdbCrew>
)

data class TmdbCast(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("character")
    val character: String,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("order")
    val order: Int
)

data class TmdbCrew(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("job")
    val job: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

// Extension function to convert TMDB movie to our app's Movie model
fun TmdbMovie.toMovie(): Movie {
    return Movie(
        id = this.id.toString(),
        title = this.title,
        category = getCategoryFromGenreIds(this.genreIds),
        isPopular = this.popularity > 100.0, // Consider movies with popularity > 100 as popular
        imageUrl = if (this.posterPath != null) "${Constants.TMDB_IMAGE_BASE_URL}${this.posterPath}" else "",
        description = this.overview,
        rating = this.voteAverage,
        releaseYear = extractYearFromDate(this.releaseDate),
        director = "", // Director info not available in basic movie response
        cast = emptyList(), // Cast info not available in basic movie response
        genre = getGenreNamesFromIds(this.genreIds),
        duration = 0 // Duration not available in basic movie response
    )
}

private fun getCategoryFromGenreIds(genreIds: List<Int>): String {
    // Use the constants mapping for better maintainability
    return when {
        genreIds.contains(28) -> "Action"
        genreIds.contains(35) -> "Comedy"
        genreIds.contains(18) -> "Drama"
        genreIds.contains(27) -> "Horror"
        genreIds.contains(10749) -> "Romance"
        genreIds.contains(878) -> "Sci-Fi"
        genreIds.contains(53) -> "Thriller"
        genreIds.contains(16) -> "Animation"
        genreIds.contains(12) -> "Adventure"
        genreIds.contains(80) -> "Crime"
        else -> "Drama" // Default category
    }
}

private fun getGenreNamesFromIds(genreIds: List<Int>): List<String> {
    val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )
    
    return genreIds.mapNotNull { genreMap[it] }
}

private fun extractYearFromDate(dateString: String): Int {
    return try {
        if (dateString.isNotEmpty()) {
            dateString.substring(0, 4).toInt()
        } else {
            0
        }
    } catch (e: Exception) {
        0
    }
}

// Extension function to convert TMDB movie details to our app's MovieDetails model
fun TmdbMovieDetailsResponse.toMovieDetails(trailerKey: String?, cast: List<TmdbCast>): MovieDetails {
    return MovieDetails(
        id = this.id.toString(),
        title = this.title,
        overview = this.overview,
        posterUrl = if (this.posterPath != null) "${Constants.TMDB_IMAGE_BASE_URL}${this.posterPath}" else "",
        backdropUrl = if (this.backdropPath != null) "https://image.tmdb.org/t/p/w1280${this.backdropPath}" else "",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        voteCount = this.voteCount,
        runtime = this.runtime ?: 0,
        genres = this.genres.map { it.name },
        trailerUrl = if (trailerKey != null) "https://www.youtube.com/watch?v=$trailerKey" else null,
        cast = cast.take(10).map { 
            CastMember(
                id = it.id,
                name = it.name,
                character = it.character,
                profilePath = if (it.profilePath != null) "https://image.tmdb.org/t/p/w185${it.profilePath}" else null
            )
        },
        tagline = this.tagline
    )
}
