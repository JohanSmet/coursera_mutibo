/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboDeck;
import mutibo.data.MutiboSync;
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
public class MutiboDeckController
{
	public MutiboDeckController()
	{
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.POST, value="/deck")
	public MutiboDeck addDeck(@RequestBody MutiboDeck p_deck)
	{
		return deckRepository.save(p_deck);
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.GET, value="/deck/{id}")
	public MutiboDeck getDeck(@PathVariable("id") Long id, HttpServletResponse httpResponse)
	{
		MutiboDeck f_deck = deckRepository.findOne(id);

		if (f_deck == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_deck;
		}

		return f_deck;
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.GET, value="/deck")
	public Iterable<MutiboDeck> listAll()
	{
		return deckRepository.findAll();
	}

	@RequestMapping(method=RequestMethod.GET, value="/deck/list-released")
	public Iterable<MutiboDeck> listReleased(HttpServletResponse httpResponse)
	{
		httpResponse.setHeader("Cache-Control", "max-age=315360000");
		return deckRepository.findByReleased(true);
	}

	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.POST, value="/deck/release/{id}")
	public MutiboDeck release(@PathVariable("id") Long id, HttpServletResponse httpResponse)
	{
		MutiboDeck f_deck = deckRepository.findOne(id);

		if (f_deck == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_deck;
		}
		
		// toggle the release flag
		f_deck.setReleased(true);

		// update the content-hash
		MutiboSync mutiboSync = new MutiboSync(f_deck);
		mutiboSync.fill(movieRepository, setRepository);
		f_deck.setContentHash(mutiboSync.computeHash());
		
		// save to the database
		return deckRepository.save(f_deck);
	}
		
	// member variables	
	@Autowired
	private MutiboMovieRepository movieRepository;

	@Autowired
	private MutiboDeckRepository deckRepository;
	
	@Autowired
	private MutiboSetRepository setRepository;
}
