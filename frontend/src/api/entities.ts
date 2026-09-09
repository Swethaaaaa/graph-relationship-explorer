import { apiClient } from './client'
import type {
  CompanyResponse,
  ConnectionResponse,
  EntitySearchResult,
  EntityType,
  GraphNodeDto,
  GraphResponse,
  PageResponse,
  ProjectResponse,
  ShortestPathResponse,
  SkillResponse,
  TeamResponse,
  TechnologyResponse,
  UserResponse,
} from '../types/api'

// Maps an EntityType to its REST resource path segment (backend/src/main/java/.../controller).
export const RESOURCE_PATH: Record<EntityType, string> = {
  USER: 'users',
  COMPANY: 'companies',
  TEAM: 'teams',
  SKILL: 'skills',
  PROJECT: 'projects',
  TECHNOLOGY: 'technologies',
}

export function searchEntities(
  type: EntityType,
  query: string,
  page = 0,
  size = 20,
): Promise<PageResponse<EntitySearchResult>> {
  const params = new URLSearchParams({ q: query, page: String(page), size: String(size) })
  return apiClient.get(`/api/${RESOURCE_PATH[type]}/search?${params.toString()}`)
}

export function getUser(id: string): Promise<UserResponse> {
  return apiClient.get(`/api/users/${id}`)
}

export function getCompany(id: string): Promise<CompanyResponse> {
  return apiClient.get(`/api/companies/${id}`)
}

export function getTeam(id: string): Promise<TeamResponse> {
  return apiClient.get(`/api/teams/${id}`)
}

export function getSkill(id: string): Promise<SkillResponse> {
  return apiClient.get(`/api/skills/${id}`)
}

export function getProject(id: string): Promise<ProjectResponse> {
  return apiClient.get(`/api/projects/${id}`)
}

export function getTechnology(id: string): Promise<TechnologyResponse> {
  return apiClient.get(`/api/technologies/${id}`)
}

/** GET /api/graph/{entityType}/{id}?depth=N — see backend GraphController. */
export function expandGraph(type: EntityType, id: string, depth = 1): Promise<GraphResponse> {
  return apiClient.get(`/api/graph/${type.toLowerCase()}/${id}?depth=${depth}`)
}

export function getConnections(userId: string): Promise<ConnectionResponse[]> {
  return apiClient.get(`/api/users/${userId}/connections`)
}

export function getCommonConnections(userId: string, otherUserId: string): Promise<GraphNodeDto[]> {
  return apiClient.get(`/api/users/${userId}/common-connections/${otherUserId}`)
}

export function getShortestPath(userId: string, otherUserId: string): Promise<ShortestPathResponse> {
  return apiClient.get(`/api/users/${userId}/shortest-path/${otherUserId}`)
}

/** The display name to show for a search result row — backend response shapes differ per entity type. */
export function displayNameOf(type: EntityType, item: EntitySearchResult): string {
  if (type === 'USER') {
    return (item as UserResponse).fullName
  }
  return (item as { name: string }).name
}
