#!/bin/bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER spring WITH PASSWORD 'secret';
    CREATE DATABASE springnative;
    GRANT ALL PRIVILEGES ON DATABASE springnative TO spring;
EOSQL
