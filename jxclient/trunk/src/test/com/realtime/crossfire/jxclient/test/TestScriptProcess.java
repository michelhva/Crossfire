package com.realtime.crossfire.jxclient.test;

import com.realtime.crossfire.jxclient.scripts.ScriptProcess;
import com.realtime.crossfire.jxclient.scripts.ScriptProcessListener;
import junit.framework.Assert;

/**
 * Implements {@link ScriptProcess} for regression tests. All functions do call
 * {@link Assert#fail()}. Sub-classes may override some functions.
 * @author Andreas Kirschbaum
 */
public class TestScriptProcess implements ScriptProcess
{
    /** {@inheritDoc} */
    @Override
    public int getScriptId()
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public String getFilename()
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void commandSent(final String cmd)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addScriptProcessListener(final ScriptProcessListener scriptProcessListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void killScript()
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final ScriptProcess o)
    {
        Assert.fail();
        throw new AssertionError();
    }
}
