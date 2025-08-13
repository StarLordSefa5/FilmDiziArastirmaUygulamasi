package com.example.movietracker.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.example.movietracker.model.Title;

public class TmdbClient {
	 // TODO: Kendi TMDb API anahtarını buraya yaz.
    // https://www.themoviedb.org/settings/api
    private static final String API_KEY  = "329df799d3966e9d824cd483eeddc48c";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    /**
     * Verilen sorguya göre film/dizi arar.
     * Not: Basit string listesi döner; ileride model sınıfına çevirebiliriz.
     */
    public static List<Title> searchTitles(String query) {
        List<Title> results = new ArrayList<>();

        if (API_KEY == null || API_KEY.isBlank() || API_KEY.contains("BURAYA")) {
            results.add(new Title(0, "API anahtarı eksik", "", "", "", 0, "TmbdClient.API_KEY içine anahtarını yaz."));
            return results;
        }
        HttpURLConnection conn = null;
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String urlStr = BASE_URL + "/search/multi?api_key=" + API_KEY + "&language=tr-TR&query=" + encoded;

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(12_000);
            conn.setReadTimeout(12_000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "MovieTracker/1.0");
            
            int code = conn.getResponseCode();
            InputStream is = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
            String jsonText = readAll(is);
            if (code < 200 || code >= 300) {
                results.add(new Title(0, "TMDb hata: " + code, "", "", "", 0, jsonText));
                return results;
            }

            JsonObject root = JsonParser.parseString(jsonText).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("results");
            if (arr == null || arr.size() == 0) {
                results.add(new Title(0, "Sonuç bulunamadı.", "", "", "", 0, ""));
                return results;
            }

            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject obj = el.getAsJsonObject();

                String mediaType = getString(obj, "media_type"); // movie/tv/person
                if ("person".equals(mediaType)) continue; // Kişileri atla

                int id = obj.has("id") ? obj.get("id").getAsInt() : 0;

                String title = getString(obj, "title");
                if (title.isBlank()) title = getString(obj, "name");

                String release = getString(obj, "release_date");
                String firstAir = getString(obj, "first_air_date");
                String date = !release.isBlank() ? release : firstAir;
                String year = (!date.isBlank() && date.length() >= 4) ? date.substring(0, 4) : "";

                String posterPath = getString(obj, "poster_path");
                double rating = 0;
                try { rating = obj.get("vote_average").getAsDouble(); } catch (Exception ignored) {}

                String overview = getString(obj, "overview");

                if (!title.isBlank()) {
                    results.add(new Title(id, title, year, mediaType, posterPath, rating, overview));
                }
            }

        } catch (IOException e) {
            results.add(new Title(0, "Ağ/IO hatası", "", "", "", 0, e.getMessage()));
        } catch (Exception e) {
            results.add(new Title(0, "Beklenmeyen hata", "", "", "", 0, e.getMessage()));
        } finally {
            if (conn != null) conn.disconnect();
        }
        return results;
    }

    // -------- yardımcılar --------

    private static String readAll(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String getString(JsonObject o, String key) {
        try {
            if (o.has(key) && !o.get(key).isJsonNull()) {
                return o.get(key).getAsString().trim();
            }
        } catch (Exception ignored) {}
        return "";
    }

    private static String safeTrim(String s) {
        if (s == null) return "";
        String t = s.trim();
        // Hata gövdesi çok uzunsa kısalt:
        return t.length() > 240 ? t.substring(0, 240) + "..." : t;
    }
}
