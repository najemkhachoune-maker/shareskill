# Build Stage
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Build the application
# We set VITE_API_URL to empty string so requests are relative (e.g. /api/auth)
# This allows Nginx to proxy them to the backend from the same origin.
ENV VITE_API_URL=""
RUN npm run build

# Production Stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
