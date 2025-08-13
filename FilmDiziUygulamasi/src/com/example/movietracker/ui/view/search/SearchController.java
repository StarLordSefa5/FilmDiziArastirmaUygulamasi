package com.example.movietracker.ui.view.search;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import com.example.movietracker.api.TmdbClient;
import com.example.movietracker.model.Title;
import javafx.util.Callback;

public class SearchController {

	 @FXML private TextField queryField;
	    @FXML private Button searchButton;
	    @FXML private Button listsBtn;           // NEW: "Listelerim" butonu
	    @FXML private ListView<Title> resultsList;

	    @FXML
	    private void initialize() {
	        // ListView için custom cell (Başlık + (Yıl) + Tip + ★Puan)
	        resultsList.setCellFactory(new Callback<ListView<Title>, ListCell<Title>>() {
	            @Override
	            public ListCell<Title> call(ListView<Title> lv) {
	                return new ListCell<Title>() {
	                    @Override
	                    protected void updateItem(Title t, boolean empty) {
	                        super.updateItem(t, empty);
	                        if (empty || t == null) {
	                            setText(null);
	                        } else {
	                            String yil = (t.getYear() == null || t.getYear().isBlank()) ? "" : " (" + t.getYear() + ")";
	                            String tip = (t.getMediaType() == null) ? "" :
	                                    ("movie".equals(t.getMediaType()) ? " • Film"
	                                                                       : "tv".equals(t.getMediaType()) ? " • Dizi"
	                                                                                                       : " • " + t.getMediaType());
	                            String puan = (t.getRating() > 0) ? " • ★ " + String.format("%.1f", t.getRating()) : "";
	                            setText(t.getName() + yil + tip + puan);
	                        }
	                    }
	                };
	            }
	        });

	        // Örnek veriler (isteğe bağlı)
	        resultsList.getItems().setAll(
	            new Title(1, "The Matrix", "1999", "movie", "", 8.7, "Bilim kurgu klasiği."),
	            new Title(2, "Inception", "2010", "movie", "", 8.8, "Rüya içinde rüya hikayesi."),
	            new Title(3, "Interstellar", "2014", "movie", "", 8.6, "Uzay yolculuğu ve zaman.")
	        );

	        // Eventler
	        searchButton.setOnAction(e -> onSearch());
	        queryField.setOnAction(e -> onSearch());
	        resultsList.setOnMouseClicked(e -> {
	            Title selected = resultsList.getSelectionModel().getSelectedItem();
	            if (selected != null) openDetails(selected);
	        });

	        // NEW: "Listelerim" butonu penceresi
	        if (listsBtn != null) {
	            listsBtn.setOnAction(e -> openLists());
	        }
	    }

	    private Title msg(String text) {
	        return new Title(0, text, "", "", "", 0, "");
	    }

	    private void onSearch() {
	        final String q = (queryField.getText() == null) ? "" : queryField.getText().trim();

	        resultsList.getItems().clear();
	        if (q.isEmpty()) {
	            resultsList.getItems().add(msg("Aramak için bir şey yazın…"));
	            return;
	        }

	        searchButton.setDisable(true);
	        resultsList.getItems().add(msg("Aranıyor… lütfen bekleyin"));

	        Task<List<Title>> task = new Task<List<Title>>() {
	            @Override
	            protected List<Title> call() {
	                return TmdbClient.searchTitles(q);
	            }
	        };

	        task.setOnSucceeded(ev -> {
	            List<Title> apiResults = task.getValue();
	            resultsList.getItems().clear();
	            if (apiResults == null || apiResults.isEmpty()) {
	                resultsList.getItems().add(msg("Sonuç bulunamadı."));
	            } else {
	                resultsList.getItems().addAll(apiResults);
	            }
	            searchButton.setDisable(false);
	        });

	        task.setOnFailed(ev -> {
	            Throwable ex = task.getException();
	            resultsList.getItems().setAll(
	                msg("Hata: " + (ex == null ? "Bilinmiyor" : ex.getMessage()))
	            );
	            searchButton.setDisable(false);
	        });

	        new Thread(task, "tmdb-search").start();
	    }

	    private void openDetails(Title t) {
	        try {
	            FXMLLoader fxml = new FXMLLoader(getClass().getResource(
	                    "/com/example/movietracker/ui/view/search/DetailsView.fxml"));
	            Parent root = fxml.load();
	            DetailsController controller = fxml.getController();
	            controller.setData(t);

	            Stage dialog = new Stage();
	            dialog.initModality(Modality.APPLICATION_MODAL);
	            dialog.setTitle(t.getName());
	            dialog.setScene(new Scene(root));
	            dialog.setWidth(650);
	            dialog.setHeight(460);
	            dialog.show();
	        } catch (Exception ex) {
	            resultsList.getItems().add(new Title(0, "Detay açılırken hata", "", "", "", 0, ex.getMessage()));
	        }
	    }

	    // NEW: Listeler penceresini aç
	    private void openLists() {
	        try {
	            java.net.URL loc = SearchController.class.getResource(
	                "/com/example/movietracker/ui/view/search/ListsView.fxml"
	            );
	            System.out.println("ListsView FXML: " + loc); // teşhis

	            javafx.fxml.FXMLLoader fxml = new javafx.fxml.FXMLLoader(loc);
	            javafx.scene.Parent root = fxml.load();

	            javafx.stage.Stage dialog = new javafx.stage.Stage();
	            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
	            dialog.setTitle("Listelerim");
	            dialog.setScene(new javafx.scene.Scene(root));
	            dialog.setWidth(720);
	            dialog.setHeight(520);
	            dialog.show();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	       