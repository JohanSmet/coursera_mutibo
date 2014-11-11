package mutibo;

import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMoviePosterRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSetRepository;
import mutibo.repository.UserRepository;
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
	public MutiboMoviePosterRepository moviePosterRepository() 
	{
		return Mockito.mock(MutiboMoviePosterRepository.class);
	}

	@Bean
	public MutiboDeckRepository deckRepository() 
	{
		return Mockito.mock(MutiboDeckRepository.class);
	}

	@Bean
	public MutiboSetRepository setRepository() 
	{
		return Mockito.mock(MutiboSetRepository.class);
	}

	@Bean
	public UserRepository userRepository() 
	{
		return Mockito.mock(UserRepository.class);
	}

	@Bean
	TmdbApi createTmdbApi()
	{
		return Mockito.mock(TmdbApi.class);
	}
}
