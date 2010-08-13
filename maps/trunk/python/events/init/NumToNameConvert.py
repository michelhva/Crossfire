import os,Crossfire,CFDataBase,sys,time

DB=CFDataBase.CFDataBase("PicDB")


faces=open(os.path.join(Crossfire.DataDirectory(),'archetypes'))
facesList=faces.read()
faces.close()
facesList=facesList.split('\n')
ListOfPics=[]
for i in facesList:
	if i.startswith("face "):
		ListOfPics+=[i.split("face ")[1]]

Dict={}
for i in ListOfPics:
	Face=Crossfire.FindFace(i)
	Dict[str(Face)]=i
DB.store("Dict",Dict)

