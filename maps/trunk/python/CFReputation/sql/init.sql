INSERT OR REPLACE INTO regions VALUES
('Dragons', 'ALL', 0.4),
('Scorn', 'scorn', 0.5),
('Scorn', 'scornarena', 0.5),
('Scorn', 'scorncounty', 0.5),
('Scorn', 'scornoldcity', 0.5);

INSERT OR REPLACE INTO relations VALUES
('Dragons', 'dragon', 1),
('Dragons', 'faerie', -1),
('Dragons', 'human', -1),
('Scorn', 'demon', -1),
('Scorn', 'dragon', -1),
('Scorn', 'giant', -1),
('Scorn', 'goblin', -1),
('Scorn', 'human', 1),
('Scorn', 'reptile', -1),
('Scorn', 'troll', -1),
('Scorn', 'undead', -1),
('Scorn', 'unnatural', -1);
