export type ProjectRole = 'ADMIN' | 'MEMBER' | 'OBSERVER';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE';
export type ActivityAction = 'CREATED' | 'UPDATED' | 'STATUS_CHANGED' | 'ASSIGNED' | 'COMMENTED';

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  user: User;
}

export interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  createdAt: string;
}

export interface ProjectDetail extends Project {
  members: ProjectMember[];
}

export interface ProjectMember {
  id: number;
  userId: number;
  username: string;
  email: string;
  role: ProjectRole;
  joinedAt: string;
}

export interface Assignee {
  id: number;
  username: string;
  email: string;
}

export interface ProjectTask {
  id: number;
  projectId: number;
  title: string;
  description: string;
  dueDate: string;
  priority: TaskPriority;
  status: TaskStatus;
  assignees: Assignee[];
  createdAt: string;
  updatedAt: string;
}

export interface Activity {
  id: number;
  action: ActivityAction;
  actor: string;
  details: string;
  createdAt: string;
}

export interface NotificationItem {
  id: number;
  projectId: number;
  taskId: number | null;
  title: string;
  message: string;
  createdAt: string;
  readAt: string | null;
}

export const TASK_STATUS_LABEL: Record<TaskStatus, string> = {
  TODO: 'A faire',
  IN_PROGRESS: 'En cours',
  REVIEW: 'Revue',
  DONE: 'Termine',
};

export const PRIORITY_LABEL: Record<TaskPriority, string> = {
  LOW: 'Basse',
  MEDIUM: 'Moyenne',
  HIGH: 'Haute',
  URGENT: 'Urgente',
};
