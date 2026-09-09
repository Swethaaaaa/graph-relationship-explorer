import type { ApiErrorResponse } from '../types/api'

// Empty in dev (Vite proxies /api to the backend, see vite.config.ts) and
// set at build time in Docker for prod (see Phase 9). Plain fetch, not a
// library like axios — the request surface here is small enough that a
// dependency would add more weight than it saves.
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

export class ApiError extends Error {
  readonly status: number
  readonly body?: ApiErrorResponse

  constructor(status: number, body: ApiErrorResponse | undefined) {
    super(body?.message ?? `Request failed with status ${status}`)
    this.status = status
    this.body = body
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  })

  if (!response.ok) {
    let body: ApiErrorResponse | undefined
    try {
      body = (await response.json()) as ApiErrorResponse
    } catch {
      // response had no JSON body; leave `body` undefined
    }
    throw new ApiError(response.status, body)
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

export const apiClient = {
  get: <T>(path: string): Promise<T> => request<T>(path),
  post: <T>(path: string, body: unknown): Promise<T> =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  del: (path: string): Promise<void> => request<void>(path, { method: 'DELETE' }),
}
