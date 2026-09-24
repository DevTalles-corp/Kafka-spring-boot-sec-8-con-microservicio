CREATE TABLE IF NOT EXISTS event_publication (
    id                     UUID                     NOT NULL PRIMARY KEY,
    listener_id            VARCHAR                  NOT NULL,
    event_type             VARCHAR                  NOT NULL,
    serialized_event       VARCHAR                  NOT NULL,
    publication_date       TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date        TIMESTAMP WITH TIME ZONE,
    status                 VARCHAR(20),
    completion_attempts    INT                      NOT NULL DEFAULT 0,
    last_resubmission_date TIMESTAMP WITH TIME ZONE
);