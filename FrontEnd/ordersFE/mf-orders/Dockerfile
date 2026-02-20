FROM node:20-alpine

WORKDIR /app

# Install deps first (better caching)
COPY package*.json ./
RUN npm ci

# Copy the rest of the workspace
COPY . .

# Nx/Angular dev servers
EXPOSE 4200 4201 4202 4203

# Helpful in containers
ENV NX_DAEMON=false
ENV HOST=0.0.0.0

# Run the shell; Nx will usually start remotes too (depending on Nx version/config)
CMD ["npx", "nx", "serve", "shell", "--host=0.0.0.0", "--port=4200"]
