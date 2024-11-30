import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ThemePalette } from '@angular/material/core';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { SkillService } from '../../../core/services/skill.service';
import { Skill } from '../../../core/models/skill.model';

@Component({
  selector: 'app-skill-list',
  templateUrl: './skill-list.component.html',
  styleUrls: ['./skill-list.component.scss']
})
export class SkillListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'description', 'category', 'level', 'isRequired', 'actions'];
  dataSource: MatTableDataSource<Skill>;
  categories: string[] = ['TECHNICAL', 'SOFT_SKILLS', 'PROJECT', 'MANAGEMENT'];
  filteredSkills: Skill[] = [];
  
  searchTerm: string = '';
  selectedCategory: string | null = null;
  selectedLevel: number | null = null;
  showRequiredOnly: boolean = false;
  loading = false;
  error: string | null = null;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private skillService: SkillService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.dataSource = new MatTableDataSource<Skill>();
  }

  ngOnInit(): void {
    this.loadSkills();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.createFilter();
  }

  loadSkills(): void {
    this.loading = true;
    this.error = null;
    
    this.skillService.getSkills().subscribe({
      next: (skills: Skill[]) => {
        this.dataSource.data = skills;
        this.filteredSkills = skills;
        this.loading = false;
      },
      error: (error: Error) => {
        this.error = 'Erreur lors du chargement des compétences: ' + error.message;
        this.loading = false;
      }
    });
  }

  createFilter(): (data: Skill, filter: string) => boolean {
    return (data: Skill, filter: string): boolean => {
      if (!this.searchTerm && !this.selectedCategory && !this.selectedLevel && !this.showRequiredOnly) {
        return true;
      }

      const matchesSearch = !this.searchTerm || 
        (data.name?.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
         data.description?.toLowerCase().includes(this.searchTerm.toLowerCase())) || false;

      const matchesCategory = !this.selectedCategory || 
        data.category === this.selectedCategory;

      const matchesLevel = !this.selectedLevel || 
        (typeof data.level === 'number' && data.level >= (this.selectedLevel || 0));

      const matchesRequired = !this.showRequiredOnly || 
        data.isRequired === true;

      return matchesSearch && matchesCategory && matchesLevel && matchesRequired;
    };
  }

  applyFilter(): void {
    this.dataSource.filter = 'trigger';  // Any non-empty string will trigger the filter
    this.filteredSkills = this.dataSource.filteredData;

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  toggleRequired(): void {
    this.applyFilter();
  }

  getCategoryColor(category: string): ThemePalette {
    switch (category) {
      case 'TECHNICAL':
        return 'primary';
      case 'SOFT_SKILLS':
        return 'accent';
      case 'PROJECT':
        return 'warn';
      default:
        return undefined;
    }
  }

  getLevelColor(level: number): ThemePalette {
    if (level >= 4) return 'primary';
    if (level >= 2) return 'accent';
    return 'warn';
  }

  createSkill(): void {
    this.router.navigate(['/skills/new']);
  }

  editSkill(id: number): void {
    this.router.navigate(['/skills/edit', id]);
  }

  viewSkillTree(): void {
    this.router.navigate(['/skills/tree']);
  }

  deleteSkill(skill: Skill): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirmation de suppression',
        message: `Êtes-vous sûr de vouloir supprimer la compétence "${skill.name}" ?`,
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.skillService.deleteSkill(skill.id).subscribe({
          next: () => {
            this.loadSkills();
          },
          error: (error: Error) => {
            this.error = 'Erreur lors de la suppression de la compétence: ' + error.message;
            this.loading = false;
          }
        });
      }
    });
  }
}
