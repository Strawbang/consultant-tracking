import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { Observable } from 'rxjs';

import { EvaluationService } from '../../../core/services/evaluation.service';
import { ProjectService } from '../../../core/services/project.service';
import { Evaluation, EvaluationType, EvaluationStatus } from '../../../core/models/evaluation.model';

@Component({
  selector: 'app-consultant-evaluation',
  templateUrl: './consultant-evaluation.component.html',
  styleUrls: ['./consultant-evaluation.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatChipsModule,
    MatListModule
  ]
})
export class ConsultantEvaluationComponent implements OnInit {
  @Input() projectId!: number;
  @Input() consultantId!: number;

  evaluationForm: FormGroup = this.fb.group({
    type: [EvaluationType.PROJECT, Validators.required],
    evaluationDate: [new Date(), Validators.required],
    status: [EvaluationStatus.DRAFT, Validators.required],
    overallPerformance: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
    technicalSkills: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
    softSkills: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
    projectContribution: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
    teamwork: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
    comments: ['']
  });

  existingEvaluation$: Observable<Evaluation>;
  skillRatings$: Observable<any[]>;

  evaluationTypes = Object.values(EvaluationType);
  evaluationStatuses = Object.values(EvaluationStatus);

  constructor(
    private fb: FormBuilder,
    private evaluationService: EvaluationService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadExistingEvaluation();
    this.loadSkillRatings();
  }

  private loadExistingEvaluation(): void {
    this.existingEvaluation$ = this.evaluationService.getEvaluationByProjectAndConsultant(
      this.projectId,
      this.consultantId
    );

    this.existingEvaluation$.subscribe(evaluation => {
      if (evaluation) {
        this.evaluationForm.patchValue({
          type: evaluation.type,
          evaluationDate: evaluation.evaluationDate,
          status: evaluation.status,
          overallPerformance: evaluation.overallPerformance,
          technicalSkills: evaluation.technicalSkills,
          softSkills: evaluation.softSkills,
          projectContribution: evaluation.projectContribution,
          teamwork: evaluation.teamwork,
          comments: evaluation.comments
        });
      }
    });
  }

  private loadSkillRatings(): void {
    this.skillRatings$ = this.projectService.getConsultantSkillRatings(
      this.projectId,
      this.consultantId
    );
  }

  onSubmit(): void {
    if (this.evaluationForm.valid) {
      const evaluationData = {
        ...this.evaluationForm.value,
        projectId: this.projectId,
        consultantId: this.consultantId,
        evaluatorId: 1 // À remplacer par l'ID de l'utilisateur connecté
      };

      this.evaluationService.createEvaluation(evaluationData).subscribe({
        next: () => {
          this.evaluationForm.reset();
          // Émettre un événement pour informer le composant parent
        },
        error: (error) => {
          console.error('Erreur lors de la création de l\'évaluation:', error);
        }
      });
    }
  }
}
