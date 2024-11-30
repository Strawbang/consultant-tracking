import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Observable, map, startWith } from 'rxjs';

import { ConsultantService } from '../../../core/services/consultant.service';
import { ProjectService } from '../../../core/services/project.service';
import { Consultant } from '../../../core/models/consultant.model';
import { ConsultantProject } from '../../../core/models/project.model';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-consultant-assignment',
  templateUrl: './consultant-assignment.component.html',
  styleUrls: ['./consultant-assignment.component.scss'],
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
    MatNativeDateModule
  ]
})
export class ConsultantAssignmentComponent implements OnInit {
  assignmentForm: FormGroup;
  consultants$: Observable<Consultant[]>;
  filteredConsultants$: Observable<Consultant[]>;
  roles: string[] = ['Developer', 'Tech Lead', 'Project Manager', 'Business Analyst', 'QA Engineer'];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ConsultantAssignmentComponent>,
    private consultantService: ConsultantService,
    private projectService: ProjectService,
    @Inject(MAT_DIALOG_DATA) public data: { projectId: number }
  ) {
    this.assignmentForm = this.fb.group({
      consultantId: ['', Validators.required],
      role: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: [''],
      allocation: [100, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    this.consultants$ = this.consultantService.getAllConsultants();
    this.filteredConsultants$ = this.consultants$;
  }

  ngOnInit(): void {
    this.setupConsultantFilter();
  }

  private setupConsultantFilter(): void {
    const consultantControl = this.assignmentForm.get('consultantId');
    if (consultantControl) {
      this.filteredConsultants$ = consultantControl.valueChanges.pipe(
        startWith(''),
        map(value => this._filterConsultants(value))
      );
    }
  }

  private _filterConsultants(value: string): Consultant[] {
    if (typeof value === 'string') {
      const filterValue = value.toLowerCase();
      return this.consultants$.pipe(
        map(consultants => consultants.filter(consultant => 
          `${consultant.firstName} ${consultant.lastName}`.toLowerCase().includes(filterValue)
        ))
      );
    }
    return this.consultants$;
  }

  onSubmit(): void {
    if (this.assignmentForm.valid) {
      const assignment: ConsultantProject = {
        projectId: this.data.projectId,
        consultantId: this.assignmentForm.value.consultantId,
        role: this.assignmentForm.value.role,
        startDate: this.assignmentForm.value.startDate,
        endDate: this.assignmentForm.value.endDate,
        allocation: this.assignmentForm.value.allocation
      };

      this.projectService.addProjectConsultant(
        this.data.projectId,
        assignment.consultantId,
        assignment.role,
        assignment.startDate,
        assignment.allocation
      ).subscribe({
        next: (result) => {
          this.dialogRef.close(result);
        },
        error: (error) => {
          console.error('Error assigning consultant:', error);
          // Handle error appropriately
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  displayConsultant(consultant: Consultant): string {
    return consultant ? `${consultant.firstName} ${consultant.lastName}` : '';
  }
}
