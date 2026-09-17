import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { itemsApi } from '../api/items'

export default function ItemsListPage() {
  const [items, setItems] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    itemsApi
      .list()
      .then((data) => {
        if (!cancelled) setItems(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  async function handleDelete(id) {
    await itemsApi.remove(id)
    setItems((current) => current.filter((item) => item.id !== id))
  }

  if (loading) return <p>Loading…</p>
  if (error) return <p role="alert">Failed to load items: {error}</p>

  return (
    <section>
      <h1>Items</h1>
      <p>
        <Link to="/items/new">+ New item</Link>
      </p>
      <ul className="item-list">
        {items.map((item) => (
          <li key={item.id}>
            <Link to={`/items/${item.id}`}>{item.name}</Link>
            {item.done && <span> ✓ done</span>}
            <button type="button" onClick={() => handleDelete(item.id)}>
              Delete
            </button>
          </li>
        ))}
      </ul>
      {items.length === 0 && <p>No items yet.</p>}
    </section>
  )
}
