import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { NotificationItem } from '../../core/models';
import { PmtApiService } from '../../core/pmt-api.service';

@Component({
  selector: 'app-notifications-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './notifications.page.html',
  styleUrl: './notifications.page.scss',
})
export class NotificationsPage implements OnInit {
  private readonly api = inject(PmtApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly notifications = signal<NotificationItem[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit() {
    if (!this.auth.isAuthenticated()) {
      this.router.navigateByUrl('/auth');
      return;
    }
    this.load();
  }

  load() {
    this.api.notifications().subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (response) => this.error.set(response.error?.message ?? 'Chargement impossible.'),
    });
  }

  markRead(notification: NotificationItem) {
    this.api.markNotificationRead(notification.id).subscribe({
      next: (updated) =>
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        ),
      error: (response) => this.error.set(response.error?.message ?? 'Lecture impossible.'),
    });
  }
}
