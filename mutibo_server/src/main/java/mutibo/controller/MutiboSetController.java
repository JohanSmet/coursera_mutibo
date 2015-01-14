/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboSet;
import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Redacted
 */

@RestController
public class MutiboSetController
{
	public MutiboSetController()
	{
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.POST, value="/set")
	public MutiboSet addSet(@RequestBody MutiboSet p_set, HttpServletResponse httpResponse) throws IOException
	{
		// all the referenced movies must exist
		for (String f_movie_id : p_set.getGoodMovies())
		{
			if (movieRepository.findOne(f_movie_id) == null) 
			{
				httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Movie not found (" + f_movie_id + ")");
				return null;
			}
		}

		// all the referenced movies must exist
		for (String f_movie_id : p_set.getBadMovies())
		{
			if (movieRepository.findOne(f_movie_id) == null) 
			{
				httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Movie not found (" + f_movie_id + ")");
				return null;
			}
		}

		// the deck must exists 
		if (deckRepository.findOne(p_set.getDeckId()) == null)
		{
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Deck not found (" + p_set.getDeckId().toString() + ")");
			return null;
		}

		return setRepository.save(p_set);
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.DELETE, value="/set/{id}")
	public MutiboSet deleteSet(@PathVariable("id") Long id, HttpServletResponse httpResponse)
	{
		MutiboSet f_set = setRepository.findOne(id);

		if (f_set == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_set;
		}
		
		setRepository.delete(id);

		return f_set;
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.GET, value="/set/{id}")
	public MutiboSet getSet(@PathVariable("id") Long id, HttpServletResponse httpResponse)
	{
		MutiboSet f_set = setRepository.findOne(id);

		if (f_set == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_set;
		}

		return f_set;
	}

	// member variables	
	@Autowired
	private MutiboMovieRepository movieRepository;

	@Autowired
	private MutiboDeckRepository deckRepository;
	
	@Autowired
	private MutiboSetRepository setRepository;
}
