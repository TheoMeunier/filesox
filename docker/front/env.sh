#!/bin/sh
for i in $(env | grep APP_)

do
    key=$(echo $i | cut -d '=' -f 1)
    value=$(echo $i | cut -d '=' -f 2-)
    echo $key=$value

    find /usr/share/nginx/html -type f -name '*.js' -exec sed -i "s|${key}|${value}|g" '{}' +
done

echo 'done'