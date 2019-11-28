FROM node:11-alpine as builder
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
EXPOSE 80
EXPOSE 443
COPY --from=builder /app/build /usr/share/nginx/html
