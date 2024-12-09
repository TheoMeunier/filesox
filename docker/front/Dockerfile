FROM node:22.4.1-slim as build

WORKDIR /app

COPY ./front/package*.json ./

RUN npm install

COPY ./front/ .

RUN npm run build

FROM nginx:1.27.0-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./docker/front/nginx.conf /etc/nginx/conf.d/default.conf

# add env.sh to docker-entrypoint.d
COPY  ./docker/front/env.sh /docker-entrypoint.d/env.sh
RUN chmod +x /docker-entrypoint.d/env.sh