import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-auth-page',
  imports: [ReactiveFormsModule],
  templateUrl: './auth.page.html',
  styleUrl: './auth.page.scss',
})
export class AuthPage {
  private readonly auth = inject(AuthService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);

  readonly mode = signal<'login' | 'register'>('login');
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group(
    {
      username: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: [''],
    },
    { validators: this.passwordsMatchValidator() },
  );

  constructor() {
    this.configureModeValidators(this.mode());
  }

  switchMode(mode: 'login' | 'register') {
    this.mode.set(mode);
    this.error.set(null);
    this.configureModeValidators(mode);
    this.form.markAsUntouched();
  }

  submit() {
    this.configureModeValidators(this.mode());
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Éléments requis : complétez les champs obligatoires.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    const value = this.form.getRawValue();
    const email = value.email.trim();
    const request =
      this.mode() === 'login'
        ? this.auth.login({ email, password: value.password })
        : this.auth.register({
            username: value.username.trim(),
            email,
            password: value.password,
          });

    request.subscribe({
      next: () => this.router.navigateByUrl('/projects'),
      error: (response) => {
        this.loading.set(false);
        this.error.set(this.toAuthErrorMessage(response));
      },
    });
  }

  hasError(field: keyof typeof this.form.controls, error: string): boolean {
    const control = this.form.controls[field];
    return control.hasError(error) && (control.dirty || control.touched);
  }

  fieldInvalid(field: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[field];
    return control.invalid && (control.dirty || control.touched);
  }

  showPasswordMismatch(): boolean {
    const confirmPassword = this.form.controls.confirmPassword;
    return (
      this.mode() === 'register' &&
      this.form.hasError('passwordMismatch') &&
      (confirmPassword.dirty || confirmPassword.touched)
    );
  }

  private configureModeValidators(mode: 'login' | 'register') {
    this.form.controls.username.setValidators(
      mode === 'register' ? [Validators.required, Validators.minLength(3), Validators.maxLength(80)] : [],
    );
    this.form.controls.password.setValidators(
      mode === 'register'
        ? [Validators.required, Validators.minLength(8), passwordStrengthValidator()]
        : [Validators.required],
    );
    this.form.controls.confirmPassword.setValidators(mode === 'register' ? [Validators.required] : []);

    this.form.controls.username.updateValueAndValidity({ emitEvent: false, onlySelf: true });
    this.form.controls.password.updateValueAndValidity({ emitEvent: false, onlySelf: true });
    this.form.controls.confirmPassword.updateValueAndValidity({ emitEvent: false, onlySelf: true });
    this.form.updateValueAndValidity({ emitEvent: false });
  }

  private passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.mode() !== 'register') {
        return null;
      }

      const password = control.get('password')?.value;
      const confirmPassword = control.get('confirmPassword')?.value;
      if (!confirmPassword || password === confirmPassword) {
        return null;
      }

      return { passwordMismatch: true };
    };
  }

  private toAuthErrorMessage(response: unknown): string {
    if (!(response instanceof HttpErrorResponse)) {
      return 'Impossible de vous authentifier.';
    }

    if (response.status === 0) {
      return "Impossible de joindre l'API. Vérifiez que le backend est lancé.";
    }
    if (response.status === 403) {
      return 'E-mail ou mot de passe incorrect.';
    }
    if (response.status === 409) {
      const message = response.error?.message as string | undefined;
      if (message?.includes('Email')) {
        return 'Cet e-mail est déjà utilisé.';
      }
      if (message?.includes('Username')) {
        return 'Ce nom utilisateur est déjà utilisé.';
      }
    }
    if (response.status >= 500) {
      return "Erreur serveur : la base locale ou l'API n'est pas prête. Le backend doit être relancé en profil dev.";
    }

    const apiMessage = typeof response.error?.message === 'string' ? response.error.message : null;
    return apiMessage ?? `Erreur ${response.status} : impossible de vous authentifier.`;
  }
}

function passwordStrengthValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = String(control.value ?? '');
    if (!value) {
      return null;
    }

    const hasLowercase = /[a-z]/.test(value);
    const hasUppercase = /[A-Z]/.test(value);
    const hasNumber = /\d/.test(value);

    return hasLowercase && hasUppercase && hasNumber ? null : { passwordStrength: true };
  };
}
