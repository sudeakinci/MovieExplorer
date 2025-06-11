 Movie App - INSTRUCTION.md
📱 Uygulama Özeti
Bu uygulama, kullanıcıların popüler filmleri görüntüleyebileceği, kategorilere göre filtreleyebileceği, arama yapabileceği ve kendi favori listelerini oluşturabileceği bir Movie App'tir. Ayrıca kullanıcılar kayıt olup giriş yapabilir ve profil bilgilerini görebilir.

📂 Proje Yapısı
css
Copy
Edit
movie-app/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourapp/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   ├── SignupScreen.kt
│   │   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   │   ├── CategoryScreen.kt
│   │   │   │   │   │   ├── SearchScreen.kt
│   │   │   │   │   │   ├── SavedMoviesScreen.kt
│   │   │   │   │   │   └── ProfileScreen.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── repository/
│   │   │   │   ├── firebase/
│   │   │   │   └── navigation/
│   │   │   └── res/
│   │   │       └── layout/
│   │   └── AndroidManifest.xml
├── build.gradle
└── README.md
✅ Gereksinimler
Android Studio

Kotlin

Jetpack Compose

Firebase (Authentication, Firestore, Storage)

Retrofit (Film verilerini almak için opsiyonel)

Glide/Coil (Görsel yükleme için)

🔐 Authentication
Firebase Auth kullanılacak.

SignupScreen.kt:

Email ve şifre ile kayıt.

Firebase'e kayıt başarılı ise dashboard'a yönlendirme.

LoginScreen.kt:

Giriş yapılır.

Başarısız olursa hata mesajı.

🏠 DashboardScreen.kt
Üstte bir Search Bar

Altında Popüler Filmler Carousel (Firestore’dan ya da dış API)

Kategori sekmeleri (örneğin: Aksiyon, Komedi, Romantik)

Sekmeye tıklanınca CategoryScreen'e yönlendirme

🔍 SearchScreen.kt
Search bar’a girilen keyword’e göre Firebase Firestore’dan film ismine göre arama yapılır.

Eşleşen sonuçlar listelenir.

🎞️ CategoryScreen.kt
Seçilen kategoriye göre Firestore’dan veriler çekilir.

Filmler grid/list olarak gösterilir.

💾 SavedMoviesScreen.kt
Kullanıcının favoriye eklediği filmler burada listelenir.

Firestore'da users/{userId}/saved_movies altında tutulabilir.

👤 ProfileScreen.kt
Kullanıcının email bilgisi görüntülenir.

Profil resmi eklemek opsiyoneldir.

Oturumu kapatma butonu bulunur.

🔁 Navigation
Jetpack Compose Navigation kullanılarak aşağıdaki akış uygulanır:

rust
Copy
Edit
LoginScreen -> SignupScreen
             -> DashboardScreen
                  -> SearchScreen
                  -> CategoryScreen
                  -> SavedMoviesScreen
                  -> ProfileScreen
🧠 Firebase Firestore Yapısı (Önerilen)
yaml
Copy
Edit
collections:
  movies: [
    {
      id: "movieId",
      title: "Inception",
      category: "Action",
      isPopular: true,
      imageUrl: "...",
      description: "...",
    }
  ]

  users:
    userId:
      saved_movies: [
        {
          movieId: "...",
          title: "...",
          imageUrl: "..."
        }
      ]
✨ Ekstra Özellikler (Opsiyonel)
Profil resmi yükleme (Firebase Storage)

Tema modu (Dark/Light)

Film detay sayfası

Swipe to delete (favoriler için)

🚀 Başlangıç Talimatları
Firebase projesi oluştur.

Authentication: Email/Password etkinleştir.

Firestore yapılandırması yap.

Android Studio’da proje oluştur ve Firebase SDK’larını kur.

Navigation ve temel UI bileşenlerini yerleştir.

Sayfaları tek tek geliştir ve bağla.