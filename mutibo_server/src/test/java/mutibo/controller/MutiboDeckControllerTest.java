package mutibo.controller;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import mutibo.TestContext;
import mutibo.TestUtil;
import mutibo.WebAppContext;
import mutibo.data.MutiboDeck;
import mutibo.data.MutiboMovie;
import mutibo.repository.MutiboDeckRepository;
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
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author Redacted
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
public class MutiboDeckControllerTest
{
	private MockMvc mockMvc;

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
		Mockito.reset(deckRepositoryMock);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getDeck_found() throws Exception
	{
		// setup the mock database
		MutiboDeck f_deck_01 = new MutiboDeck(1l, "Deck 1", false, "11");
		
		when(deckRepositoryMock.findOne(1l)).thenReturn(f_deck_01);

		mockMvc.perform(get("/deck/{id}", 1L))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.deckId",			is(1)))
					.andExpect(jsonPath("$.description",	is("Deck 1")))
					.andExpect(jsonPath("$.released",		is(false)))
					.andExpect(jsonPath("$.contentHash",	is("11")))
				;

		verify(deckRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(deckRepositoryMock);
	}

	@Test
	public void getDeck_notfound() throws Exception
	{
		mockMvc.perform(get("/deck/{id}", 1L))
					.andExpect(status().isNotFound());

		verify(deckRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(deckRepositoryMock);
	}

	@Test
	public void addMovie_test() throws Exception
	{
		// setup the tmdb-mock
		MutiboDeck f_deck_01 = new MutiboDeck(1l, "Deck 1", false, "11");
		
		when(deckRepositoryMock.save(f_deck_01)).thenReturn(f_deck_01);

		Gson gson = new Gson();

		mockMvc.perform(post("/deck")
							.contentType(MediaType.APPLICATION_JSON)
							.content(gson.toJson(f_deck_01))
					   )
					.andExpect(status().isOk())
/*					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.deckId",			is(1)))
					.andExpect(jsonPath("$.description",	is("Deck 1")))
					.andExpect(jsonPath("$.released",		is(false)))
					.andExpect(jsonPath("$.contentHash",	is("11")))
*/
				;

		// verify(deckRepositoryMock, times(1)).save(f_deck_01);
        //verifyNoMoreInteractions(deckRepositoryMock);
	}

	@Test
	public void listReleased_test() throws Exception
	{
		// setup the tmdb-mock
		MutiboDeck f_deck_01 = new MutiboDeck(1l, "Deck 1", true,  "11");
		MutiboDeck f_deck_02 = new MutiboDeck(2l, "Deck 2", false, "12");
		MutiboDeck f_deck_03 = new MutiboDeck(3l, "Deck 3", true,  "13");

		when(deckRepositoryMock.findOne(1l)).thenReturn(f_deck_01);
		when(deckRepositoryMock.findOne(2l)).thenReturn(f_deck_02);
		when(deckRepositoryMock.findOne(3l)).thenReturn(f_deck_03);
		when(deckRepositoryMock.findByReleased(true)).thenReturn(Arrays.asList(f_deck_01, f_deck_03));
		
		mockMvc.perform(get("/deck/list-released"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].deckId",		is(1)))
					.andExpect(jsonPath("$[0].description",	is("Deck 1")))
					.andExpect(jsonPath("$[0].released",	is(true)))
					.andExpect(jsonPath("$[0].contentHash",	is("11")))
					.andExpect(jsonPath("$[1].deckId",		is(3)))
					.andExpect(jsonPath("$[1].description",	is("Deck 3")))
					.andExpect(jsonPath("$[1].released",	is(true)))
					.andExpect(jsonPath("$[1].contentHash",	is("13")))
				;

		verify(deckRepositoryMock, times(1)).findByReleased(true);
        verifyNoMoreInteractions(deckRepositoryMock);
	}

	@Test
	public void release_test() throws Exception
	{
		// setup the tmdb-mock
		MutiboDeck f_deck_01 = new MutiboDeck(1l, "Deck 1", false,  "11");

		when(deckRepositoryMock.findOne(1l)).thenReturn(f_deck_01);
		when(deckRepositoryMock.save(f_deck_01)).thenReturn(f_deck_01);

		mockMvc.perform(post("/deck/release/{id}", 1L))
					.andExpect(status().isOk())
					.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.deckId",			is(1)))
					.andExpect(jsonPath("$.description",	is("Deck 1")))
					.andExpect(jsonPath("$.released",		is(true)))
					.andExpect(jsonPath("$.contentHash",	is("11")))
				;

		verify(deckRepositoryMock, times(1)).save(f_deck_01);
		verify(deckRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(deckRepositoryMock);

	}
	
}
