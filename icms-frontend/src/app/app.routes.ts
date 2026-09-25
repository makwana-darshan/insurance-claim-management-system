import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { LoginComponent } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { ChangePassword } from './features/account/change-password/change-password';
import { Shell } from './core/layout/shell/shell';
import { Dashboard as CustomerDashboard } from './features/customer/dashboard/dashboard';
import { Dashboard as ClaimOfficerDashboard } from './features/claim-officer/dashboard/dashboard';
import { ClaimDetail } from './features/admin/claim-detail/claim-detail';
import { Users } from './features/admin/users/users';
import { Dashboard as AdminDashboard } from './features/admin/dashboard/dashboard';
import { Profile } from './features/account/profile/profile';
import { Dashboard as SurveyorDashboard } from './features/surveyor/dashboard/dashboard';
import { CreateClaim } from './features/customer/create-claim/create-claim';
import { VerifyEmail } from './features/auth/verify-email/verify-email';
import { ClaimTimeline } from './features/customer/claim-timeline/claim-timeline';
import { roleGuard } from './core/guards/role.guard';
import { authGuard } from './core/guards/auth.guard';
import { dashboardRedirectGuard } from './core/guards/dashboard-redirect.guard';
import { ForgotPassword } from './features/auth/forgot-password/forgot-password';
import { ResetPassword } from './features/auth/reset-password/reset-password';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'verify-email', component: VerifyEmail },

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
      {
        path: 'dashboard',
        canActivate: [dashboardRedirectGuard],
        children: [],
      },
      {
        path: 'admin/users',
        component: Users,
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN' },
      },
      {
        path: 'customer/claims/:id/edit',
        component: CreateClaim,
        canActivate: [roleGuard],
        data: { role: 'CUSTOMER' },
      },
      {
        path: 'admin/claims/:id',
        component: ClaimDetail,
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN' },
      },
      {
        path: 'account/profile',
        component: Profile,
      },
      {
        path: 'account/change-password',
        component: ChangePassword,
      },
    ],
  },

  { path: '**', redirectTo: '/login' },
];
