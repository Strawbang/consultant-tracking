import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EvaluationListComponent } from './evaluation-list/evaluation-list.component';
import { EvaluationFormComponent } from './evaluation-form/evaluation-form.component';
import { EvaluationDetailComponent } from './evaluation-detail/evaluation-detail.component';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: '', component: EvaluationListComponent },
      { path: 'new', component: EvaluationFormComponent },
      { path: 'edit/:id', component: EvaluationFormComponent },
      { path: ':id', component: EvaluationDetailComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EvaluationsRoutingModule { }
