package com.example.movietracker.db;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
	
	// SQLite dosyanın yolu
    private static final String DB_URL = "jdbc:sqlite:movietracker.db";

    static {
        // Uygulama başlarken tabloyu oluştur
        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement st = c.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS watchlist (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " tmdb_id INTEGER NOT NULL," +
                " name TEXT NOT NULL," +
                " year TEXT," +
                " media_type TEXT," +
                " poster_path TEXT," +
                " rating REAL," +
                " overview TEXT," +
                " list_type TEXT NOT NULL CHECK(list_type IN ('FAVORITE','WATCH'))," +
                " UNIQUE(tmdb_id, list_type)" +
                ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // İşte bu metot sayesinde WatchlistRepository çağırabilecek
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    }


