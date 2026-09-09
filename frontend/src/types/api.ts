// Mirrors the backend's dto/response and domain/enums packages. Kept as a
// single hand-written file (not generated) since the API surface is small
// and stable enough that codegen would be more ceremony than it's worth —
// see docs/ARCHITECTURE.md in the repo root for the tradeoff.

export type EntityType = 'USER' | 'COMPANY' | 'TEAM' | 'SKILL' | 'PROJECT' | 'TECHNOLOGY'

export type RelationshipType =
  | 'USER_WORKS_AT_COMPANY'
  | 'USER_MEMBER_OF_TEAM'
  | 'USER_HAS_SKILL'
  | 'USER_WORKED_ON_PROJECT'
  | 'PROJECT_USES_TECHNOLOGY'
  | 'USER_COLLABORATED_WITH_USER'

export interface ApiErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fieldErrors?: Record<string, string>
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface UserResponse {
  id: string
  fullName: string
  email: string
  title?: string | null
  bio?: string | null
  location?: string | null
  createdAt: string
}

export interface CompanyResponse {
  id: string
  name: string
  industry?: string | null
  website?: string | null
  size?: string | null
  foundedYear?: number | null
}

export interface TeamResponse {
  id: string
  name: string
  description?: string | null
  department?: string | null
}

export interface SkillResponse {
  id: string
  name: string
  category?: string | null
}

export interface ProjectResponse {
  id: string
  name: string
  description?: string | null
  status: string
  startDate?: string | null
  endDate?: string | null
}

export interface TechnologyResponse {
  id: string
  name: string
  category?: string | null
}

export type EntitySearchResult =
  | UserResponse
  | CompanyResponse
  | TeamResponse
  | SkillResponse
  | ProjectResponse
  | TechnologyResponse

export interface GraphNodeDto {
  id: string
  type: EntityType
  name: string
  properties: Record<string, unknown>
}

export interface GraphEdgeDto {
  id: string
  type: RelationshipType
  sourceId: string
  targetId: string
  properties: Record<string, unknown>
}

export interface GraphResponse {
  nodes: GraphNodeDto[]
  edges: GraphEdgeDto[]
  depth: number
}

export interface ConnectionResponse {
  relationshipType: RelationshipType
  direction: 'OUTGOING' | 'INCOMING'
  relationshipProperties: Record<string, unknown>
  connectedEntity: GraphNodeDto
}

export interface ShortestPathResponse {
  length: number
  nodes: GraphNodeDto[]
  edges: GraphEdgeDto[]
}
