package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class MapSquare
{
    private int x=0, y=0;
    private MapSquare[] myhead=new MapSquare[ServerConnection.NUM_LAYERS];
    private Face[] myface=new Face[ServerConnection.NUM_LAYERS];
    private boolean isdirty = false;
    private int mydarkness = 0;

    public MapSquare(int nx, int ny)
    {
        x = nx;
        y = ny;
        for (int i=0; i<ServerConnection.NUM_LAYERS; i++)
        {
            myface[i] = null;
            myhead[i] = this;
        }
    }
    public int getXPos()
    {
        return x;
    }
    public int getYPos()
    {
        return y;
    }
    public void setPos(int nx, int ny)
    {
        x = nx;
        y = ny;
    }
    public Face getFace(int layer)
    {
        return myface[layer];
    }
    public void setFace(Face f, int layer)
    {
        MapSquare[][] map = com.realtime.crossfire.jxclient.Map.getMap();

        int sw, sh, psx, psy;

        if (myface[layer]!=null)
        {
            sw = myface[layer].getPicture().getWidth() / ServerConnection.SQUARE_SIZE;
            sh = myface[layer].getPicture().getHeight() / ServerConnection.SQUARE_SIZE;

            psx = x - (sw-1);
            psy = y - (sh-1);

            if (psx < 0) psx = 0;
            if (psy < 0) psy = 0;

            for (int ly=psy; ly<=y;ly++)
            {
                for (int lx=psx; lx<=x; lx++)
                {
                    map[lx][ly].setFaceHead(null, layer, map[lx][ly]);
                }
            }
        }
        myface[layer] = f;
        if (myface[layer]!=null)
        {
            sw = myface[layer].getPicture().getWidth() / ServerConnection.SQUARE_SIZE;
            sh = myface[layer].getPicture().getHeight() / ServerConnection.SQUARE_SIZE;

            psx = x - (sw-1);
            psy = y - (sh-1);

            if (psx < 0) psx = 0;
            if (psy < 0) psy = 0;

            for (int ly=psy; ly<=y;ly++)
            {
                for (int lx=psx; lx<=x; lx++)
                {
                    map[lx][ly].setFaceHead(myface[layer], layer, this);
                }
            }
        }
    }
    protected void setFaceHead(Face f, int layer, MapSquare m)
    {
        //System.out.println("- setFaceHead: ("+x+";"+y+";"+layer+"):"+m);
        myface[layer] = f;
        setHead(m, layer);
        dirty();
    }
    public void dirty()
    {
        isdirty = true;
        for(int layer = 0; layer < ServerConnection.NUM_LAYERS; layer++)
        {
            if ((myhead[layer] == this)&&(myface[layer]!=null))
            {
                MapSquare[][] map = com.realtime.crossfire.jxclient.Map.getMap();
                int sw = myface[layer].getPicture().getWidth() / ServerConnection.SQUARE_SIZE;
                int sh = myface[layer].getPicture().getHeight() / ServerConnection.SQUARE_SIZE;

                int psx = x - (sw-1);
                int psy = y - (sh-1);

                if (psx < 0) psx = 0;
                if (psy < 0) psy = 0;

                for (int ly=psy; ly<=y;ly++)
                {
                    for (int lx=psx; lx<=x; lx++)
                    {
                        if (map[lx][ly].isDirty()==false)
                            map[lx][ly].dirty();
                    }
                }
            }
            else if ((myhead[layer] != null)&&(myhead[layer].isDirty()==false))
            {
                myhead[layer].dirty();
            }
        }

    }
    public boolean isDirty()
    {
        return isdirty;
    }
    public void clean()
    {
        isdirty = false;
    }
    public MapSquare getHead(int layer)
    {
        return myhead[layer];
    }
    public void setHead(MapSquare m, int layer)
    {
        myhead[layer] = m;
    }
    public void clear()
    {
        for (int i=0; i<ServerConnection.NUM_LAYERS; i++)
        {
            setFace(null, i);
        }
    }
    public void setDarkness(int d)
    {
        mydarkness = d;
    }
    public int getDarkness()
    {
        return mydarkness;
    }
    public String toString()
    {
        String str = new String("("+x+";"+y+"):");
        for (int i=0; i<ServerConnection.NUM_LAYERS; i++)
        {
            if (myface[i] != null)
                str += myface[i].getName()+", ";
        }
        return str;
    }
    public void copy(MapSquare square)
    {
        System.out.println(this+"->"+square);
        for (int i=0; i<ServerConnection.NUM_LAYERS; i++)
        {
            if (getHead(i)==this)
            {
                System.out.println(" -> setFace("+i+")");
                square.setFace(getFace(i), i);
            }
        }
    }
}