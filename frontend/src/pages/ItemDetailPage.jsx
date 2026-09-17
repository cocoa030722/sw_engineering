import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { itemsApi } from '../api/items'

export default function ItemDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [item, setItem] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    itemsApi
      .get(id)
      .then((data) => {
        if (!cancelled) setItem(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
    return () => {
      cancelled = true
    }
  }, [id])

  async function handleDelete() {
    await itemsApi.remove(id)
    navigate('/items')
  }

  if (error) return <p role="alert">Failed to load item: {error}</p>
  if (!item) return <p>Loading…</p>

  return (
    <section>
      <h1>{item.name}</h1>
      <p>{item.description || <em>No description</em>}</p>
      <p>Status: {item.done ? 'Done' : 'Not done'}</p>
      <p>Created: {new Date(item.createdAt).toLocaleString()}</p>
      <p>
        <Link to={`/items/${item.id}/edit`}>Edit</Link>{' '}
        <button type="button" onClick={handleDelete}>
          Delete
        </button>{' '}
        <Link to="/items">Back to list</Link>
      </p>
    </section>
  )
}
