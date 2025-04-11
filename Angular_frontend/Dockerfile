### STAGE 1: Build ###
FROM node:18 AS build

WORKDIR /usr/src/app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Angular app for production
RUN npm run build --prod

### STAGE 2: Serve ###
FROM nginx:alpine

# Copy the built Angular app from the build stage to Nginx's public folder
COPY --from=build /usr/src/app/dist/gestionSki-front /usr/share/nginx/html

# Expose the default port for Nginx (80)
EXPOSE 80

# Start Nginx to serve the Angular app
CMD ["nginx", "-g", "daemon off;"]