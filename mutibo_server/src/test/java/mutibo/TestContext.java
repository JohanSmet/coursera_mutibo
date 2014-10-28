package mutibo;

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
	TmdbApi createTmdbApi()
	{
		return Mockito.mock(TmdbApi.class);
	}
}
