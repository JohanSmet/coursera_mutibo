package mutibo.controller;

import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboMovie;
import mutibo.repository.MutiboMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * /movie controller
 * @author Redacted
 */
@RestController
public class MutiboMovieControler 
{
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

	@RequestMapping(method=RequestMethod.GET, value="/movie/find-by-name")
	public Iterable<MutiboMovie> findByName(@RequestParam("pattern") String pattern)
	{
		return movieRepository.findByNameLike(pattern);
	}

	/**
	 * Add or update a movie
	 * @param p_movie
	 * @return MutiboMovie
	 */
	@RequestMapping(method=RequestMethod.POST, value="/movie")
	public MutiboMovie addMovie(@RequestBody MutiboMovie p_movie)
	{	
		movieRepository.save(p_movie);
		return p_movie;
	}

	//
	// member variables
	//

	@Autowired private MutiboMovieRepository	movieRepository;
}
