import { useEffect, useRef, useState } from 'react'

/**
 * force-graph sizes its canvas from getBoundingClientRect() once at
 * mount/prop-change time rather than observing the container itself, so a
 * panel that resizes (window resize, layout changes) would otherwise leave
 * it stale. Tracking size here and passing it down as explicit width/height
 * props keeps the canvas correctly sized.
 */
export function useContainerSize<T extends HTMLElement>() {
  const ref = useRef<T | null>(null)
  const [size, setSize] = useState({ width: 0, height: 0 })

  useEffect(() => {
    const el = ref.current
    if (!el) return

    const observer = new ResizeObserver((entries) => {
      const entry = entries[0]
      if (!entry) return
      const { width, height } = entry.contentRect
      setSize({ width, height })
    })
    observer.observe(el)
    return () => observer.disconnect()
  }, [])

  return { ref, ...size }
}
