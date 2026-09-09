import { RELATIONSHIP_LABELS } from '../constants'
import { useEntityDetails } from '../hooks/useEntityDetails'
import type { EntitySelection } from '../types/selection'

interface Props {
  selection: EntitySelection | null
  onSelect: (selection: EntitySelection) => void
}

export function EntityDetailsPanel({ selection, onSelect }: Props) {
  const { loading, error, details } = useEntityDetails(selection)

  if (!selection) {
    return (
      <div className="details-panel details-panel--empty">
        <p>Search for an entity and select it to see its details.</p>
      </div>
    )
  }

  if (loading) {
    return <div className="details-panel">Loading…</div>
  }

  if (error) {
    return <div className="details-panel details-panel--error">{error}</div>
  }

  if (!details) {
    return null
  }

  const { entity, directRelationships } = details

  return (
    <div className="details-panel">
      <div className="details-panel__header">
        <span className="badge">{entity.type}</span>
        <h2>{entity.name || '(unnamed)'}</h2>
      </div>

      <dl className="details-panel__properties">
        {Object.entries(entity.properties)
          .filter(([, value]) => value !== null && value !== undefined && value !== '')
          .map(([key, value]) => (
            <div key={key}>
              <dt>{key}</dt>
              <dd>{String(value)}</dd>
            </div>
          ))}
      </dl>

      <h3>Direct relationships ({directRelationships.length})</h3>
      {directRelationships.length === 0 ? (
        <p className="details-panel__hint">No direct relationships.</p>
      ) : (
        <ul className="relationship-list">
          {directRelationships.map(({ edge, direction, other }) => (
            <li key={edge.id}>
              <span className="relationship-list__arrow">{direction === 'OUTGOING' ? '→' : '←'}</span>
              <span className="relationship-list__label">
                {RELATIONSHIP_LABELS[edge.type] ?? edge.type}
              </span>
              <button type="button" onClick={() => onSelect({ type: other.type, id: other.id })}>
                {other.name}
              </button>
              <span className="badge badge--small">{other.type}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
