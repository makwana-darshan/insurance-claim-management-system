import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { Shell } from './core/layout/shell/shell';
import { Dashboard as CustomerDashboard } from './features/customer/dashboard/dashboard';
import { Dashboard as ClaimOfficerDashboard } from './features/claim-officer/dashboard/dashboard';
import { Dashboard as AdminDashboard } from './features/admin/dashboard/dashboard';
import { Dashboard as SurveyorDashboard } from './features/surveyor/dashboard/dashboard';
import { CreateClaim } from './features/customer/create-claim/create-claim';
import { ClaimTimeline } from './features/customer/claim-timeline/claim-timeline';
import { roleGuard } from './core/guards/role.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      {
        path: 'customer/dashboard',
        component: CustomerDashboard,
        canActivate: [roleGuard],
        data: { role: 'CUSTOMER' },
      },
      {
        path: 'claim-officer/dashboard',
        component: ClaimOfficerDashboard,
        canActivate: [roleGuard],
        data: { role: 'CLAIM_OFFICER' },
      },
      {
        path: 'admin/dashboard',
        component: AdminDashboard,
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN' },
      },
      {
        path: 'surveyor/dashboard',
        component: SurveyorDashboard,
        canActivate: [roleGuard],
        data: { role: 'SURVEYOR' },
      },
      {
        path: 'customer/claims/new',
        component: CreateClaim,
        canActivate: [roleGuard],
        data: { role: 'CUSTOMER' },
      },
      {
        path: 'customer/claims/:id/timeline',
        component: ClaimTimeline,
        canActivate: [roleGuard],
        data: { role: 'CUSTOMER' },
      },
    ],
  },

  { path: '**', redirectTo: '/login' },
];
