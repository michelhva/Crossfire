package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;

public class Spell
{
    private BufferedImage myspellpic;
    private String myspellname;
    private String myid;

    public Spell(String filename, String spellname, String id) throws IOException
    {
        myspellpic   = javax.imageio.ImageIO.read(new File(filename));
        myspellname = spellname;
        myid = id;
    }
    public String getName()
    {
        return myspellname;
    }
    public String getInternalName()
    {
        return myid;
    }
    public BufferedImage getPicture()
    {
        return myspellpic;
    }
}
