package mutibo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import mutibo.TestContext;
import mutibo.TestUtil;
import mutibo.WebAppContext;
import mutibo.data.MutiboMovie;
import mutibo.repository.MutiboMovieRepository;
import mutibo.themoviedb.TmdbApi;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Redacted
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
public class MutiboMovieControllerTest
{
	private MockMvc mockMvc;

	@Autowired
	private MutiboMovieRepository movieRepositoryMock;

	@Autowired
	private TmdbApi tmdbApiMock;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() 
	{
		// We have to reset our mock between tests because the mock objects
		// are managed by the Spring container. If we would not reset them,
		// stubbing and verified behavior would "leak" from one test to another.
		Mockito.reset(movieRepositoryMock);
		Mockito.reset(tmdbApiMock);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void getMovieList_test() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");
		MutiboMovie f_movie_02 = new MutiboMovie("tt0371746", "Iron Man", 2008, "Plot Irrelavant");
		
		when(movieRepositoryMock.findAll()).thenReturn(Arrays.asList(f_movie_01, f_movie_02));

		mockMvc.perform(get("/movie"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].imdbId", 		is("tt0848228")))
					.andExpect(jsonPath("$[0].name", 		is("Avengers Assemble")))
					.andExpect(jsonPath("$[0].yearRelease", is(2012)))
					.andExpect(jsonPath("$[0].plot",		is("Plot Irrelavant")))
					.andExpect(jsonPath("$[1].imdbId", 		is("tt0371746")))
					.andExpect(jsonPath("$[1].name", 		is("Iron Man")))
					.andExpect(jsonPath("$[1].yearRelease",	is(2008)))
					.andExpect(jsonPath("$[1].plot",		is("Plot Irrelavant")))
				;
		
		verify(movieRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(movieRepositoryMock);
	}

	@Test
	public void getMovie_found() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");
		
		when(movieRepositoryMock.findOne("tt0848228")).thenReturn(f_movie_01);

		mockMvc.perform(get("/movie/{id}", "tt0848228"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.imdbId", 		is("tt0848228")))
					.andExpect(jsonPath("$.name", 			is("Avengers Assemble")))
					.andExpect(jsonPath("$.yearRelease", 	is(2012)))
					.andExpect(jsonPath("$.plot",			is("Plot Irrelavant")))
				;

		verify(movieRepositoryMock, times(1)).findOne("tt0848228");
        verifyNoMoreInteractions(movieRepositoryMock);
	}

	@Test
	public void getMovie_notfound() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");
		
		when(movieRepositoryMock.findOne("tt0848228")).thenReturn(f_movie_01);
		mockMvc.perform(get("/movie/{id}", "tt0371746"))
					.andExpect(status().isNotFound())
				;

		verify(movieRepositoryMock, times(1)).findOne("tt0371746");
        verifyNoMoreInteractions(movieRepositoryMock);
	}

	@Test
	public void getMovieByName_found() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");
		MutiboMovie f_movie_02 = new MutiboMovie("tt0371746", "Iron Man", 2008, "Plot Irrelavant");

		when(movieRepositoryMock.findByNameLike("%Iron%")).thenReturn(Arrays.asList(f_movie_02));

		mockMvc.perform(get("/movie/find-by-name?pattern={pattern}", "%Iron%"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(1)))
					.andExpect(jsonPath("$[0].imdbId", 		is("tt0371746")))
					.andExpect(jsonPath("$[0].name", 		is("Iron Man")))
					.andExpect(jsonPath("$[0].yearRelease",	is(2008)))
					.andExpect(jsonPath("$[0].plot",		is("Plot Irrelavant")))
				;

		verify(movieRepositoryMock, times(1)).findByNameLike("%Iron%");
        verifyNoMoreInteractions(movieRepositoryMock);
	}

	@Test
	public void getMovieByName_notfound() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");

		when(movieRepositoryMock.findByNameLike("%Iron%")).thenReturn(new ArrayList<MutiboMovie>());

		mockMvc.perform(get("/movie/find-by-name?pattern={pattern}", "%Iron%"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(0)))
				;

		verify(movieRepositoryMock, times(1)).findByNameLike("%Iron%");
        verifyNoMoreInteractions(movieRepositoryMock);
	}

	@Test
	public void addMovie_test() throws Exception
	{
		// setup the tmdb-mock
		MutiboMovie f_movie_01 = new MutiboMovie("tt0848228", "Avengers Assemble", 2012, "Plot Irrelavant");
		when(tmdbApiMock.findByImdbId("tt0848228")).thenReturn(f_movie_01);

		mockMvc.perform(post("/movie/{id}", "tt0848228"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.imdbId", 		is("tt0848228")))
					.andExpect(jsonPath("$.name", 			is("Avengers Assemble")))
					.andExpect(jsonPath("$.yearRelease", 	is(2012)))
					.andExpect(jsonPath("$.plot",			is("Plot Irrelavant")))
				;

		verify(movieRepositoryMock, times(1)).save(f_movie_01);
		verify(tmdbApiMock, times(1)).findByImdbId("tt0848228");
        verifyNoMoreInteractions(movieRepositoryMock);
	}
}
