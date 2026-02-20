import { NxWelcome } from './nx-welcome';
import { Route } from '@angular/router';
import { AuthStore } from '@org/shared-auth';
import { inject } from '@angular/core';
import { Dashboard } from './dashboard/dashboard';

const requireAuth = () => {
  const auth = inject(AuthStore);
  console.log(auth.token);
  return auth.token ? true : false;
};

export const appRoutes: Route[] = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },

  { path: 'auth', loadChildren: () => import('auth/Routes').then(m => m.remoteRoutes) },

  {
    path: 'dashboard',
    canMatch: [requireAuth],
    component: Dashboard,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: '/dashboard/(left:new-order//right:orders)',
      },

      // left outlet: orders form
      {
        path: 'new-order',
        outlet: 'left',
        loadChildren: () => import('orders_form/Routes').then(m => m.remoteRoutes),
      },

      // right outlet: orders list
      {
        path: 'orders',
        outlet: 'right',
        loadChildren: () => import('orders_list/Routes').then(m => m.remoteRoutes),
      },
    ],
  },

  { path: 'orders/new', canMatch: [requireAuth], loadChildren: () => import('orders_form/Routes').then(m => m.remoteRoutes) },
  { path: 'orders', canMatch: [requireAuth], loadChildren: () => import('orders_list/Routes').then(m => m.remoteRoutes) },

  { path: '**', redirectTo: 'auth' },
];
