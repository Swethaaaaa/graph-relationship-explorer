import { useCallback, useEffect, useMemo, useState } from 'react'
import ForceGraph2D from 'react-force-graph-2d'
import type { LinkObject, NodeObject } from 'react-force-graph-2d'
import { ApiError } from '../api/client'
import { expandGraph } from '../api/entities'
import { ENTITY_COLORS, MAX_TRAVERSAL_DEPTH, RELATIONSHIP_LABELS } from '../constants'
import { useContainerSize } from '../hooks/useContainerSize'
import type { EntityType, GraphResponse, RelationshipType } from '../types/api'
import type { EntitySelection } from '../types/selection'

interface Props {
  selection: EntitySelection | null
  onSelect: (selection: EntitySelection) => void
}

interface FGNode {
  id: string
  name: string
  type: EntityType
}

interface FGLink {
  id: string
  source: string
  target: string
  type: RelationshipType
}

/**
 * Renders the subgraph reachable from the selected entity using
 * react-force-graph-2d (a canvas-based force-directed layout on top of
 * d3-force) rather than a hand-rolled rendering engine. Zoom, pan, and node
 * dragging are the library's default interactions; "expanding connections"
 * is implemented as re-centering the visualization on whichever node the
 * user clicks (which also updates the search/details panel selection, so
 * all three panels always agree on what's selected).
 */
export function GraphView({ selection, onSelect }: Props) {
  const { ref: containerRef, width, height } = useContainerSize<HTMLDivElement>()
  const [depth, setDepth] = useState(1)
  const [graph, setGraph] = useState<GraphResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!selection) {
      setGraph(null)
      return
    }

    let cancelled = false
    setLoading(true)
    setError(null)

    expandGraph(selection.type, selection.id, depth)
      .then((response) => {
        if (!cancelled) {
          setGraph(response)
          setLoading(false)
        }
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setError(err instanceof ApiError ? err.message : 'Failed to load graph')
        setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [selection?.type, selection?.id, depth])

  const graphData = useMemo(() => {
    if (!graph) {
      return { nodes: [] as FGNode[], links: [] as FGLink[] }
    }
    return {
      nodes: graph.nodes.map((node) => ({ id: node.id, name: node.name, type: node.type })),
      links: graph.edges.map((edge) => ({
        id: edge.id,
        source: edge.sourceId,
        target: edge.targetId,
        type: edge.type,
      })),
    }
  }, [graph])

  const drawNode = useCallback(
    (node: NodeObject<FGNode>, ctx: CanvasRenderingContext2D, globalScale: number) => {
      const isSelected = node.id === selection?.id
      const radius = isSelected ? 7 : 5
      const x = node.x ?? 0
      const y = node.y ?? 0

      ctx.beginPath()
      ctx.arc(x, y, radius, 0, 2 * Math.PI, false)
      ctx.fillStyle = ENTITY_COLORS[node.type]
      ctx.fill()
      if (isSelected) {
        ctx.lineWidth = 2 / globalScale
        ctx.strokeStyle = '#ffffff'
        ctx.stroke()
      }

      const fontSize = 11 / globalScale
      ctx.font = `${fontSize}px sans-serif`
      ctx.textAlign = 'center'
      ctx.textBaseline = 'top'
      ctx.fillStyle = '#e6e8ec'
      ctx.fillText(node.name, x, y + radius + 2)
    },
    [selection?.id],
  )

  const drawLinkLabel = useCallback((link: LinkObject<FGNode, FGLink>, ctx: CanvasRenderingContext2D, globalScale: number) => {
    const source = link.source
    const target = link.target
    if (typeof source !== 'object' || typeof target !== 'object') return
    const sx = (source as NodeObject<FGNode>).x
    const sy = (source as NodeObject<FGNode>).y
    const tx = (target as NodeObject<FGNode>).x
    const ty = (target as NodeObject<FGNode>).y
    if (sx === undefined || sy === undefined || tx === undefined || ty === undefined) return

    const midX = (sx + tx) / 2
    const midY = (sy + ty) / 2
    const label = RELATIONSHIP_LABELS[link.type] ?? link.type
    const fontSize = 8 / globalScale
    ctx.font = `${fontSize}px sans-serif`
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillStyle = 'rgba(230, 232, 236, 0.65)'
    ctx.fillText(label, midX, midY)
  }, [])

  return (
    <div className="graph-view">
      <div className="graph-view__toolbar">
        <label>
          Depth
          <select value={depth} onChange={(e) => setDepth(Number(e.target.value))} disabled={!selection}>
            {Array.from({ length: MAX_TRAVERSAL_DEPTH }, (_, i) => i + 1).map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>
        </label>
        {graph && (
          <span className="graph-view__stats">
            {graph.nodes.length} nodes · {graph.edges.length} edges
          </span>
        )}
      </div>

      <div className="graph-view__canvas" ref={containerRef}>
        {!selection && <p className="graph-view__hint">Select an entity to visualize its graph.</p>}
        {selection && loading && <p className="graph-view__hint">Loading graph…</p>}
        {selection && error && <p className="graph-view__hint graph-view__hint--error">{error}</p>}
        {selection && !loading && !error && graph && width > 0 && height > 0 && (
          <ForceGraph2D<FGNode, FGLink>
            graphData={graphData}
            width={width}
            height={height}
            backgroundColor="#00000000"
            nodeCanvasObject={drawNode}
            nodeLabel={(node) => `${node.name} (${node.type})`}
            linkCanvasObjectMode={() => 'after'}
            linkCanvasObject={drawLinkLabel}
            linkLabel={(link) => RELATIONSHIP_LABELS[link.type] ?? link.type}
            linkDirectionalArrowLength={4}
            linkDirectionalArrowRelPos={1}
            linkColor={() => 'rgba(154, 161, 172, 0.6)'}
            onNodeClick={(node) => onSelect({ type: node.type, id: node.id as string })}
            cooldownTicks={100}
          />
        )}
      </div>
    </div>
  )
}
