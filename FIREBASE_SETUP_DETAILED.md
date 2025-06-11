# Firebase Kurulum ve Hata Düzeltme Rehberi

## Problem Tanımı
Uygulamanızda Firebase authentication çalışmıyor ve "internal error" hatası alıyorsunuz. Bu durum `google-services.json` dosyasının sahte veriler içermesinden kaynaklanıyor.

## Adım Adım Çözüm

### 1. Firebase Projesi Oluşturma
1. [Firebase Console](https://console.firebase.google.com/)'a gidin
2. "Proje oluştur" butonuna tıklayın
3. Proje adı girin (örnek: "movie-mobile-project")
4. Google Analytics'i etkinleştirin (isteğe bağlı)
5. "Projeyi oluştur" butonuna tıklayın

### 2. Android Uygulamasını Firebase'e Ekleme
1. Firebase projenizde "Uygulama ekle" ve Android seçin
2. Paket adını girin: `com.example.moviemobileproject`
3. Uygulama takma adı (isteğe bağlı): "Movie Mobile App"
4. SHA-1 imzasını ekleyin (geliştirme için gerekli değil)
5. `google-services.json` dosyasını indirin

### 3. google-services.json Dosyasını Değiştirme
1. İndirdiğiniz `google-services.json` dosyasını kopyalayın
2. Proje klasöründeki `app/google-services.json` dosyasını yenisiyle değiştirin
3. Dosyanın şu yolda olduğundan emin olun: `app/google-services.json`

### 4. Firebase Authentication Etkinleştirme
1. Firebase Console'da "Authentication" bölümüne gidin
2. "Başlayın" butonuna tıklayın
3. "Sign-in method" sekmesine gidin
4. "Email/Password" seçeneğini etkinleştirin
5. "Kaydet" butonuna tıklayın

### 5. Firestore Database Kurulumu
1. Firebase Console'da "Firestore Database" bölümüne gidin
2. "Veritabanı oluştur" butonuna tıklayın
3. "Test modunda başla" seçeneğini seçin (geliştirme için)
4. Kullanıcılarınıza yakın bir konum seçin
5. "Tamam" butonuna tıklayın

### 6. Firestore Güvenlik Kuralları (Geliştirme İçin)
Firebase Console'da Firestore > Rules bölümünde şu kuralları ekleyin:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Kimlik doğrulaması yapan kullanıcılar için okuma/yazma izni
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 7. Uygulamayı Yeniden Derleme
1. Android Studio'da projeyi temizleyin: `Build > Clean Project`
2. Projeyi yeniden derleyin: `Build > Rebuild Project`
3. Uygulamayı çalıştırın

## Test Etme
1. Uygulamayı açın
2. Kayıt ol ekranından yeni bir hesap oluşturun
3. Giriş yapma işlemini test edin
4. Firebase Console'da Authentication bölümünde kullanıcıların göründüğünü kontrol edin

## Olası Hatalar ve Çözümleri

### "Internal Error" Hatası
- `google-services.json` dosyasının gerçek veriler içerdiğinden emin olun
- Paket adının (`com.example.moviemobileproject`) doğru olduğunu kontrol edin
- Uygulamayı temizleyip yeniden derleyin

### "Network Error" Hatası
- İnternet bağlantınızı kontrol edin
- Firebase projesinin aktif olduğundan emin olun
- Emulator kullanıyorsanız, network ayarlarını kontrol edin

### "User Not Found" veya "Wrong Password" Hatası
- Bu normal hata mesajlarıdır, Firebase doğru çalışıyor demektir
- Doğru email ve şifre kombinasyonunu kullandığınızdan emin olun

## Güvenlik Notları
- **Asla** gerçek Firebase yapılandırmasını public repository'lere commit etmeyin
- Production için güvenlik kurallarını güncelleyin
- API anahtarlarını güvenli tutun

## Debug İçin Faydalı Komutlar
```bash
# Gradle cache temizleme
./gradlew clean

# Uygulama verilerini temizleme (emulator)
adb shell pm clear com.example.moviemobileproject

# Logcat ile Firebase loglarını görüntüleme
adb logcat | grep -i firebase
```

Bu adımları tamamladıktan sonra Firebase authentication çalışmaya başlayacaktır!
