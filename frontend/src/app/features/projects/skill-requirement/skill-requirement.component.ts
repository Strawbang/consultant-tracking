import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { Skill } from '../../../core/models/skill.model';
import { ProjectSkill } from '../../../core/models/project.model';
import { SkillService } from '../../../core/services/skill.service';
import { ProjectService } from '../../../core/services/project.service';

@Component({
  selector: 'app-skill-requirement',
  templateUrl: './skill-requirement.component.html',
  styleUrls: ['./skill-requirement.component.scss']
})
export class SkillRequirementComponent {
  skillForm: FormGroup;
  skills$: Observable<Skill[]>;
  filteredSkills$: Observable<Skill[]>;
  skillLevels = [1, 2, 3, 4, 5];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SkillRequirementComponent>,
    private skillService: SkillService,
    private projectService: ProjectService,
    @Inject(MAT_DIALOG_DATA) public data: { projectId: number }
  ) {
    this.createForm();
    this.loadSkills();
  }

  private createForm(): void {
    this.skillForm = this.fb.group({
      skillId: ['', Validators.required],
      requiredLevel: ['', [Validators.required, Validators.min(1), Validators.max(5)]],
      priority: ['', Validators.required],
      description: ['']
    });
  }

  private loadSkills(): void {
    this.skills$ = this.skillService.getAllSkills();
    
    this.filteredSkills$ = this.skillForm.get('skillId').valueChanges.pipe(
      startWith(''),
      map(value => this._filterSkills(value))
    );
  }

  private _filterSkills(value: string): Skill[] {
    if (typeof value !== 'string') return [];
    
    const filterValue = value.toLowerCase();
    return this.skills$.pipe(
      map(skills => skills.filter(skill => 
        skill.name.toLowerCase().includes(filterValue) ||
        skill.category.toLowerCase().includes(filterValue)
      ))
    );
  }

  displaySkill(skill: Skill): string {
    return skill ? skill.name : '';
  }

  onSubmit(): void {
    if (this.skillForm.valid) {
      const requirement: ProjectSkill = {
        projectId: this.data.projectId,
        ...this.skillForm.value
      };

      this.projectService.addProjectSkill(requirement).subscribe(
        result => {
          this.dialogRef.close(result);
        },
        error => {
          console.error('Error adding skill requirement:', error);
          // TODO: Add proper error handling
        }
      );
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
