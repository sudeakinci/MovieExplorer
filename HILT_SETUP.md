# Hilt Dependency Injection Kurulumu

Bu dokümanda Hilt'i projenize nasıl ekledik ve nasıl yapılandırdık açıklanmıştır.

## Yapılan Değişiklikler

### 1. Project-level build.gradle.kts
```kotlin
plugins {
    // ...existing plugins...
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}
```

### 2. App-level build.gradle.kts
```kotlin
plugins {
    // ...existing plugins...
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

dependencies {
    // Hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
}
```

### 3. libs.versions.toml
```toml
[versions]
hilt = "2.48.1"

[libraries]
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.1.0" }
```

### 4. Application Class (@HiltAndroidApp)
```kotlin
@HiltAndroidApp
class MovieApplication : Application() {
    // Application kodları
}
```

### 5. MainActivity (@AndroidEntryPoint)
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Activity kodları
}
```

### 6. ViewModel (@HiltViewModel)
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // ViewModel kodları
}
```

### 7. Repository (@Singleton)
```kotlin
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // Repository kodları
}
```

### 8. Firebase Module (Dependency Provider)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
```

### 9. Composable Screens (hiltViewModel kullanımı)
```kotlin
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Screen kodları
}
```

## Hilt'in Faydaları

1. **Dependency Injection**: Bağımlılıkları otomatik olarak enjekte eder
2. **Lifecycle Awareness**: Android lifecycle'ına uyumlu çalışır
3. **Compile-time Safety**: Derleme zamanında hataları yakalar
4. **Testing**: Test yazımını kolaylaştırır
5. **Memory Management**: Bellek yönetimini optimize eder

## Kullanım

Artık tüm bağımlılıklar Hilt tarafından otomatik olarak enjekte edilecektir:

- `AuthRepository` Firebase Auth ve Firestore'u otomatik alır
- `AuthViewModel` AuthRepository'yi otomatik alır
- `MainActivity` ve diğer screen'ler hiltViewModel() kullanarak ViewModel'leri alır

## Build ve Test

Değişiklikleri uygulamak için:

1. Projeyi temizleyin: `Build > Clean Project`
2. Rebuild yapın: `Build > Rebuild Project`
3. Uygulamayı çalıştırın

Hilt kurulumu tamamlanmıştır! 🎉
