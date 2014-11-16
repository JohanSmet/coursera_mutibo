package org.coursera.mutibo.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MutiboGameResult
{
    public MutiboGameResult()
    {
        setResults = new ArrayList<MutiboSetResult>();
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public List<MutiboSetResult> getSetResults()
    {
        return setResults;
    }

    public void addSetResult(MutiboSetResult setResult)
    {
        setResults.add(setResult);
    }

    public void clearSetResults()
    {
        setResults.clear();
    }

    // member variables
    Date                  startTime;
    Date			      endTime;
    List<MutiboSetResult> setResults;
}
