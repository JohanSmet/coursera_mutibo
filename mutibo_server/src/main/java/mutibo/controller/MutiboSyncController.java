package mutibo.controller;

import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboDeck;
import mutibo.data.MutiboSync;
import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Redacted
 */

@RestController
public class MutiboSyncController
{
	@RequestMapping(method=RequestMethod.GET, value="/sync")
	public MutiboSync sync(@RequestParam("id") Long id, @RequestParam("hash") String hash, HttpServletResponse httpResponse)
	{
		// fetch the requested deck
		MutiboDeck mutiboDeck = deckRepository.findOne(id);

		if (mutiboDeck == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		// create the syncer deck
		MutiboSync mutiboSync = new MutiboSync(mutiboDeck);
		mutiboSync.fill(movieRepository, setRepository);
		return mutiboSync;
	}

	// member variables	
	@Autowired
	private MutiboMovieRepository movieRepository;

	@Autowired
	private MutiboDeckRepository deckRepository;
	
	@Autowired
	private MutiboSetRepository setRepository;
}
