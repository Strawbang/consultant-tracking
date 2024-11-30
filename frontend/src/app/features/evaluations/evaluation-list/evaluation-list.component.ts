import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ThemePalette } from '@angular/material/core';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EvaluationService } from '../../../core/services/evaluation.service';
import { ConsultantService } from '../../../core/services/consultant.service';
import { Evaluation } from '../../../core/models/evaluation.model';
import { Consultant } from '../../../core/models/consultant.model';

@Component({
  selector: 'app-evaluation-list',
  templateUrl: './evaluation-list.component.html',
  styleUrls: ['./evaluation-list.component.scss']
})
export class EvaluationListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'type', 'consultantName', 'evaluationDate', 'score', 'status', 'actions'];
  dataSource: MatTableDataSource<Evaluation>;
  evaluationTypes: string[] = [];
  consultants: Map<number, Consultant> = new Map();
  selectedType: string | null = null;
  startDate: Date | null = null;
  endDate: Date | null = null;
  isLoading = false;
  error: string | null = null;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private evaluationService: EvaluationService,
    private consultantService: ConsultantService,
    private dialog: MatDialog,
    private router: Router
  ) {
    this.dataSource = new MatTableDataSource<Evaluation>();
  }

  ngOnInit(): void {
    this.loadEvaluations();
    this.loadConsultants();
    this.loadEvaluationTypes();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.createFilter();
  }

  loadEvaluations(): void {
    this.isLoading = true;
    this.error = null;
    
    this.evaluationService.getEvaluations().subscribe({
      next: (evaluations: Evaluation[]) => {
        this.dataSource.data = evaluations;
        this.isLoading = false;
      },
      error: (error: Error) => {
        this.error = 'Erreur lors du chargement des évaluations: ' + error.message;
        this.isLoading = false;
      }
    });
  }

  loadConsultants(): void {
    this.consultantService.getConsultants().subscribe({
      next: (consultants: Consultant[]) => {
        consultants.forEach((consultant: Consultant) => {
          this.consultants.set(consultant.id, consultant);
        });
      },
      error: (error: Error) => {
        console.error('Erreur lors du chargement des consultants:', error);
      }
    });
  }

  loadEvaluationTypes(): void {
    // Assuming this is a static list for now
    this.evaluationTypes = ['TECHNICAL', 'SOFT_SKILLS', 'PROJECT', 'ANNUAL'];
  }

  getConsultantName(consultantId: number): string {
    const consultant = this.consultants.get(consultantId);
    return consultant ? `${consultant.firstName} ${consultant.lastName}` : 'N/A';
  }

  getStatusColor(status: string): ThemePalette {
    switch (status) {
      case 'SCHEDULED':
        return 'accent';
      case 'COMPLETED':
        return 'primary';
      case 'CANCELLED':
        return 'warn';
      default:
        return undefined;
    }
  }

  createFilter(): (data: Evaluation, filter: string) => boolean {
    return (data: Evaluation, filter: string): boolean => {
      if (!this.selectedType && !this.startDate && !this.endDate) {
        return true;
      }

      let matchesType = true;
      let matchesDateRange = true;

      if (this.selectedType) {
        matchesType = data.type === this.selectedType;
      }

      if (this.startDate || this.endDate) {
        const evaluationDate = new Date(data.evaluationDate);
        
        if (this.startDate) {
          matchesDateRange = matchesDateRange && evaluationDate >= this.startDate;
        }
        
        if (this.endDate) {
          matchesDateRange = matchesDateRange && evaluationDate <= this.endDate;
        }
      }

      return matchesType && matchesDateRange;
    };
  }

  applyFilter(): void {
    this.dataSource.filter = 'trigger';  // Any non-empty string will trigger the filter
  }

  createEvaluation(): void {
    this.router.navigate(['/evaluations/new']);
  }

  viewEvaluation(id: number): void {
    this.router.navigate(['/evaluations', id]);
  }

  editEvaluation(id: number): void {
    this.router.navigate(['/evaluations', id, 'edit']);
  }

  onDeleteEvaluation(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirmation de suppression',
        message: 'Êtes-vous sûr de vouloir supprimer cette évaluation ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteEvaluation(id);
      }
    });
  }

  private deleteEvaluation(id: number): void {
    this.isLoading = true;
    this.error = null;

    this.evaluationService.deleteEvaluation(id).subscribe({
      next: () => {
        this.loadEvaluations();
      },
      error: (error: Error) => {
        this.error = 'Erreur lors de la suppression de l\'évaluation: ' + error.message;
        this.isLoading = false;
      }
    });
  }
}
