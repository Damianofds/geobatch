#!/bin/bash
#
#
PGPASSWD=${1}
SCHEMA=${2}
USER=${3}
DATABASE=${4}
HOST=${5}
PORT=${6}

echo "PGPASSWD:'"${PGPASSWD}"' SCHEMA:'"${SCHEMA}"' USER:'"${USER}"' DATABASE:'"${DATABASE}"' HOST:'"${HOST}"' PORT:'"${PORT}"'" >> /opt/gb_config_dir/geodatabase2pg/ogr2ogr/config/errorlog.txt

if [ $# -eq 0 ]; then
    echo "Usage:"
    echo "   createschema PGPASSWD SCHEMA USER DATABASE HOST PORT"
    echo "   only HOST and PORT aren't mandatory, if they are blank 'localhost' and '5432' values will be used"
    exit 0
fi

if [ -z "$PGPASSWD" ]; then
	echo "you must provide the password"
        exit 1
fi

if [ -z "$SCHEMA" ]; then
	echo "you must provide the schema"
        exit 1
fi

if [ -z "$USER" ]; then
        echo "you must provide the username"
        exit 1
fi

if [ -z "$DATABASE" ]; then
        echo "you must provide the database"
        exit 1
fi

if [ -z "$HOST" ]; then
	HOST="localhost"
fi

if [ -z "$PORT" ]; then
	PORT="5432"
fi

export PGPASSWORD=${PGPASSWD}
echo "PGPASSWORD: "${PGPASSWORD} >> /opt/gb_config_dir/geodatabase2pg/ogr2ogr/config/errorlog.txt
psql -w -d ${DATABASE} -h ${HOST} -p ${PORT} -U ${USER} -c "CREATE SCHEMA IF NOT EXISTS ${SCHEMA}"

