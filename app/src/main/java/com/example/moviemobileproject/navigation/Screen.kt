package com.example.moviemobileproject.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    object SavedMovies : Screen("saved_movies")
    object Profile : Screen("profile")
}
