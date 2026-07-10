import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AuthService } from './auth.service';
import { PmtApiService } from './pmt-api.service';

describe('PmtApiService', () => {
  let service: PmtApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({
        id: 42,
        username: 'mathilde',
        email: 'mathilde@example.com',
        createdAt: '2026-07-08T10:00:00Z',
      }),
    );
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    service = TestBed.inject(PmtApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('adds the current user id header to project requests', () => {
    service.projects().subscribe((projects) => expect(projects).toEqual([]));

    const request = http.expectOne('http://localhost:8080/api/projects');
    expect(request.request.headers.get('X-User-Id')).toBe('42');
    request.flush([]);
  });

  it('creates tasks with assignee ids', () => {
    service
      .createTask(9, {
        title: 'Task',
        description: 'Description',
        dueDate: '2026-07-12',
        priority: 'HIGH',
        assigneeIds: [42],
      })
      .subscribe((task) => expect(task.id).toBe(3));

    const request = http.expectOne('http://localhost:8080/api/projects/9/tasks');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.assigneeIds).toEqual([42]);
    request.flush({
      id: 3,
      projectId: 9,
      title: 'Task',
      description: 'Description',
      dueDate: '2026-07-12',
      priority: 'HIGH',
      status: 'TODO',
      assignees: [],
      createdAt: '2026-07-08T10:00:00Z',
      updatedAt: '2026-07-08T10:00:00Z',
    });
  });
});
