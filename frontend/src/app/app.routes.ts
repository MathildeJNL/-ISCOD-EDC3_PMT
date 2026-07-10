import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth',
    loadComponent: () => import('./pages/auth/auth.page').then((m) => m.AuthPage),
  },
  {
    path: 'projects',
    loadComponent: () => import('./pages/projects/projects.page').then((m) => m.ProjectsPage),
  },
  {
    path: 'projects/:projectId/board',
    loadComponent: () => import('./pages/board/board.page').then((m) => m.BoardPage),
  },
  {
    path: 'tasks/:taskId',
    loadComponent: () => import('./pages/task-detail/task-detail.page').then((m) => m.TaskDetailPage),
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./pages/notifications/notifications.page').then((m) => m.NotificationsPage),
  },
  { path: '', pathMatch: 'full', redirectTo: 'auth' },
  { path: '**', redirectTo: 'auth' },
];
