package com.example.movietracker.model;

public class Title {

	
	    private final int id;
	    private final String name;
	    private final String year;       // "1999" gibi 4 haneli
	    private final String mediaType;  // "movie" / "tv"
	    private final String posterPath; // "/abc123.jpg"
	    private final double rating;     // 0.0 - 10.0
	    private final String overview;

	    // >>> İSTENEN CONSTRUCTOR (tam senin çağırdığın sırayla) <<<
	    public Title(int id, String name, String year, String mediaType,
	                 String posterPath, double rating, String overview) {
	        this.id = id;
	        this.name = name;
	        this.year = year;
	        this.mediaType = mediaType;
	        this.posterPath = posterPath;
	        this.rating = rating;
	        this.overview = overview;
	    }

	    // Getter'lar (gerekli olanlar)
	    public int getId() { return id; }
	    public String getName() { return name; }
	    public String getYear() { return year; }
	    public String getMediaType() { return mediaType; }
	    public String getPosterPath() { return posterPath; }
	    public double getRating() { return rating; }
	    public String getOverview() { return overview; }

	    @Override
	    public String toString() {
	        return name + (year == null || year.isBlank() ? "" : " (" + year + ")");
	    }
	}

	
	   
	


