import { Routes } from '@angular/router';

export const appRoutes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  {
    path: 'consultants',
    loadChildren: () => import('./features/consultants/consultants.module').then(m => m.ConsultantsModule)
  },
  {
    path: 'projects',
    loadChildren: () => import('./features/projects/projects.module').then(m => m.ProjectsModule)
  },
  {
    path: 'skills',
    loadChildren: () => import('./features/skills/skills.module').then(m => m.SkillsModule)
  },
  {
    path: 'evaluations',
    loadChildren: () => import('./features/evaluations/evaluations.module').then(m => m.EvaluationsModule)
  }
];
