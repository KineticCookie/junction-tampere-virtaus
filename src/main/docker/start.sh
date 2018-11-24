#!/usr/bin/env sh

[ -z "$DATABASE_HOST" ] && DATABASE_HOST="localhost"
[ -z "$DATABASE_PORT" ] && DATABASE_PORT="5432"
[ -z "$DATABASE_NAME" ] && DATABASE_NAME="docker"
[ -z "$DATABASE_USERNAME" ] && DATABASE_USERNAME="docker"
[ -z "$DATABASE_PASSWORD" ] && DATABASE_PASSWORD="docker"

if [ "$CUSTOM_CONFIG" = "" ]
then
    APP_OPTS="$APP_OPTS -Ddb.jdbc-url=jdbc:postgresql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME"
    APP_OPTS="$APP_OPTS -Ddb.username=$DATABASE_USERNAME -Ddb.password=$DATABASE_PASSWORD"
    echo "No CUSTOM_CONFIG file"
else
   APP_OPTS="$APP_OPTS -Dconfig.file=$CUSTOM_CONFIG"
fi

echo "Running with opts:"
echo "JAVA_OPTS=$JAVA_OPTS"
echo "APP_OPTS=$APP_OPTS"

java $JAVA_OPTS $APP_OPTS -cp "/app/main.jar:/app/lib/*" io.kineticcookie.junction2018.Main
