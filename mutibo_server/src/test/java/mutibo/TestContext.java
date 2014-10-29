package mutibo;

import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.themoviedb.TmdbApi;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redacted
 */
@Configuration
public class TestContext
{
	@Bean
	public MutiboMovieRepository movieRepository() 
	{
		return Mockito.mock(MutiboMovieRepository.class);
	}

	@Bean
	public MutiboDeckRepository deckRepository() 
	{
		return Mockito.mock(MutiboDeckRepository.class);
	}

	@Bean
	TmdbApi createTmdbApi()
	{
		return Mockito.mock(TmdbApi.class);
	}
}
