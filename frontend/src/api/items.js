import { apiClient } from './client'

// Thin wrapper around the /api/items endpoints (see ItemController.java).
// Pages call these functions instead of using axios directly, so the HTTP
// details stay in one place.
export const itemsApi = {
  list: () => apiClient.get('/items').then((res) => res.data),
  get: (id) => apiClient.get(`/items/${id}`).then((res) => res.data),
  create: (item) => apiClient.post('/items', item).then((res) => res.data),
  update: (id, item) => apiClient.put(`/items/${id}`, item).then((res) => res.data),
  remove: (id) => apiClient.delete(`/items/${id}`),
}
