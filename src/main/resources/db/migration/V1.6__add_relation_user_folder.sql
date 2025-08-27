ALTER TABLE users
    ADD CONSTRAINT fk_file_path
        FOREIGN KEY (file_path) REFERENCES folders (id);
