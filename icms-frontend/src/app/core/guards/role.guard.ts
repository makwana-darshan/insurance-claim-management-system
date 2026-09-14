import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRole = route.data['role'] as string;

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (authService.hasRole(requiredRole)) {
    return true;
  }

  // Logged in, but wrong role -> send back to their own dashboard, not login
  router.navigate([authService.getDefaultRouteForUser()]);
  return false;
};