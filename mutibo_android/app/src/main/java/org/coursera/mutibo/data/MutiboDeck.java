package org.coursera.mutibo.data;

/**
 * Created by johan_000 on 1/11/2014.
 */
public class MutiboDeck
{
    public MutiboDeck()
    {
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
    private Long 	deckId;
    private String 	description;
    private boolean	released;
    private String	contentHash;
}
