#! /usr/bin/python
import os, sys
from filelist import filelist
ToGuild=sys.argv[1]
ToRegion=sys.argv[2]


Ctl=0
if len(sys.argv)>=7:
        ToFolder=sys.argv[3]
        ExitPath=sys.argv[4]
        ExitX,ExitY=sys.argv[5],sys.argv[6]
        Ctl=1
        if sys.argv[7]=="same":
                sys.argv[7]=sys.argv[4]
        StorageExit=sys.argv[7]
        StorageX,StorageY=sys.argv[8:]
os.system('mkdir '+ToGuild)
for i in filelist:
        fromfile=open(i, 'r')
        filecontents=fromfile.read()
        fromfile.close()
        filecontents=filecontents.replace('GUILD_TEMPLATE', ToGuild)
        if Ctl==1:
                filecontents=filecontents.replace("region Template","region "+ToRegion).replace("TemplateExit", ExitPath).replace("TemplateHP", ExitX).replace("TemplateSP", ExitY).replace("Exit+1X", StorageX).replace("ExitY",StorageY).replace("ExitX",StorageX).replace("ExitPath",StorageExit)
        tofile=open('./'+ToGuild+'/'+i, 'w')
        tofile.write(filecontents)
        tofile.close()
        if Ctl==1:
                secondtofile=open('../../'+ToFolder+"/"+i,'w')
                secondtofile.write(filecontents)
                secondtofile.close()