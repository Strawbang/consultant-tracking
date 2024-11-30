import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { EvaluationService } from '@core/services/evaluation.service';
import { ConsultantService } from '@core/services/consultant.service';
import { Evaluation } from '@core/models/evaluation.model';
import { Consultant } from '@core/models/consultant.model';

@Component({
  selector: 'app-evaluation-form',
  templateUrl: './evaluation-form.component.html',
  styleUrls: ['./evaluation-form.component.scss']
})
export class EvaluationFormComponent implements OnInit {
  evaluationForm: FormGroup;
  isEditMode = false;
  evaluationId?: number;
  consultants: Consultant[] = [];
  isLoading = false;
  error: string | null = null;
  submitted = false;

  evaluationTypes: string[] = ['TYPE1', 'TYPE2', 'TYPE3'];

  get f() {
    return this.evaluationForm.controls;
  }

  constructor(
    private fb: FormBuilder,
    private evaluationService: EvaluationService,
    private consultantService: ConsultantService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.evaluationForm = this.fb.group({
      type: ['', Validators.required],
      consultantId: [null, Validators.required],
      evaluationDate: [null, Validators.required],
      score: [null, [Validators.required, Validators.min(0), Validators.max(5)]],
      comments: [''],
      status: ['DRAFT', Validators.required]
    });
  }

  async ngOnInit() {
    await this.loadConsultants();

    const params = this.route.snapshot.params;
    if (params['id']) {
      this.isEditMode = true;
      this.evaluationId = +params['id'];
      await this.loadEvaluation();
    }
  }

  private async loadEvaluation() {
    if (this.evaluationId) {
      this.isLoading = true;
      try {
        const evaluation = await firstValueFrom(this.evaluationService.getEvaluationById(this.evaluationId));
        this.evaluationForm.patchValue({
          type: evaluation.type,
          consultantId: evaluation.consultantId,
          evaluationDate: evaluation.evaluationDate,
          score: evaluation.score,
          comments: evaluation.comments,
          status: evaluation.status
        });
      } catch (error) {
        console.error('Error loading evaluation:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  private async loadConsultants() {
    this.isLoading = true;
    try {
      const consultants = await firstValueFrom(this.consultantService.getConsultants());
      this.consultants = consultants;
    } catch (error) {
      console.error('Error loading consultants:', error);
    } finally {
      this.isLoading = false;
    }
  }

  async onSubmit() {
    if (this.evaluationForm.valid) {
      this.isLoading = true;
      try {
        const evaluationData = this.evaluationForm.value;
        if (this.isEditMode && this.evaluationId) {
          await firstValueFrom(this.evaluationService.updateEvaluation(this.evaluationId, evaluationData));
        } else {
          await firstValueFrom(this.evaluationService.createEvaluation(evaluationData));
        }
        this.router.navigate(['/evaluations']);
      } catch (error) {
        console.error('Error saving evaluation:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  onCancel() {
    this.router.navigate(['/evaluations']);
  }
}
