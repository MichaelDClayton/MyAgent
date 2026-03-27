import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // Needed for Docker
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://concierge-backend:8080', // Use the Docker container name
        changeOrigin: true,
        secure: false,
      }
    }
  }
})