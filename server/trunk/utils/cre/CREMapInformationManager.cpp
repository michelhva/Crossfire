#include "CREMapInformationManager.h"
#include "CRESettings.h"

extern "C" {
#include "global.h"
}

CREMapInformationManager::CREMapInformationManager(QObject* parent) : QObject(parent)
{
}

bool CREMapInformationManager::browseFinished() const
{
    return myWorker.isFinished();
}

void CREMapInformationManager::start()
{
    if (myWorker.isRunning())
        return;

    /** @todo clear memory */
    myInformation.clear();
    myArchetypeUse.clear();

    loadCache();

    myCancelled = false;
    myCurrentMap = 0;
    myToProcess.clear();
    myToProcess.append(QString(first_map_path));

    myWorker = QtConcurrent::run(this, &CREMapInformationManager::browseMaps);
}

void CREMapInformationManager::process(const QString& path2)
{
    /*
     don't ask why, but the variable gets apparently destroyed on the myToProcess.append() when it reallocated values...
    so keep a copy to avoid messes
     */
    QString path(path2);

    if (myCancelled)
        return;

    emit browsingMap(path);
//    qDebug() << "processing" << path;
    CREMapInformation* information = getOrCreateMapInformation(path);

    char tmppath[MAX_BUF];
    create_pathname(path.toAscii(), tmppath, MAX_BUF);
    QFileInfo info(tmppath);

    if (!info.exists())
    {
//        qDebug() << "non existant map" << tmppath;
        return;
    }

    if (!information->mapTime().isNull() && information->mapTime() >= info.lastModified())
    {
        foreach(QString exit, information->exitsTo())
        {
            if (!myToProcess.contains(exit))
                myToProcess.append(exit);
        }
//        qDebug() << "skipping " << tmppath;
        return;
    }

    mapstruct *m = ready_map_name(path.toAscii(), 0);
//    qDebug() << "processing" << path << information->mapTime() << info.lastModified();
    information->setName(m->name);
    information->setMapTime(info.lastModified());

    char exit_path[500];

    for (int x = MAP_WIDTH(m)-1; x >= 0; x--)
    {
        for (int y = MAP_HEIGHT(m)-1; y >= 0; y--)
        {
            FOR_MAP_PREPARE(m, x, y, item)
            {
                {
                    archetype *arch = find_archetype(item->arch->name);
                    addArchetypeUse(arch->name, information);
                    information->addArchetype(arch->name);
                }

                FOR_INV_PREPARE(item, inv)
                {
                    archetype *arch = find_archetype(inv->arch->name);
                    addArchetypeUse(arch->name, information);
                    information->addArchetype(arch->name);
                } FOR_INV_FINISH();

                if (item->type == EXIT || item->type == TELEPORTER || item->type == PLAYER_CHANGER) {
                    char ep[500];
                    const char *start;

                    if (!item->slaying) {
                        ep[0] = '\0';
                        /*if (warn_no_path)
                            printf(" exit without any path at %d, %d on %s\n", item->x, item->y, info->path);*/
                    } else {
                        memset(ep, 0, 500);
                        if (strcmp(item->slaying, "/!"))
                            strcpy(ep, EXIT_PATH(item));
                        else {
                            if (!item->msg) {
                                //printf("  random map without message in %s at %d, %d\n", info->path, item->x, item->y);
                            } else {
                                /* Some maps have a 'exit_on_final_map' flag, ignore it. */
                                start = strstr(item->msg, "\nfinal_map ");
                                if (!start && strncmp(item->msg, "final_map", strlen("final_map")) == 0)
                                    /* Message start is final_map, nice */
                                    start = item->msg;
                                if (start) {
                                    const char *end = strchr(start+1, '\n');

                                    start += strlen("final_map")+2;
                                    strncpy(ep, start, end-start);
                                }
                            }
                        }

                        if (strlen(ep)) {
                            path_combine_and_normalize(m->path, ep, exit_path, 500);
                            create_pathname(exit_path, tmppath, MAX_BUF);
                            struct stat stats;
                            if (stat(tmppath, &stats)) {
                                //printf("  map %s doesn't exist in map %s, at %d, %d.\n", ep, info->path, item->x, item->y);
                            } else {
                                QString exit = exit_path;
                                if (!myToProcess.contains(exit))
                                    myToProcess.append(exit);

                                CREMapInformation* other = getOrCreateMapInformation(path);
                                Q_ASSERT(other);
                                other->addAccessedFrom(path);
                                information->addExitTo(exit_path);

#if 0
                                link = get_map_info(exit_path);
                                add_map(link, &info->exits_from);
                                add_map(info, &link->exits_to);

                                if (do_regions_link) {
                                    mapstruct *link = ready_map_name(exit_path, 0);

                                    if (link && link != m) {
                                        /* no need to link a map with itself. Also, if the exit points to the same map, we don't
                                        * want to reset it. */
                                        add_region_link(m, link, item->arch->clone.name);
                                        link->reset_time = 1;
                                        link->in_memory = MAP_IN_MEMORY;
                                        delete_map(link);
                                    }
                                }
#endif
                            }
                        }
                    }
                }
            } FOR_MAP_FINISH();
        }
    }

    m->reset_time = 1;
    m->in_memory = MAP_IN_MEMORY;
    delete_map(m);
}

void CREMapInformationManager::browseMaps()
{
    while (myCurrentMap < myToProcess.size())
    {
        process(myToProcess[myCurrentMap]);
        myCurrentMap++;
        if (myCancelled)
            break;
    }

    storeCache();

    emit finished();
}

void CREMapInformationManager::cancel()
{
    myCancelled = true;
    myWorker.waitForFinished();
}

QList<CREMapInformation*> CREMapInformationManager::getArchetypeUse(const archetype* arch)
{
    QMutexLocker lock(&myLock);
    return myArchetypeUse.values(arch->name);
}

void CREMapInformationManager::loadCache()
{
    Q_ASSERT(myInformation.isEmpty());

    CRESettings settings;
    QFile file(settings.mapCacheDirectory() + QDir::separator() + "maps_cache.xml");
    file.open(QFile::ReadOnly);

    QXmlStreamReader reader(&file);
    bool hasMaps = false;
    CREMapInformation* map = NULL;

    while (!reader.atEnd())
    {
        reader.readNext();

        if (reader.isStartElement() && reader.name() == "maps")
        {
            hasMaps = true;
            continue;
        }

        if (!hasMaps)
            continue;

        if (reader.isStartElement() && reader.name() == "map")
        {
            map = new CREMapInformation();
            continue;
        }
        if (reader.isStartElement() && reader.name() == "path")
        {
            QString path = reader.readElementText();
            map->setPath(path);
            Q_ASSERT(!myInformation.contains(path));
            myInformation[path] = map;
            continue;
        }
        if (reader.isStartElement() && reader.name() == "name")
        {
            map->setName(reader.readElementText());
            continue;
        }
        if (reader.isStartElement() && reader.name() == "lastModified")
        {
            QString date = reader.readElementText();
            map->setMapTime(QDateTime::fromString(date, Qt::ISODate));
            continue;
        }
        if (reader.isStartElement() && reader.name() == "arch")
        {
            QString arch = reader.readElementText();
            map->addArchetype(arch);
            addArchetypeUse(arch, map);
            continue;
        }
        if (reader.isStartElement() && reader.name() == "exitTo")
        {
            QString path = reader.readElementText();
            map->addExitTo(path);
            continue;
        }
        if (reader.isStartElement() && reader.name() == "accessedFrom")
        {
            QString path = reader.readElementText();
            map->addAccessedFrom(path);
            continue;
        }
        if (reader.isEndElement() && reader.name() == "map")
        {
            map = NULL;
            continue;
        }
    }

//    qDebug() << "loaded maps from cache:" << myInformation.size();
}

void CREMapInformationManager::storeCache()
{
    CRESettings settings;
    QFile file(settings.mapCacheDirectory() + QDir::separator() + "maps_cache.xml");
    file.open(QFile::WriteOnly | QFile::Truncate);

    QXmlStreamWriter writer(&file);

    writer.setAutoFormatting(true);
    writer.writeStartDocument();

    writer.writeStartElement("maps");

    QList<CREMapInformation*> maps = myInformation.values();
    foreach(CREMapInformation* map, maps)
    {
        writer.writeStartElement("map");
        writer.writeTextElement("path", map->path());
        writer.writeTextElement("name", map->name());
        writer.writeTextElement("lastModified", map->mapTime().toString(Qt::ISODate));
        foreach(QString arch, map->archetypes())
        {
            writer.writeTextElement("arch", arch);
        }
        foreach(QString path, map->exitsTo())
        {
            writer.writeTextElement("exitTo", path);
        }
        foreach(QString path, map->accessedFrom())
        {
            writer.writeTextElement("accessedFrom", path);
        }
        writer.writeEndElement();
    }

    writer.writeEndElement();

    writer.writeEndDocument();
}

CREMapInformation* CREMapInformationManager::getOrCreateMapInformation(const QString& path)
{
    if (!myInformation.contains(path))
    {
        CREMapInformation* information = new CREMapInformation(path);
        myInformation[path] = information;
    }
    return myInformation[path];
}

void CREMapInformationManager::addArchetypeUse(const QString& name, CREMapInformation* map)
{
    QMutexLocker lock(&myLock);
    if (!myArchetypeUse.values(name).contains(map))
        myArchetypeUse.insert(name, map);
}
