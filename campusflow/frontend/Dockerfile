FROM node:20-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
# package-lock.json in this repo may drift; prefer npm install for Docker builds
RUN npm install --no-audit --no-fund

COPY public public
COPY src src

RUN npm run build

FROM nginx:1.27-alpine

COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
