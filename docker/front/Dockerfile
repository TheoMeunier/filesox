FROM node:22.4.1-slim as build

WORKDIR /app

COPY ./front/package*.json ./

RUN npm install

COPY ./front .

RUN npm run build

FROM nginx:1.27.0-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./docker/front/nginx.conf /etc/nginx/conf.d/default.conf

RUN echo '#!/bin/sh' > /docker-entrypoint.d/env.sh && \
    echo 'envsubst < /usr/share/nginx/html/env-config.template.js > /usr/share/nginx/html/env-config.js' >> /docker-entrypoint.d/env.sh && \
    chmod +x /docker-entrypoint.d/env.sh

RUN echo 'window.env = { VITE_API_URL: "${VITE_API_URL}" }' > /usr/share/nginx/html/env-config.template.js

RUN chmod +x /docker-entrypoint.d/*