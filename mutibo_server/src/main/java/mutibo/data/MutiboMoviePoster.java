package mutibo.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * POSTER data element (a movie poster)
 * @author Redacted
 */

@Document(collection = "posters")
public class MutiboMoviePoster
{
	public MutiboMoviePoster()
	{
		this.id 		= "";
		this.imdbId 	= "";
		this.resolution = "";
		this.imageData  = null;
	}

	public String getImdbId()
	{
		return imdbId;
	}

	public void setImdbId(String imdbId)
	{
		this.imdbId = imdbId;
		constructId();
	}

	public String getResolution()
	{
		return resolution;
	}

	public void setResolution(String resolution)
	{
		this.resolution = resolution;
		constructId();
	}

	public byte[] getImageData()
	{
		return imageData;
	}

	public void setImageData(byte[] imageData)
	{
		this.imageData = imageData;
	}

	private void constructId()
	{
		this.id = constructId(this.imdbId, this.resolution);
	}

	public static String constructId(String imdbId, String resolution)
	{
		return imdbId + '_' + resolution;
	}
	
	// member variables
	@Id 
	private String	id;

	private	String 	imdbId;
	private String 	resolution;
	private byte[]	imageData;
}
