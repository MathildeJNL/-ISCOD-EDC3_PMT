import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import {
  Activity,
  NotificationItem,
  Project,
  ProjectDetail,
  ProjectMember,
  ProjectRole,
  ProjectTask,
  TaskPriority,
  TaskStatus,
} from './models';
import { AuthService } from './auth.service';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class PmtApiService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  projects() {
    return this.http.get<Project[]>(`${API_URL}/projects`, { headers: this.headers() });
  }

  createProject(payload: { name: string; description: string; startDate: string }) {
    return this.http.post<ProjectDetail>(`${API_URL}/projects`, payload, { headers: this.headers() });
  }

  project(projectId: number) {
    return this.http.get<ProjectDetail>(`${API_URL}/projects/${projectId}`, { headers: this.headers() });
  }

  members(projectId: number) {
    return this.http.get<ProjectMember[]>(`${API_URL}/projects/${projectId}/members`, {
      headers: this.headers(),
    });
  }

  inviteMember(projectId: number, payload: { email: string; role: ProjectRole }) {
    return this.http.post<ProjectMember>(`${API_URL}/projects/${projectId}/members`, payload, {
      headers: this.headers(),
    });
  }

  changeRole(projectId: number, memberId: number, role: ProjectRole) {
    return this.http.patch<ProjectMember>(
      `${API_URL}/projects/${projectId}/members/${memberId}/role`,
      { role },
      { headers: this.headers() },
    );
  }

  tasks(projectId: number, status?: TaskStatus) {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<ProjectTask[]>(`${API_URL}/projects/${projectId}/tasks`, {
      headers: this.headers(),
      params,
    });
  }

  createTask(
    projectId: number,
    payload: {
      title: string;
      description: string;
      dueDate: string;
      priority: TaskPriority;
      assigneeIds: number[];
    },
  ) {
    return this.http.post<ProjectTask>(`${API_URL}/projects/${projectId}/tasks`, payload, {
      headers: this.headers(),
    });
  }

  task(taskId: number) {
    return this.http.get<ProjectTask>(`${API_URL}/tasks/${taskId}`, { headers: this.headers() });
  }

  updateTask(
    taskId: number,
    payload: Partial<{
      title: string;
      description: string;
      dueDate: string;
      priority: TaskPriority;
      status: TaskStatus;
    }>,
  ) {
    return this.http.patch<ProjectTask>(`${API_URL}/tasks/${taskId}`, payload, { headers: this.headers() });
  }

  assignTask(taskId: number, assigneeIds: number[]) {
    return this.http.post<ProjectTask>(
      `${API_URL}/tasks/${taskId}/assignments`,
      { assigneeIds },
      { headers: this.headers() },
    );
  }

  history(taskId: number) {
    return this.http.get<Activity[]>(`${API_URL}/tasks/${taskId}/history`, { headers: this.headers() });
  }

  notifications() {
    return this.http.get<NotificationItem[]>(`${API_URL}/notifications`, { headers: this.headers() });
  }

  markNotificationRead(notificationId: number) {
    return this.http.patch<NotificationItem>(
      `${API_URL}/notifications/${notificationId}/read`,
      {},
      { headers: this.headers() },
    );
  }

  private headers() {
    return new HttpHeaders({ 'X-User-Id': String(this.auth.userId()) });
  }
}
