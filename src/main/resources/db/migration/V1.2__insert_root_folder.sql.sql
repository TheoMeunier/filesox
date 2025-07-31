INSERT INTO folders (id, path, parent_id)
VALUES (UNHEX(REPLACE(UUID(), '-', '')), '/', NULL);