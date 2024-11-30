import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { EvaluationService } from '../../../core/services/evaluation.service';
import { Evaluation } from '../../../core/models/evaluation.model';
import { ConfirmDialogComponent } from '@shared/components/confirm-dialog/confirm-dialog.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-evaluation-detail',
  templateUrl: './evaluation-detail.component.html',
  styleUrls: ['./evaluation-detail.component.css']
})
export class EvaluationDetailComponent implements OnInit {
  evaluation: Evaluation | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private evaluationService: EvaluationService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) {}

  async ngOnInit(): Promise<void> {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      await this.loadEvaluation(id);
    } else {
      this.error = 'ID de l\'évaluation non trouvé';
    }
  }

  async loadEvaluation(id: number): Promise<void> {
    try {
      this.loading = true;
      this.error = null;
      this.evaluation = await firstValueFrom(this.evaluationService.getEvaluationById(id));
    } catch (error) {
      this.error = 'Erreur lors du chargement de l\'évaluation';
      console.error('Error loading evaluation:', error);
    } finally {
      this.loading = false;
    }
  }

  async deleteEvaluation(): Promise<void> {
    if (!this.evaluation) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Supprimer l\'évaluation',
        message: 'Êtes-vous sûr de vouloir supprimer cette évaluation ?'
      }
    });

    try {
      const result = await dialogRef.afterClosed().toPromise();
      if (result) {
        await this.evaluationService.deleteEvaluation(this.evaluation.id);
        this.router.navigate(['/evaluations']);
      }
    } catch (error) {
      console.error('Error deleting evaluation:', error);
      this.error = 'Erreur lors de la suppression de l\'évaluation';
    }
  }

  editEvaluation(): void {
    if (this.evaluation) {
      this.router.navigate(['/evaluations/edit', this.evaluation.id]);
    }
  }

  goBack(): void {
    this.router.navigate(['/evaluations']);
  }

  getStatusClass(): string {
    if (!this.evaluation) return '';
    
    switch (this.evaluation.status) {
      case 'SCHEDULED':
        return 'bg-warning';
      case 'COMPLETED':
        return 'bg-success';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return '';
    }
  }
}
