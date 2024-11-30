import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { Project, ProjectStatus, ConsultantProject, ProjectSkill } from '../../../core/models/project.model';
import { ProjectService } from '../../../core/services/project.service';
import { ConsultantService } from '../../../core/services/consultant.service';
import { ProjectFormComponent } from '../project-form/project-form.component';
import { ConsultantAssignmentComponent } from '../consultant-assignment/consultant-assignment.component';
import { SkillRequirementComponent } from '../skill-requirement/skill-requirement.component';
import { ConsultantEvaluationComponent } from '../consultant-evaluation/consultant-evaluation.component';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  project$: Observable<Project>;
  consultants$: Observable<ConsultantProject[]>;
  skills$: Observable<ProjectSkill[]>;
  projectId: number;
  ProjectStatus = ProjectStatus;

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private consultantService: ConsultantService,
    private dialog: MatDialog
  ) {
    this.projectId = +this.route.snapshot.params['id'];
    this.loadProject();
  }

  ngOnInit(): void {}

  private loadProject(): void {
    this.project$ = this.projectService.getProjectById(this.projectId);
    this.consultants$ = this.projectService.getProjectConsultants(this.projectId);
    this.skills$ = this.projectService.getProjectSkills(this.projectId);
  }

  editProject(project: Project): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '600px',
      data: project
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProject();
      }
    });
  }

  addConsultant(): void {
    const dialogRef = this.dialog.open(ConsultantAssignmentComponent, {
      width: '600px',
      data: { projectId: this.projectId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProject();
      }
    });
  }

  removeConsultant(consultantId: number): void {
    if (confirm('Are you sure you want to remove this consultant from the project?')) {
      this.projectService.removeProjectConsultant(this.projectId, consultantId)
        .subscribe(() => {
          this.loadProject();
        });
    }
  }

  addSkill(): void {
    const dialogRef = this.dialog.open(SkillRequirementComponent, {
      width: '600px',
      data: { projectId: this.projectId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProject();
      }
    });
  }

  removeSkill(skillId: number): void {
    if (confirm('Are you sure you want to remove this skill requirement?')) {
      this.projectService.removeProjectSkill(this.projectId, skillId)
        .subscribe(() => {
          this.loadProject();
        });
    }
  }

  evaluateConsultant(consultant: ConsultantProject): void {
    const dialogRef = this.dialog.open(ConsultantEvaluationComponent, {
      width: '800px',
      data: {
        projectId: this.projectId,
        consultantId: consultant.consultantId,
        consultantName: `${consultant.consultant?.firstName} ${consultant.consultant?.lastName}`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProject();
      }
    });
  }

  getStatusColor(status: ProjectStatus): string {
    switch (status) {
      case ProjectStatus.PLANNED:
        return 'accent';
      case ProjectStatus.IN_PROGRESS:
        return 'primary';
      case ProjectStatus.COMPLETED:
        return 'success';
      case ProjectStatus.CANCELLED:
        return 'warn';
      case ProjectStatus.ON_HOLD:
        return 'warn';
      default:
        return '';
    }
  }
}
