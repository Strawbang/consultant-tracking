import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConsultantListComponent } from './consultant-list/consultant-list.component';
import { ConsultantFormComponent } from './consultant-form/consultant-form.component';

const routes: Routes = [
  { path: '', component: ConsultantListComponent },
  { path: 'new', component: ConsultantFormComponent },
  { path: 'edit/:id', component: ConsultantFormComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConsultantsRoutingModule { }
