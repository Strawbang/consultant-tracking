import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SkillListComponent } from './skill-list/skill-list.component';
import { SkillFormComponent } from './skill-form/skill-form.component';
import { SkillTreeComponent } from './skill-tree/skill-tree.component';

const routes: Routes = [
  { path: '', component: SkillListComponent },
  { path: 'new', component: SkillFormComponent },
  { path: 'edit/:id', component: SkillFormComponent },
  { path: 'tree', component: SkillTreeComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SkillsRoutingModule { }
