import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../core/auth.service';
import {
  PRIORITY_LABEL,
  ProjectDetail,
  ProjectMember,
  ProjectRole,
  ProjectTask,
  TASK_STATUS_LABEL,
  TaskPriority,
  TaskStatus,
} from '../../core/models';
import { PmtApiService } from '../../core/pmt-api.service';

@Component({
  selector: 'app-board-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './board.page.html',
  styleUrl: './board.page.scss',
})
export class BoardPage implements OnInit {
  private readonly api = inject(PmtApiService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly project = signal<ProjectDetail | null>(null);
  readonly members = signal<ProjectMember[]>([]);
  readonly tasks = signal<ProjectTask[]>([]);
  readonly showTaskForm = signal(false);
  readonly showInviteForm = signal(false);
  readonly error = signal<string | null>(null);

  readonly statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];
  readonly priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  readonly roles: ProjectRole[] = ['ADMIN', 'MEMBER', 'OBSERVER'];
  readonly statusLabel = TASK_STATUS_LABEL;
  readonly priorityLabel = PRIORITY_LABEL;

  readonly taskForm = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    dueDate: [new Date().toISOString().slice(0, 10), Validators.required],
    priority: ['MEDIUM' as TaskPriority, Validators.required],
    assigneeId: [0],
  });

  readonly inviteForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    role: ['MEMBER' as ProjectRole, Validators.required],
  });

  get projectId() {
    return Number(this.route.snapshot.paramMap.get('projectId'));
  }

  ngOnInit() {
    if (!this.auth.isAuthenticated()) {
      this.router.navigateByUrl('/auth');
      return;
    }
    this.load();
  }

  load() {
    forkJoin({
      project: this.api.project(this.projectId),
      members: this.api.members(this.projectId),
      tasks: this.api.tasks(this.projectId),
    }).subscribe({
      next: ({ project, members, tasks }) => {
        this.project.set(project);
        this.members.set(members);
        this.tasks.set(tasks);
      },
      error: (response) => this.error.set(response.error?.message ?? 'Chargement impossible.'),
    });
  }

  tasksByStatus(status: TaskStatus) {
    return this.tasks().filter((task) => task.status === status);
  }

  createTask() {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }
    const value = this.taskForm.getRawValue();
    const assigneeIds = value.assigneeId > 0 ? [value.assigneeId] : [];

    this.api
      .createTask(this.projectId, {
        title: value.title,
        description: value.description,
        dueDate: value.dueDate,
        priority: value.priority,
        assigneeIds,
      })
      .subscribe({
        next: (task) => {
          this.tasks.update((items) => [task, ...items]);
          this.showTaskForm.set(false);
          this.taskForm.reset({
            title: '',
            description: '',
            dueDate: new Date().toISOString().slice(0, 10),
            priority: 'MEDIUM',
            assigneeId: 0,
          });
        },
        error: (response) => this.error.set(response.error?.message ?? 'Creation impossible.'),
      });
  }

  inviteMember() {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }
    this.api.inviteMember(this.projectId, this.inviteForm.getRawValue()).subscribe({
      next: (member) => {
        this.members.update((items) => [...items, member]);
        this.showInviteForm.set(false);
        this.inviteForm.reset({ email: '', role: 'MEMBER' });
      },
      error: (response) => this.error.set(response.error?.message ?? 'Invitation impossible.'),
    });
  }

  moveTask(task: ProjectTask, status: TaskStatus) {
    if (task.status === status) {
      return;
    }
    this.api.updateTask(task.id, { status }).subscribe({
      next: (updated) =>
        this.tasks.update((items) => items.map((item) => (item.id === updated.id ? updated : item))),
      error: (response) => this.error.set(response.error?.message ?? 'Mise a jour impossible.'),
    });
  }

  changeRole(member: ProjectMember, event: Event) {
    const role = (event.target as HTMLSelectElement).value as ProjectRole;
    this.api.changeRole(this.projectId, member.id, role).subscribe({
      next: (updated) =>
        this.members.update((items) => items.map((item) => (item.id === updated.id ? updated : item))),
      error: (response) => this.error.set(response.error?.message ?? 'Role impossible a modifier.'),
    });
  }
}
