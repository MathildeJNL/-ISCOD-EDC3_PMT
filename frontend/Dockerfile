FROM node:24.13.0-alpine AS build
WORKDIR /workspace
COPY package*.json ./
RUN npm install -g npm@11.6.2
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
