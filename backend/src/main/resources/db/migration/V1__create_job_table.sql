CREATE TABLE job (
                     id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                     content_hash CHAR(64)    NOT NULL UNIQUE,
                     source       VARCHAR(20) NOT NULL,
                     url          TEXT,
                     description  TEXT        NOT NULL,
                     score        INTEGER     CHECK (score BETWEEN 0 AND 100),
                     reason       TEXT,
                     matched_at   TIMESTAMPTZ,
                     created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);