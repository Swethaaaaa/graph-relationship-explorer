import { useState } from 'react'
import { EntityDetailsPanel } from './components/EntityDetailsPanel'
import { GraphView } from './components/GraphView'
import { SearchBar } from './components/SearchBar'
import type { EntitySelection } from './types/selection'

function App() {
  const [selection, setSelection] = useState<EntitySelection | null>(null)

  return (
    <div className="app">
      <header className="app__header">
        <h1>Graph-Based Relationship Explorer</h1>
      </header>

      <main className="app__body">
        <aside className="app__sidebar">
          <SearchBar selectedId={selection?.id ?? null} onSelect={setSelection} />
        </aside>

        <section className="app__details">
          <EntityDetailsPanel selection={selection} onSelect={setSelection} />
        </section>

        <section className="app__graph">
          <GraphView selection={selection} onSelect={setSelection} />
        </section>
      </main>
    </div>
  )
}

export default App
