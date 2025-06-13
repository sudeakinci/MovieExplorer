package com.example.moviemobileproject.data.model

import com.google.gson.annotations.SerializedName

// Movie Details Response from TMDB
data class TmdbMovieDetailsExtended(
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
    val tagline: String?,
    @SerializedName("homepage")
    val homepage: String?
)

// Movie Videos Response (for trailers)
data class TmdbVideosResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<TmdbVideo>
)

data class TmdbCastMember(
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

data class TmdbCrewMember(
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


// Extension functions to convert TMDB responses to app models
fun TmdbMovieDetailsExtended.toMovieDetails(
    trailerKey: String? = null,
    cast: List<TmdbCastMember> = emptyList()
): MovieDetails {
    return MovieDetails(
        id = this.id.toString(),
        title = this.title,
        overview = this.overview,
        posterUrl = if (this.posterPath != null) "https://image.tmdb.org/t/p/w500${this.posterPath}" else "",
        backdropUrl = if (this.backdropPath != null) "https://image.tmdb.org/t/p/w780${this.backdropPath}" else "",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        voteCount = this.voteCount,
        runtime = this.runtime ?: 0,
        genres = this.genres.map { it.name },
        trailerUrl = if (trailerKey != null) "https://www.youtube.com/watch?v=$trailerKey" else null,
        cast = cast.take(8).map { it.toCastMember() }, // Take first 8 cast members
        tagline = this.tagline
    )
}

fun TmdbCastMember.toCastMember(): CastMember {
    return CastMember(
        id = this.id,
        name = this.name,
        character = this.character,
        profilePath = if (this.profilePath != null) "https://image.tmdb.org/t/p/w185${this.profilePath}" else null
    )
}
