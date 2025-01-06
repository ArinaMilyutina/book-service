DO
$$
BEGIN
        IF
NOT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname = 'book_service_schema') THEN
CREATE SCHEMA book_service_schema;
END IF;
END
$$;

DO
$$
BEGIN
        IF
NOT EXISTS(SELECT 1
                      FROM information_schema.tables
                      WHERE table_schema = 'book_service_schema'
                        AND table_name = 'flyway_book_service_history') THEN
CREATE TABLE book_service_schema.flyway_book_service_history
(
    installed_rank INT           NOT NULL PRIMARY KEY,
    version        VARCHAR(50),
    description    VARCHAR(200)  NOT NULL,
    type           VARCHAR(20)   NOT NULL,
    script         VARCHAR(1000) NOT NULL,
    checksum       INT,
    installed_by   VARCHAR(100)  NOT NULL,
    installed_on   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INT           NOT NULL,
    success        BOOLEAN       NOT NULL
);
END IF;
END
$$;

CREATE TABLE book_service_schema.books
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    ISBN        VARCHAR(255) NOT NULL,
    admin_id    BIGINT,
    UNIQUE (ISBN)
);

CREATE TABLE book_service_schema.book_genres
(
    book_id BIGINT REFERENCES book_service_schema.books (id) ON DELETE CASCADE,
    genre   VARCHAR(255) NOT NULL,
    PRIMARY KEY (book_id, genre)
);
CREATE TABLE book_service_schema.book_status
(
    book_id BIGINT      NOT NULL,
    status  VARCHAR(50) NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book_service_schema.books (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, status)
);