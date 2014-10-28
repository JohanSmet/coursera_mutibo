/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.themoviedb;

import java.util.Date;

/**
 *
 * @author Redacted
 */
public class TmdbMovie
{
	// constructor
	public TmdbMovie()
	{
	}
	
	// getters / setters
	public boolean isAdult()
	{
		return adult;
	}

	public void setAdult(boolean adult)
	{
		this.adult = adult;
	}

	public String getBackdrop_path()
	{
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path)
	{
		this.backdrop_path = backdrop_path;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getImdb_id()
	{
		return imdb_id;
	}

	public void setImdb_id(String imdb_id)
	{
		this.imdb_id = imdb_id;
	}

	public String getOriginal_title()
	{
		return original_title;
	}

	public void setOriginal_title(String original_title)
	{
		this.original_title = original_title;
	}

	public String getOverview()
	{
		return overview;
	}

	public void setOverview(String overview)
	{
		this.overview = overview;
	}

	public String getPoster_path()
	{
		return poster_path;
	}

	public void setPoster_path(String poster_path)
	{
		this.poster_path = poster_path;
	}

	public Date getRelease_date()
	{
		return release_date;
	}

	public void setRelease_date(Date release_date)
	{
		this.release_date = release_date;
	}

	public String getTagline()
	{
		return tagline;
	}

	public void setTagline(String tagline)
	{
		this.tagline = tagline;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	// member variabes
	boolean	adult;
	String	backdrop_path;
	int		id;
	String	imdb_id;
    String	original_title;
	String	overview;
	String 	poster_path;
    Date 	release_date;
    String	tagline;
    String	title;
}
