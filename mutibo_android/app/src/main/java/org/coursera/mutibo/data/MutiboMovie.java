package org.coursera.mutibo.data;

public class MutiboMovie
{
    public MutiboMovie()
    {
    }

    public String getImdbId()
    {
        return imdbId;
    }

    public void setImdbId(String imdbId)
    {
        this.imdbId = imdbId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getYearRelease()
    {
        return yearRelease;
    }

    public void setYearRelease(int yearRelease)
    {
        this.yearRelease = yearRelease;
    }

    public String getPlot()
    {
        return plot;
    }

    public void setPlot(String plot)
    {
        this.plot = plot;
    }

    // member variables
    private	String 	imdbId;
    private String 	name;
    private int	 	yearRelease;
    private String	plot;
}
