import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Consultant } from '../../../core/models/consultant.model';
import { Project, ProjectStatus } from '../../../core/models/project.model';
import { ConsultantService } from '../../../core/services/consultant.service';
import { ProjectService } from '../../../core/services/project.service';

@Component({
  selector: 'app-project-form',
  templateUrl: './project-form.component.html',
  styleUrls: ['./project-form.component.scss']
})
export class ProjectFormComponent implements OnInit {
  projectForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  error: string | null = null;
  consultants: Consultant[] = [];
  projectStatuses = Object.values(ProjectStatus);

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private consultantService: ConsultantService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.projectForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      status: [ProjectStatus.DRAFT, Validators.required],
      startDate: [new Date(), Validators.required],
      endDate: [null],
      consultantIds: [[]]
    });
  }

  async ngOnInit(): Promise<void> {
    try {
      this.isLoading = true;
      await this.loadConsultants();

      const projectId = this.route.snapshot.params['id'];
      if (projectId) {
        this.isEditMode = true;
        await this.loadProject(projectId);
      }
    } catch (error) {
      console.error('Error initializing project form:', error);
      this.error = 'Une erreur est survenue lors de l\'initialisation du formulaire.';
    } finally {
      this.isLoading = false;
    }
  }

  private async loadConsultants(): Promise<void> {
    try {
      this.consultants = await firstValueFrom(this.consultantService.getConsultants());
    } catch (error) {
      console.error('Error loading consultants:', error);
      this.error = 'Une erreur est survenue lors du chargement des consultants.';
      throw error;
    }
  }

  private async loadProject(id: number): Promise<void> {
    try {
      const project = await firstValueFrom(this.projectService.getProject(id));
      this.projectForm.patchValue({
        name: project.name,
        description: project.description,
        status: project.status,
        startDate: new Date(project.startDate),
        endDate: project.endDate ? new Date(project.endDate) : null,
        consultantIds: project.consultantIds
      });
    } catch (error) {
      console.error('Error loading project:', error);
      this.error = 'Une erreur est survenue lors du chargement du projet.';
      throw error;
    }
  }

  async onSubmit(): Promise<void> {
    if (this.projectForm.invalid) {
      return;
    }

    try {
      this.isLoading = true;
      this.error = null;

      const projectId = this.isEditMode ? this.route.snapshot.params['id'] : undefined;
      const projectData = {
        ...this.projectForm.value,
        id: projectId
      };

      if (this.isEditMode && projectId) {
        await firstValueFrom(this.projectService.updateProject(projectId, projectData));
      } else {
        await firstValueFrom(this.projectService.createProject(projectData));
      }

      this.router.navigate(['/projects']);
    } catch (error) {
      console.error('Error saving project:', error);
      this.error = 'Une erreur est survenue lors de l\'enregistrement du projet.';
    } finally {
      this.isLoading = false;
    }
  }

  onCancel(): void {
    this.router.navigate(['/projects']);
  }
}
