package mutibo.themoviedb;

import java.util.List;

/**
 * @author Redacted
 */
public class TmdbSearchResults
{
	public TmdbSearchResults()
	{
	}

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public int getTotal_pages()
	{
		return total_pages;
	}

	public void setTotal_pages(int total_pages)
	{
		this.total_pages = total_pages;
	}

	public int getTotal_results()
	{
		return total_results;
	}

	public void setTotal_results(int total_results)
	{
		this.total_results = total_results;
	}

	public List<TmdbSearchMovie> getResults()
	{
		return results;
	}

	public void setResults(List<TmdbSearchMovie> results)
	{
		this.results = results;
	}
	
	// member variables
	private int 					page;
	private List<TmdbSearchMovie>	results;
	private int 					total_pages;
	private int 					total_results;
}
