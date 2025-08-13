package com.example.movietracker.db;
import com.example.movietracker.model.Title;
import java.sql.*;
import java.util.*;
public class WatchlistRepository {
	
	   // KAYDET: FAVORITE veya WATCH
    public void add(Title t, String listType) throws SQLException {
        String sql = "INSERT OR IGNORE INTO watchlist " +
                     "(tmdb_id, name, year, media_type, poster_path, rating, overview, list_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getId());
            ps.setString(2, t.getName());
            ps.setString(3, t.getYear());
            ps.setString(4, t.getMediaType());
            ps.setString(5, t.getPosterPath());
            ps.setDouble(6, t.getRating());
            ps.setString(7, t.getOverview());
            ps.setString(8, listType);
            ps.executeUpdate();
        }
    }

    // LİSTELE: Seçilen liste tipine göre (FAVORITE veya WATCH)
    public List<Title> list(String listType) throws SQLException {
        String sql = "SELECT tmdb_id, name, year, media_type, poster_path, rating, overview " +
                     "FROM watchlist WHERE list_type=? ORDER BY id DESC";
        List<Title> out = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, listType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Title(
                        rs.getInt("tmdb_id"),
                        rs.getString("name"),
                        rs.getString("year"),
                        rs.getString("media_type"),
                        rs.getString("poster_path"),
                        rs.getDouble("rating"),
                        rs.getString("overview")
                    ));
                }
            }
        }
        return out;
    }
}
