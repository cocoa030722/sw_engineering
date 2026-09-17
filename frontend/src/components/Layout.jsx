import { NavLink, Outlet } from 'react-router-dom'

// Shared shell for every route: nav bar + whatever the active route renders
// via <Outlet />. This is the "layout route" pattern from react-router-dom.
export default function Layout() {
  return (
    <div className="app-shell">
      <nav className="app-nav">
        <NavLink to="/" end>
          Home
        </NavLink>
        <NavLink to="/items">Items</NavLink>
        <NavLink to="/items/new">New Item</NavLink>
        <NavLink to="/about">About</NavLink>
      </nav>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}
