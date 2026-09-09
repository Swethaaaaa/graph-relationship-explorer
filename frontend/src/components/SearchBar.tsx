import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { displayNameOf, searchEntities } from '../api/entities'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import type { EntitySearchResult, EntityType } from '../types/api'
import type { EntitySelection } from '../types/selection'

const ENTITY_TYPES: { value: EntityType; label: string }[] = [
  { value: 'USER', label: 'Users' },
  { value: 'COMPANY', label: 'Companies' },
  { value: 'TEAM', label: 'Teams' },
  { value: 'SKILL', label: 'Skills' },
  { value: 'PROJECT', label: 'Projects' },
  { value: 'TECHNOLOGY', label: 'Technologies' },
]

interface Props {
  selectedId: string | null
  onSelect: (selection: EntitySelection) => void
}

export function SearchBar({ selectedId, onSelect }: Props) {
  const [entityType, setEntityType] = useState<EntityType>('USER')
  const [query, setQuery] = useState('')
  const [results, setResults] = useState<EntitySearchResult[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const debouncedQuery = useDebouncedValue(query, 300)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)

    searchEntities(entityType, debouncedQuery)
      .then((page) => {
        if (!cancelled) {
          setResults(page.content)
          setLoading(false)
        }
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setError(err instanceof ApiError ? err.message : 'Search failed')
        setResults([])
        setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [entityType, debouncedQuery])

  return (
    <div className="search-bar">
      <div className="search-bar__controls">
        <select
          value={entityType}
          onChange={(e) => setEntityType(e.target.value as EntityType)}
          aria-label="Entity type"
        >
          {ENTITY_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
        <input
          type="search"
          placeholder={`Search ${entityType.toLowerCase()}s by name…`}
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          aria-label="Search query"
        />
      </div>

      {error && <p className="search-bar__error">{error}</p>}
      {loading && <p className="search-bar__hint">Searching…</p>}
      {!loading && !error && query && results.length === 0 && (
        <p className="search-bar__hint">No results for "{query}"</p>
      )}

      <ul className="search-bar__results">
        {results.map((item) => (
          <li key={item.id}>
            <button
              type="button"
              className={item.id === selectedId ? 'is-selected' : ''}
              onClick={() => onSelect({ type: entityType, id: item.id })}
            >
              {displayNameOf(entityType, item)}
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
