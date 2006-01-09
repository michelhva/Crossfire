#!/bin/sh
#java -Dsun.java2d.opengl=true -Dsun.java2d.trace=log,timestamp,count,out:java2d.log com.realtime.crossfire.jxclient.jxclient 1024 768 -1 0 > logme.log
java -Xmx192M -cp jxclient.jar:jxcskin_prelude.jar -Dsun.java2d.opengl=true com.realtime.crossfire.jxclient.jxclient


