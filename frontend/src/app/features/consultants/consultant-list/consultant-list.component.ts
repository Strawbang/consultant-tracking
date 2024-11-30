import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { firstValueFrom } from 'rxjs';
import { ConsultantService } from '@core/services/consultant.service';
import { ProjectService } from '@core/services/project.service';
import { Consultant } from '@core/models/consultant.model';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '@shared/components/confirm-dialog/confirm-dialog.component';
import { Project } from '@core/models/project.model'; // Added import statement

@Component({
  selector: 'app-consultant-list',
  templateUrl: './consultant-list.component.html',
  styleUrls: ['./consultant-list.component.scss']
})
export class ConsultantListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['name', 'email', 'phone', 'rating', 'projectsCount', 'actions'];
  dataSource: MatTableDataSource<Consultant>;
  isLoading = false;
  consultants: Consultant[] = [];
  projectsCountMap = new Map<number, number>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private consultantService: ConsultantService,
    private projectService: ProjectService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<Consultant>();
  }

  ngOnInit(): void {
    this.loadConsultants();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  async loadConsultants(): Promise<void> {
    this.isLoading = true;
    try {
      this.consultants = await firstValueFrom(this.consultantService.getConsultants());
      this.dataSource.data = this.consultants;
      await this.loadProjectCounts();
    } catch (error) {
      console.error('Error loading consultants:', error);
    } finally {
      this.isLoading = false;
    }
  }

  private async loadProjectCounts(): Promise<void> {
    try {
      await Promise.all(
        this.consultants.map(async consultant => {
          try {
            const projects = await firstValueFrom(this.projectService.getConsultantProjects(consultant.id!));
            this.projectsCountMap.set(consultant.id!, (projects as Project[]).length); // Modified line
          } catch (error) {
            console.error(`Error loading projects for consultant ${consultant.id}:`, error);
            this.projectsCountMap.set(consultant.id!, 0);
          }
        })
      );
    } catch (error) {
      console.error('Error loading project counts:', error);
    }
  }

  getProjectsCount(consultant: Consultant): number {
    return this.projectsCountMap.get(consultant.id!) || 0;
  }

  addConsultant(): void {
    this.router.navigate(['/consultants/new']);
  }

  editConsultant(id: number): void {
    this.router.navigate(['/consultants/edit', id]);
  }

  async deleteConsultant(id: number): Promise<void> {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Consultant',
        message: 'Are you sure you want to delete this consultant?'
      }
    });

    const result = await firstValueFrom(dialogRef.afterClosed());
    if (result) {
      this.isLoading = true;
      try {
        await firstValueFrom(this.consultantService.deleteConsultant(id));
        await this.loadConsultants();
      } catch (error) {
        console.error('Error deleting consultant:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
