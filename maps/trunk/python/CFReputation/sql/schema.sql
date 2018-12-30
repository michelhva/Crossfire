CREATE TABLE IF NOT EXISTS schema(version INT);

CREATE TABLE regions(
    faction      TEXT,
    region       TEXT,    -- region name (or 'ALL') this faction controls
    influence    NUMERIC,
    CONSTRAINT   influence_range CHECK(influence BETWEEN 0 AND 1)
);

CREATE TABLE relations(
    faction      TEXT,
    race         TEXT,
    attitude     NUMERIC,
    PRIMARY KEY  (faction, race)
);

CREATE TABLE reputations(
    name         TEXT,          -- player name
    faction      TEXT,
    reputation   NUMERIC,
    PRIMARY KEY  (name, faction),
    CONSTRAINT   reputation_range CHECK(reputation BETWEEN -1 AND 1)
);

INSERT INTO schema VALUES(1);
