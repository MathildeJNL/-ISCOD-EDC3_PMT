import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Project } from '../../core/models';
import { PmtApiService } from '../../core/pmt-api.service';

@Component({
  selector: 'app-projects-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './projects.page.html',
  styleUrl: './projects.page.scss',
})
export class ProjectsPage implements OnInit {
  private readonly api = inject(PmtApiService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);

  readonly projects = signal<Project[]>([]);
  readonly query = signal('');
  readonly showCreate = signal(false);
  readonly error = signal<string | null>(null);
  readonly createFormError = signal<string | null>(null);
  readonly user = this.auth.user;

  readonly createForm = this.fb.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    startDate: [new Date().toISOString().slice(0, 10), Validators.required],
  });

  readonly filteredProjects = computed(() => {
    const term = this.query().trim().toLowerCase();
    if (!term) {
      return this.projects();
    }
    return this.projects().filter((project) =>
      [project.name, project.description].some((value) => value.toLowerCase().includes(term)),
    );
  });

  readonly stats = computed(() => {
    const projects = this.projects();
    return {
      total: projects.length,
      active: Math.max(projects.length - 1, 0),
      done: projects.length > 0 ? 1 : 0,
      late: projects.filter((project) => new Date(project.startDate) < new Date('2024-01-01')).length,
    };
  });

  ngOnInit() {
    if (!this.auth.isAuthenticated()) {
      this.router.navigateByUrl('/auth');
      return;
    }
    this.load();
  }

  load() {
    this.api.projects().subscribe({
      next: (projects) => this.projects.set(projects),
      error: (response) => this.error.set(response.error?.message ?? 'Chargement impossible.'),
    });
  }

  openCreateModal() {
    this.createFormError.set(null);
    this.createForm.markAsUntouched();
    this.showCreate.set(true);
  }

  closeCreateModal() {
    this.createFormError.set(null);
    this.showCreate.set(false);
  }

  createProject() {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      this.createFormError.set('Éléments requis : complétez les champs obligatoires.');
      return;
    }

    this.createFormError.set(null);
    this.api.createProject(this.createForm.getRawValue()).subscribe({
      next: (project) => {
        this.projects.update((items) => [project, ...items]);
        this.closeCreateModal();
        this.createForm.reset({
          name: '',
          description: '',
          startDate: new Date().toISOString().slice(0, 10),
        });
        this.router.navigate(['/projects', project.id, 'board']);
      },
      error: (response) => this.error.set(response.error?.message ?? 'Création impossible.'),
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/auth');
  }
}
