package mutibo;

import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMoviePosterRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSessionRepository;
import mutibo.repository.MutiboSetRepository;
import mutibo.repository.MutiboSetResultRepository;
import mutibo.repository.MutiboUserResultRepository;
import mutibo.repository.UserRepository;
import mutibo.security.TokenAuthenticationService;
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

	@Bean MutiboSetResultRepository setResultRepository()
	{
		return Mockito.mock(MutiboSetResultRepository.class);
	}

	@Bean MutiboSessionRepository sessionepository()
	{
		return Mockito.mock(MutiboSessionRepository.class);
	}

	@Bean
	public UserRepository userRepository() 
	{
		return Mockito.mock(UserRepository.class);
	}

	@Bean
	public MutiboUserResultRepository userResultRepository()
	{
		return Mockito.mock(MutiboUserResultRepository.class);
	}

	@Bean
	TmdbApi createTmdbApi()
	{
		return Mockito.mock(TmdbApi.class);
	}

	@Bean
	TokenAuthenticationService tokenAuthenticationService()
	{
		return Mockito.mock(TokenAuthenticationService.class);
	}
}
