import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401 || error.status === 403) {
        const wasLoggedIn = authService.isLoggedIn();

        if (wasLoggedIn) {
          authService.clearSession();
          router.navigate(['/login'], {
            queryParams: { sessionExpired: 'true' },
          });
          return throwError(() => error);
        }
      }

      const message =
        error.error?.error ||
        (typeof error.error === 'object' ? Object.values(error.error)[0] : null) ||
        'Something went wrong. Please try again.';

      toastService.error(message as string);

      return throwError(() => error);
    }),
  );
};
