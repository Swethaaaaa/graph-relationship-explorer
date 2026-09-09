import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { expandGraph } from '../api/entities'
import type { GraphEdgeDto, GraphNodeDto } from '../types/api'
import type { EntitySelection } from '../types/selection'

export interface DirectRelationship {
  edge: GraphEdgeDto
  direction: 'OUTGOING' | 'INCOMING'
  other: GraphNodeDto
}

export interface EntityDetails {
  entity: GraphNodeDto
  directRelationships: DirectRelationship[]
}

interface State {
  loading: boolean
  error: string | null
  details: EntityDetails | null
}

/**
 * Loads a depth-1 subgraph centered on the selected entity and derives both
 * "the entity itself" and "its direct relationships" from it. Reusing the
 * same /api/graph/{type}/{id} endpoint the graph visualization (Phase 6)
 * calls means the details panel and the graph are always looking at
 * identical data, and it works uniformly for every entity type — unlike
 * /api/users/{id}/connections, which only exists for User.
 */
export function useEntityDetails(selection: EntitySelection | null): State {
  const [state, setState] = useState<State>({ loading: false, error: null, details: null })

  useEffect(() => {
    if (!selection) {
      setState({ loading: false, error: null, details: null })
      return
    }

    let cancelled = false
    setState({ loading: true, error: null, details: null })

    expandGraph(selection.type, selection.id, 1)
      .then((graph) => {
        if (cancelled) return
        const entity = graph.nodes.find((node) => node.id === selection.id)
        if (!entity) {
          setState({ loading: false, error: 'Entity not found in graph response', details: null })
          return
        }
        const nodesById = new Map(graph.nodes.map((node) => [node.id, node]))
        const directRelationships: DirectRelationship[] = graph.edges
          .filter((edge) => edge.sourceId === selection.id || edge.targetId === selection.id)
          .map((edge) => {
            const direction: 'OUTGOING' | 'INCOMING' = edge.sourceId === selection.id ? 'OUTGOING' : 'INCOMING'
            const otherId = direction === 'OUTGOING' ? edge.targetId : edge.sourceId
            const other = nodesById.get(otherId)
            return other ? { edge, direction, other } : null
          })
          .filter((item): item is DirectRelationship => item !== null)

        setState({ loading: false, error: null, details: { entity, directRelationships } })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        const message = err instanceof ApiError ? err.message : 'Failed to load entity details'
        setState({ loading: false, error: message, details: null })
      })

    return () => {
      cancelled = true
    }
  }, [selection?.type, selection?.id])

  return state
}
