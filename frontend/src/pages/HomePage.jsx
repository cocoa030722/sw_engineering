import { Link } from 'react-router-dom'

export default function HomePage() {
  return (
    <section>
      <h1>Spring Boot + React Template</h1>
      <p>
        This page is served by React Router. The <code>/api/items</code> endpoints
        below are served by Spring Boot and currently backed by an in-memory
        store — see <code>ARCHITECTURE.md</code> for how the pieces fit together.
      </p>
      <p>
        <Link to="/items">Go to the Items list &rarr;</Link>
      </p>
    </section>
  )
}
