import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../core/auth.service';
import {
  Activity,
  PRIORITY_LABEL,
  ProjectTask,
  TASK_STATUS_LABEL,
  TaskPriority,
  TaskStatus,
} from '../../core/models';
import { PmtApiService } from '../../core/pmt-api.service';

@Component({
  selector: 'app-task-detail-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './task-detail.page.html',
  styleUrl: './task-detail.page.scss',
})
export class TaskDetailPage implements OnInit {
  private readonly api = inject(PmtApiService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly task = signal<ProjectTask | null>(null);
  readonly history = signal<Activity[]>([]);
  readonly error = signal<string | null>(null);
  readonly saved = signal(false);

  readonly statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];
  readonly priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  readonly statusLabel = TASK_STATUS_LABEL;
  readonly priorityLabel = PRIORITY_LABEL;

  readonly form = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    dueDate: ['', Validators.required],
    priority: ['MEDIUM' as TaskPriority, Validators.required],
    status: ['TODO' as TaskStatus, Validators.required],
  });

  get taskId() {
    return Number(this.route.snapshot.paramMap.get('taskId'));
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
      task: this.api.task(this.taskId),
      history: this.api.history(this.taskId),
    }).subscribe({
      next: ({ task, history }) => {
        this.task.set(task);
        this.history.set(history);
        this.form.reset({
          title: task.title,
          description: task.description,
          dueDate: task.dueDate,
          priority: task.priority,
          status: task.status,
        });
      },
      error: (response) => this.error.set(response.error?.message ?? 'Chargement impossible.'),
    });
  }

  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.api.updateTask(this.taskId, this.form.getRawValue()).subscribe({
      next: (task) => {
        this.task.set(task);
        this.saved.set(true);
        this.api.history(this.taskId).subscribe((history) => this.history.set(history));
        setTimeout(() => this.saved.set(false), 1800);
      },
      error: (response) => this.error.set(response.error?.message ?? 'Mise a jour impossible.'),
    });
  }
}
