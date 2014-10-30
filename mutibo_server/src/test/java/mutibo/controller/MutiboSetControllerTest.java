package mutibo.controller;

import com.google.gson.Gson;
import mutibo.TestContext;
import mutibo.TestUtil;
import mutibo.WebAppContext;
import mutibo.data.MutiboDeck;
import mutibo.data.MutiboMovie;
import mutibo.data.MutiboSet;
import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSetRepository;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Redacted
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
public class MutiboSetControllerTest
{
	private MockMvc mockMvc;

	@Autowired
	private MutiboSetRepository setRepositoryMock;

	@Autowired
	private MutiboMovieRepository movieRepositoryMock;

	@Autowired
	private MutiboDeckRepository deckRepositoryMock;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() 
	{
		// We have to reset our mock between tests because the mock objects
		// are managed by the Spring container. If we would not reset them,
		// stubbing and verified behavior would "leak" from one test to another.
		Mockito.reset(setRepositoryMock);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void getSet_found() throws Exception
	{
		// setup the mock database
		MutiboSet f_set_01 = new MutiboSet(	1L, 1L, 
											new String[] {"tt0371746", "tt0800369", "tt2015381" }, new String[] {"tt0770828"},
											"Man of Steel is not part of the Marvel franchise.",
											5, 1, 9, 2);
	
		when(setRepositoryMock.findOne(1l)).thenReturn(f_set_01);

		mockMvc.perform(get("/set/{id}", 1L))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.setId",			is(1)))
					.andExpect(jsonPath("$.deckId",			is(1)))
					.andExpect(jsonPath("$.goodMovies",		hasSize(3)))
					.andExpect(jsonPath("$.goodMovies[0]",	is("tt0371746")))
					.andExpect(jsonPath("$.goodMovies[1]",	is("tt0800369")))
					.andExpect(jsonPath("$.goodMovies[2]",	is("tt2015381")))
					.andExpect(jsonPath("$.badMovies",		hasSize(1)))
					.andExpect(jsonPath("$.badMovies[0]",	is("tt0770828")))
					.andExpect(jsonPath("$.reason",			is("Man of Steel is not part of the Marvel franchise.")))
					.andExpect(jsonPath("$.points",			is(5)))
					.andExpect(jsonPath("$.difficulty",		is(1)))
					.andExpect(jsonPath("$.ratingTotal",	is(9)))
					.andExpect(jsonPath("$.ratingCount",	is(2)))
				;

		verify(setRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(setRepositoryMock);
	}

	@Test
	public void getSet_notfound() throws Exception
	{
		mockMvc.perform(get("/set/{id}", 1L))
					.andExpect(status().isNotFound());

		verify(setRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(setRepositoryMock);
	}

	class IsSetOne extends ArgumentMatcher<MutiboSet> 
	{
		@Override
		public boolean matches(Object set) 
		{
			return ((MutiboSet) set).getSetId() == 1L;
		}
	}

	@Test
	public void addSet() throws Exception
	{
		// setup the mock database
		MutiboMovie f_movie_01 = new MutiboMovie("tt0371746", "Iron Man", 2008, "Plot Irrelavant");
		MutiboMovie f_movie_02 = new MutiboMovie("tt0800369", "Thor", 	  2011, "Plot Irrelavant");
		MutiboMovie f_movie_03 = new MutiboMovie("tt2015381", "Gardians of the Galaxy", 2014, "Plot Irrelavant");
		MutiboMovie f_movie_04 = new MutiboMovie("tt0770828", "Man of Steel", 2013, "Plot Irrelavant");

		MutiboDeck  f_deck_01  = new MutiboDeck(1l, "Deck 1", false, "11");
		
		MutiboSet f_set_01 = new MutiboSet(	1L, f_deck_01.getDeckId(),
											new String[] {f_movie_01.getImdbId(), f_movie_02.getImdbId(), f_movie_03.getImdbId() }, 
											new String[] {f_movie_04.getImdbId()},
											"Man of Steel is not part of the Marvel franchise.",
											5, 1, 9, 2);

		when(setRepositoryMock.save(argThat(new IsSetOne()))).thenReturn(f_set_01);
		when(movieRepositoryMock.findOne(f_movie_01.getImdbId())).thenReturn(f_movie_01);
		when(movieRepositoryMock.findOne(f_movie_02.getImdbId())).thenReturn(f_movie_02);
		when(movieRepositoryMock.findOne(f_movie_03.getImdbId())).thenReturn(f_movie_03);
		when(movieRepositoryMock.findOne(f_movie_04.getImdbId())).thenReturn(f_movie_04);
		when(deckRepositoryMock.findOne(f_deck_01.getDeckId())).thenReturn(f_deck_01);

		Gson gson = new Gson();

		mockMvc.perform(post("/set")
							.contentType(MediaType.APPLICATION_JSON)
							.content(gson.toJson(f_set_01))
					   )
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.setId",			is(1)))
					.andExpect(jsonPath("$.deckId",			is(1)))
					.andExpect(jsonPath("$.goodMovies",		hasSize(3)))
					.andExpect(jsonPath("$.goodMovies[0]",	is("tt0371746")))
					.andExpect(jsonPath("$.goodMovies[1]",	is("tt0800369")))
					.andExpect(jsonPath("$.goodMovies[2]",	is("tt2015381")))
					.andExpect(jsonPath("$.badMovies",		hasSize(1)))
					.andExpect(jsonPath("$.badMovies[0]",	is("tt0770828")))
					.andExpect(jsonPath("$.reason",			is("Man of Steel is not part of the Marvel franchise.")))
					.andExpect(jsonPath("$.points",			is(5)))
					.andExpect(jsonPath("$.difficulty",		is(1)))
					.andExpect(jsonPath("$.ratingTotal",	is(9)))
					.andExpect(jsonPath("$.ratingCount",	is(2)))
				;

		verify(setRepositoryMock, times(1)).save(argThat(new IsSetOne()));
        verifyNoMoreInteractions(setRepositoryMock);
	}
}
