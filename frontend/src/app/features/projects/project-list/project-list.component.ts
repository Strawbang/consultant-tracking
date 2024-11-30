import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { Project } from '../../../core/models/project.model';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'client', 'startDate', 'endDate', 'status', 'consultants', 'actions'];
  dataSource!: MatTableDataSource<Project>;
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private projectService: ProjectService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        this.dataSource = new MatTableDataSource(projects);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  getConsultantCount(project: Project): number {
    return project.consultantIds?.length || 0;
  }

  addProject(): void {
    this.router.navigate(['/projects/new']);
  }

  editProject(id: number): void {
    this.router.navigate(['/projects', id]);
  }

  async deleteProject(id: number): Promise<void> {
    if (confirm('Are you sure you want to delete this project?')) {
      try {
        await this.projectService.deleteProject(id);
        await this.loadProjects();
      } catch (error: unknown) {
        console.error('Error deleting project:', error);
      }
    }
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'active':
        return 'accent';
      case 'completed':
        return 'primary';
      case 'on hold':
        return 'warn';
      default:
        return '';
    }
  }
}
