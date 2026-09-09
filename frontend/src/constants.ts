import type { EntityType, RelationshipType } from './types/api'

export const RELATIONSHIP_LABELS: Record<RelationshipType, string> = {
  USER_WORKS_AT_COMPANY: 'works at',
  USER_MEMBER_OF_TEAM: 'member of',
  USER_HAS_SKILL: 'has skill',
  USER_WORKED_ON_PROJECT: 'worked on',
  PROJECT_USES_TECHNOLOGY: 'uses',
  USER_COLLABORATED_WITH_USER: 'collaborated with',
}

export const ENTITY_COLORS: Record<EntityType, string> = {
  USER: '#5b8def',
  COMPANY: '#e6a23c',
  TEAM: '#67c23a',
  SKILL: '#909399',
  PROJECT: '#f56c6c',
  TECHNOLOGY: '#9b59b6',
}

export const MAX_TRAVERSAL_DEPTH = 4
