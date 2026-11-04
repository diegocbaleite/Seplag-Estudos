INSERT INTO artista (id, nome, tipo) VALUES
 (1, 'Serj Tankian', 'CANTOR'),
 (2, 'Mike Shinoda', 'CANTOR'),
 (3, 'Michel Teló', 'CANTOR'),
 (4, 'Guns N'' Roses', 'BANDA');

INSERT INTO album (id, titulo) VALUES
 (1, 'Harakiri'), (2, 'Black Blooms'), (3, 'The Rough Dog'),
 (4, 'The Rising Tied'), (5, 'Post Traumatic'), (6, 'Post Traumatic EP'), (7, 'Where''d You Go'),
 (8, 'Bem Sertanejo'), (9, 'Bem Sertanejo - O Show (Ao Vivo)'), (10, 'Bem Sertanejo - (1ª Temporada) - EP'),
 (11, 'Use Your Illusion I'), (12, 'Use Your Illusion II'), (13, 'Greatest Hits');
INSERT INTO artista_album (album_id, artista_id) VALUES
 (1,1),(2,1),(3,1),(4,2),(5,2),(6,2),(7,2),(8,3),(9,3),(10,3),(11,4),(12,4),(13,4);
SELECT setval(pg_get_serial_sequence('artista','id'), (SELECT max(id) FROM artista));
SELECT setval(pg_get_serial_sequence('album','id'), (SELECT max(id) FROM album));

