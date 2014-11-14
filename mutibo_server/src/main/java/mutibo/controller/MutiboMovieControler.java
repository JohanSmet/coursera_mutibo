package mutibo.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboMovie;
import mutibo.data.MutiboMoviePoster;
import mutibo.repository.MutiboMoviePosterRepository;
import mutibo.repository.MutiboMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import mutibo.themoviedb.TmdbApi;
import org.springframework.http.MediaType;

/**
 * /movie controller
 * @author Redacted
 */
@RestController
public class MutiboMovieControler 
{
	public MutiboMovieControler()
	{
	}
	/**
	 * Retrieve a list of all known movies
	 * @return A list of MutiboMovies
	 */
	@RequestMapping(method=RequestMethod.GET, value="/movie")
	@ResponseBody
	public Iterable<MutiboMovie> getMovieList()
	{
		return movieRepository.findAll();
	}

	/**
	 * Retrieve the information of a single movie
	 * @param p_id
	 * @param httpResponse
	 * @return MutiboMovie
	 */
	@RequestMapping(method=RequestMethod.GET, value="/movie/{id}")
	public MutiboMovie getMovie(@PathVariable("id") String p_id, HttpServletResponse httpResponse)
	{
		MutiboMovie f_movie = movieRepository.findOne(p_id);

		if (f_movie == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_movie;
		}

		return f_movie;
	}

	/**
	 * Return a list of movies who's names match a given pattern
	 * @param pattern
	 * @return 
	 */
	@RequestMapping(method=RequestMethod.GET, value="/movie/find-by-name")
	public Iterable<MutiboMovie> findByName(@RequestParam("pattern") String pattern)
	{
		List<MutiboMovie> f_results = movieRepository.findByNameLike(pattern);

		f_results.addAll(tmdbApi.findByName(pattern));

		return f_results;
	}

	/**
	 * Add or update a movie
	 * @param id
	 * @return MutiboMovie
	 */
	@RequestMapping(method=RequestMethod.POST, value="/movie/{id}")
	public MutiboMovie addMovie(@PathVariable("id") String id)
	{	
		MutiboMovie f_movie = null;

		// add the movie
		if (id.startsWith("tt"))
			f_movie = tmdbApi.findByImdbId(id);
		else if (id.matches("\\d+"))
			f_movie = tmdbApi.findById(Integer.parseInt(id));
		else
			return f_movie;

		movieRepository.save(f_movie);

		// add posters
		moviePosterRepository.save(tmdbApi.retrieveMoviePoster(f_movie.getImdbId(), "low", f_movie.getTmdbPosterPath()));

		return f_movie;
	}

	/**
	 * Retrieve the poster for the specified movie
	 * @param id
	 * @param resolution
	 * @param httpResponse
	 * @return 
	 */

	@RequestMapping(method=RequestMethod.GET, value="/movie/poster", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getPoster(@RequestParam("id") String id, @RequestParam ("resolution") String resolution, HttpServletResponse httpResponse)
	{
		MutiboMoviePoster poster = moviePosterRepository.findOne(MutiboMoviePoster.constructId(id, resolution));
		
		if (poster == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		httpResponse.setHeader("Cache-Control", "max-age=315360000");
		return poster.getImageData();
	}

	//
	// member variables
	//

	@Autowired private MutiboMovieRepository   		movieRepository;
	@Autowired private MutiboMoviePosterRepository	moviePosterRepository;
	@Autowired private TmdbApi						tmdbApi;
}
