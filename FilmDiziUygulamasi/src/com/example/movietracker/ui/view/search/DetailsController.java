package com.example.movietracker.ui.view.search;
import com.example.movietracker.model.Title;
import com.example.movietracker.db.WatchlistRepository; // ADD

import javafx.fxml.FXML;
import javafx.scene.control.Button;      // ADD
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DetailsController {
	  @FXML private ImageView posterView;
	    @FXML private Label titleLabel;
	    @FXML private Label metaLabel;
	    @FXML private TextArea overviewArea;

	    // ADD: FXML'deki butonlar
	    @FXML private Button favBtn;
	    @FXML private Button watchBtn;

	    private static final String IMG_BASE = "https://image.tmdb.org/t/p/w342";

	    // ADD: Repo ve mevcut seçim
	    private final WatchlistRepository repo = new WatchlistRepository();
	    private Title current;

	    public void setData(Title t) {
	        current = t; // ADD: Kaydetme için elde tut

	        // Başlık + meta
	        titleLabel.setText(t.getName());
	        String type = t.getMediaType() == null ? "" :
	                ("movie".equals(t.getMediaType()) ? "Film" :
	                 "tv".equals(t.getMediaType())    ? "Dizi" : t.getMediaType());
	        String yil  = (t.getYear() == null || t.getYear().isBlank()) ? "" : " • " + t.getYear();
	        String puan = (t.getRating() > 0) ? " • ★ " + String.format("%.1f", t.getRating()) : "";
	        metaLabel.setText((type.isBlank() ? "" : type) + yil + puan);

	        // Özet
	        String overview = t.getOverview();
	        overviewArea.setText(overview == null || overview.isBlank() ? "Açıklama bulunamadı." : overview);

	        // Poster (teşhis logu + placeholder ile)
	        if (t.getPosterPath() != null && !t.getPosterPath().isBlank()) {
	            String fullUrl = IMG_BASE + t.getPosterPath();
	            System.out.println("Poster URL = " + fullUrl);
	            posterView.setImage(new Image(fullUrl, true));
	        } else {
	            System.out.println("Poster path boş/null");
	            posterView.setImage(new Image("https://via.placeholder.com/250x375?text=No+Image", true));
	        }

	        // ADD: Buton aksiyonları
	        if (favBtn != null) {
	            favBtn.setOnAction(e -> save("FAVORITE"));
	        }
	        if (watchBtn != null) {
	            watchBtn.setOnAction(e -> save("WATCH"));
	        }
	    }

	    // ADD: Veri kaydet
	    private void save(String type) {
	        try {
	            if (current == null) return;
	            repo.add(current, type);
	            metaLabel.setText(metaLabel.getText() + " • Kaydedildi (" + type + ")");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            metaLabel.setText(metaLabel.getText() + " • KAYIT HATASI");
	        }
	    }
}
