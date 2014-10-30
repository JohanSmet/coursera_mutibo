package mutibo.data;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MOVIE data element
 * @author Redacted
 */
@Document(collection = "movies")
public class MutiboMovie 
{
	// interface functions

	/**
	 * Default constructor
	 */
	public MutiboMovie()
	{
	}

	/**
	 * Constructor that initializes the record to the given values
	 * @param imdbId Id of the movie on IMDB
	 * @param name Official name of the movie
	 * @param year The year the movie was released
	 * @param plot Short plot of the movie
	 */
	public MutiboMovie(String imdbId, String name, int year, String plot)
	{
		this.imdbId			= imdbId;
		this.name			= name;
		this.yearRelease	= year;
		this.plot			= plot;
	}

	/**
	 * Retrieve the IMDB-id of the movie
	 * @return imdbId
	 */
	public String getImdbId()
	{
		return this.imdbId;
	}
	
	/**
	 * Change the IMDB-id of the movie
	 * @param p_imdb_id The new IMDB-id of the movie.
	 */
	public void setImdbId(String p_imdb_id)
	{
		this.imdbId = p_imdb_id;
	}
	
	/**
	 * Retrieve the name of the movie
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}
		
	/**
	 * Change the name of the movie
	 * @param p_name The new name of the movie.
	 */
	public void setName(String p_name)
	{
		this.name = p_name;
	}
	
	/**
	 * Retrieve the year of release of the movie
	 * @return yearRelease
	 */
	public int getYearRelease()
	{
		return this.yearRelease;
	}
	
	/**
	 * Change the year of release of the movie
	 * @param p_year The new year of release to set.
	 */
	public void setYearRelease(int p_year)
	{
		this.yearRelease = p_year;
	}
	
	/**
	 * Retrieve the plot of the movie
	 * @return plot
	 */
	public String getPlot() 
	{
		return plot;
	}
	
	/**
	 * Change the plot of the movie
	 * @param plot The new plot of the movie
	 */
	public void setPlot(String plot)
	{
		this.plot = plot;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MutiboMovie && this.imdbId.equals(((MutiboMovie) obj).imdbId);
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 73 * hash + Objects.hashCode(this.imdbId);
		return hash;
	}

	// member variables
	@Id 
	private	String 	imdbId;
	private String 	name;
	private int	 	yearRelease;
	private String	plot;
}
