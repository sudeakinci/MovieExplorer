
# 🎬 TMDB API Kullanımı (The Movie Database)

Bu proje, [The Movie Database (TMDB)](https://developer.themoviedb.org) API'sini kullanarak film, dizi ve oyuncular hakkında bilgi çekmek isteyen geliştiriciler için hazırlanmıştır. Aşağıda API'yi kullanmaya başlamak için gerekli tüm adımları ve örnekleri bulabilirsiniz.

## 🚀 Başlangıç

### 1. API Anahtarı Alın
TMDB API'yi kullanabilmek için bir hesap oluşturup API anahtarı edinmeniz gerekir.

- [TMDB Developer Portal](https://developer.themoviedb.org) adresine gidin.
- Giriş yaptıktan sonra API sekmesine giderek bir **API Key (v3)** veya **Access Token (v4)** alın.

### 2. Temel API Bilgileri

- **Base URL (v3):** `https://api.themoviedb.org/3`
- **Yanıt Formatı:** JSON
- **Kimlik Doğrulama:**
  - v3 için: `api_key` query parametresi olarak
  - v4 için: `Authorization: Bearer <access_token>` şeklinde header'da

## 📦 Örnek API İstekleri

### Film Ara
```
GET /search/movie?query=Inception&api_key=YOUR_API_KEY
```

### Popüler Filmler
```
GET /movie/popular?api_key=YOUR_API_KEY
```

### Film Detayı
```
GET /movie/{movie_id}?api_key=YOUR_API_KEY
```

### Dizi Detayı
```
GET /tv/{tv_id}?api_key=YOUR_API_KEY
```

### Oyuncu Bilgisi
```
GET /person/{person_id}?api_key=YOUR_API_KEY
```

## 🔐 Kimlik Doğrulama (v4)

v4 token ile örnek bir istek şu şekilde yapılır:

```
GET /movie/550
Host: api.themoviedb.org
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json;charset=utf-8
```

### cURL Örneği:
```bash
curl --request GET \
  --url https://api.themoviedb.org/3/movie/550 \
  --header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

## 🧩 Sık Kullanılan Endpoint’ler

| Tip         | Endpoint                        | Açıklama                |
|-------------|---------------------------------|--------------------------|
| Film Arama  | `/search/movie`                 | Film adına göre arama   |
| Film Detayı | `/movie/{movie_id}`             | Film bilgisi            |
| Oyuncu      | `/person/{person_id}`           | Oyuncu bilgisi          |
| Dizi        | `/tv/{tv_id}`                   | Dizi detayları          |
| Popüler     | `/movie/popular`, `/tv/popular` | Popüler içerikler       |

## ⚙️ Parametreler

Bazı sık kullanılan parametreler:
- `language`: Yanıt dili (örnek: `tr-TR`, `en-US`)
- `page`: Sayfalama için (1, 2, 3...)
- `region`: Bölgeye göre filtreleme (örnek: `TR`, `US`)
- `include_adult`: Yetişkin içerik dahil et (true/false)

## 📝 Lisans ve Kullanım

TMDB API yalnızca kişisel veya ticari olmayan projeler için kullanılabilir. Tüm içerik TMDB'ye aittir. API'yi kullanırken [TMDB kullanım şartlarına](https://www.themoviedb.org/documentation/api/terms-of-use) uyduğunuzdan emin olun.

## 📚 Kaynaklar

- [TMDB API Belgeleri](https://developer.themoviedb.org/reference/intro/getting-started)
- [API Explorer](https://developer.themoviedb.org/reference)
- [Sık Sorulan Sorular](https://developer.themoviedb.org/docs/faq)

---

Herhangi bir soru ya da geri bildiriminiz olursa TMDB topluluğuna katılabilir veya dokümantasyon sayfasından destek alabilirsiniz.
