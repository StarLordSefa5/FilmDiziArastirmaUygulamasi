package com.example.movietracker.ui.view.search;
import com.example.movietracker.db.WatchlistRepository;
import com.example.movietracker.model.Title;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Arrays;

public class ListsController {
	
	@FXML private ComboBox<String> typeBox; // FAVORITE / WATCH
    @FXML private ListView<Title> listView;

    private final WatchlistRepository repo = new WatchlistRepository();

    @FXML
    private void initialize() {
        typeBox.getItems().setAll(Arrays.asList("FAVORITE", "WATCH"));
        typeBox.setValue("FAVORITE");
        typeBox.valueProperty().addListener((obs, o, n) -> load(n));

        // Basit görünüm (adı + yıl + ★)
        listView.setCellFactory(lv -> new ListCell<Title>() {
            @Override protected void updateItem(Title t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) { setText(null); return; }
                String yil = (t.getYear()==null||t.getYear().isBlank()) ? "" : " ("+t.getYear()+")";
                String tip = "movie".equals(t.getMediaType()) ? " • Film" :
                             "tv".equals(t.getMediaType())    ? " • Dizi" : "";
                String puan = t.getRating()>0 ? " • ★ " + String.format("%.1f", t.getRating()) : "";
                setText(t.getName() + yil + tip + puan);
            }
        });

        load("FAVORITE");
        // Çift tıkla detay aç
        listView.setOnMouseClicked(e -> {
            Title sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) openDetails(sel);
        });
    }

    private void load(String type) {
        try {
            List<Title> data = repo.list(type);
            listView.getItems().setAll(data);
        } catch (Exception ex) {
            listView.getItems().setAll(new Title(0, "Liste yüklenemedi", "", "", "", 0, ex.getMessage()));
        }
    }

    private void openDetails(Title t) {
        try {
            FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/com/example/movietracker/ui/view/search/DetailsView.fxml"));
            Parent root = fxml.load();
            DetailsController dc = fxml.getController();
            dc.setData(t);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(t.getName());
            dialog.setScene(new Scene(root));
            dialog.setWidth(650);
            dialog.setHeight(460);
            dialog.show();
        } catch (Exception ex) {
            // sessiz geç
        }
    }

}
