package com.example.moviemobileproject.data.model

import com.google.gson.annotations.SerializedName

// Person Details Response from TMDB
data class TmdbPersonDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("biography")
    val biography: String,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("deathday")
    val deathday: String?,
    @SerializedName("place_of_birth")
    val placeOfBirth: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("known_for_department")
    val knownForDepartment: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("gender")
    val gender: Int // 1 = Female, 2 = Male, 0 = Not specified
)

// Person Movie Credits Response from TMDB
data class TmdbPersonMovieCredits(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<TmdbPersonCastMovie>,
    @SerializedName("crew")
    val crew: List<TmdbPersonCrewMovie>
)

data class TmdbPersonCastMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("character")
    val character: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("overview")
    val overview: String
)

data class TmdbPersonCrewMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("job")
    val job: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("overview")
    val overview: String
)

// App models
data class PersonDetails(
    val id: String,
    val name: String,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    val placeOfBirth: String?,
    val profileImageUrl: String,
    val knownForDepartment: String,
    val popularity: Double,
    val gender: String
)

data class PersonMovie(
    val id: String,
    val title: String,
    val character: String?, // For cast
    val job: String?, // For crew
    val department: String?, // For crew
    val posterUrl: String,
    val releaseDate: String,
    val rating: Double,
    val overview: String
)

// Extension functions to convert TMDB responses to app models
fun TmdbPersonDetails.toPersonDetails(): PersonDetails {
    return PersonDetails(
        id = this.id.toString(),
        name = this.name,
        biography = this.biography,
        birthday = this.birthday,
        deathday = this.deathday,
        placeOfBirth = this.placeOfBirth,
        profileImageUrl = if (this.profilePath != null) "https://image.tmdb.org/t/p/w500${this.profilePath}" else "",
        knownForDepartment = this.knownForDepartment,
        popularity = this.popularity,
        gender = when (this.gender) {
            1 -> "Female"
            2 -> "Male"
            else -> "Not specified"
        }
    )
}

fun TmdbPersonCastMovie.toPersonMovie(): PersonMovie {
    return PersonMovie(
        id = this.id.toString(),
        title = this.title,
        character = this.character,
        job = null,
        department = null,
        posterUrl = if (this.posterPath != null) "https://image.tmdb.org/t/p/w500${this.posterPath}" else "",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        overview = this.overview
    )
}

fun TmdbPersonCrewMovie.toPersonMovie(): PersonMovie {
    return PersonMovie(
        id = this.id.toString(),
        title = this.title,
        character = null,
        job = this.job,
        department = this.department,
        posterUrl = if (this.posterPath != null) "https://image.tmdb.org/t/p/w500${this.posterPath}" else "",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        overview = this.overview
    )
}
