/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboDeck;
import mutibo.repository.MutiboDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping(method=RequestMethod.POST, value="/deck")
	public MutiboDeck addDeck(@RequestBody MutiboDeck p_deck)
	{
		return deckRepository.save(p_deck);
	}

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

	@RequestMapping(method=RequestMethod.GET, value="/deck/list-released")
	public Iterable<MutiboDeck> listReleased()
	{
		return deckRepository.findByReleased(true);
	}

	@RequestMapping(method=RequestMethod.POST, value="/deck/release/{id}")
	public MutiboDeck release(@PathVariable("id") Long id, HttpServletResponse httpResponse)
	{
		MutiboDeck f_deck = deckRepository.findOne(id);

		if (f_deck == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return f_deck;
		}

		f_deck.setReleased(true);
		return deckRepository.save(f_deck);
	}
		
	// member variables	
	@Autowired
	private MutiboDeckRepository deckRepository;
}
