FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5173
# The --host flag is CRITICAL for Docker to let you see the site on your Mac
CMD ["npm", "run", "dev", "--", "--host"]