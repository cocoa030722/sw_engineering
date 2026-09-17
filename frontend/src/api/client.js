import axios from 'axios'

// Relative base URL: in dev, Vite's proxy (vite.config.js) forwards `/api/*`
// to the Spring Boot server; in production, the backend serves the built
// frontend itself, so `/api/*` is same-origin there too. Either way the
// frontend code never needs to know the backend's host/port.
export const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})
