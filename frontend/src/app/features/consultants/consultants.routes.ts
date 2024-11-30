import { Routes } from '@angular/router';
import { ConsultantListComponent } from './consultant-list/consultant-list.component';
import { ConsultantFormComponent } from './consultant-form/consultant-form.component';

export const consultantRoutes: Routes = [
  {
    path: '',
    component: ConsultantListComponent
  },
  {
    path: 'new',
    component: ConsultantFormComponent
  },
  {
    path: ':id',
    component: ConsultantFormComponent
  }
];
