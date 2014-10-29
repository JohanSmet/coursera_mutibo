package mutibo.themoviedb;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Redacted
 */
public class TmdbSearchMovie
{
	public TmdbSearchMovie()
	{
	}

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

	public String getOriginal_title()
	{
		return original_title;
	}

	public void setOriginal_title(String original_title)
	{
		this.original_title = original_title;
	}

	public Date getRelease_date()
	{
		return release_date;
	}

	public int getRelease_year()
	{
		if (release_date != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(release_date);
			return cal.get(Calendar.YEAR);
		}
		else
		{
			return 0;
		}
	}

	public void setRelease_date(Date release_date)
	{
		this.release_date = release_date;
	}

	public String getPoster_path()
	{
		return poster_path;
	}

	public void setPoster_path(String poster_path)
	{
		this.poster_path = poster_path;
	}

	public float getPopularity()
	{
		return popularity;
	}

	public void setPopularity(float popularity)
	{
		this.popularity = popularity;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public float getVote_average()
	{
		return vote_average;
	}

	public void setVote_average(float vote_average)
	{
		this.vote_average = vote_average;
	}

	public int getVote_count()
	{
		return vote_count;
	}

	public void setVote_count(int vote_count)
	{
		this.vote_count = vote_count;
	}

	public static class Comparators {
		public static Comparator<TmdbSearchMovie> POPULARITY_DESC = new Comparator<TmdbSearchMovie>() {
            @Override
            public int compare(TmdbSearchMovie o1, TmdbSearchMovie o2) {
                return Float.compare(o1.getPopularity(), o2.getPopularity()) * -1;
            }
        };
	}

	// member variables
	private boolean adult;
    private String	backdrop_path;
    private int     id;
    private String	original_title;
    private Date 	release_date;
    private String	poster_path;
    private float 	popularity;
    private String	title;
    private float   vote_average;
    private int 	vote_count;
}
