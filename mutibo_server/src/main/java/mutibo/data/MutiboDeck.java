/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DECK data element
 * @author Redacted
 */

@Document(collection = "decks")
public class MutiboDeck
{
	public MutiboDeck()
	{
	}

	public MutiboDeck(Long deckId, String description, boolean released, String contentHash)
	{
		this.deckId = deckId;
		this.description = description;
		this.released = released;
		this.contentHash = contentHash;
	}

	public Long getDeckId()
	{
		return deckId;
	}

	public void setDeckId(Long deckId)
	{
		this.deckId = deckId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isReleased()
	{
		return released;
	}

	public void setReleased(boolean released)
	{
		this.released = released;
	}

	public String getContentHash()
	{
		return contentHash;
	}

	public void setContentHash(String contentHash)
	{
		this.contentHash = contentHash;
	}
	
	// member variables
	@Id
	private Long 	deckId;
	private String 	description;
	private boolean	released;
	private String	contentHash;
}
