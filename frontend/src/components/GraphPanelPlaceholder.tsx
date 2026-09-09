import type { EntitySelection } from '../types/selection'

interface Props {
  selection: EntitySelection | null
}

/**
 * Reserves the layout position for the graph visualization. Replaced by a
 * real force-directed graph component in Phase 6 — kept as its own
 * component now so that swap is a one-file change, not a layout rewrite.
 */
export function GraphPanelPlaceholder({ selection }: Props) {
  return (
    <div className="graph-panel-placeholder">
      {selection ? (
        <p>
          Graph visualization for this <strong>{selection.type}</strong> arrives in Phase 6.
        </p>
      ) : (
        <p>Select an entity to see it here once graph visualization is added.</p>
      )}
    </div>
  )
}
