import { Component, OnInit } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConsultantService } from '@core/services/consultant.service';
import { ProjectService } from '@core/services/project.service';
import { EvaluationService } from '@core/services/evaluation.service';
import { SkillService } from '@core/services/skill.service';
import { Consultant } from '@core/models/consultant.model';
import { Project, ProjectStatus } from '@core/models/project.model';
import { Evaluation } from '@core/models/evaluation.model';
import { Skill } from '@core/models/skill.model';

interface DashboardStats {
  totalConsultants: number;
  activeProjects: number;
  pendingEvaluations: number;
  skillCoverage: number;
}

interface ChartData {
  name: string;
  value: number;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalConsultants: 0,
    activeProjects: 0,
    pendingEvaluations: 0,
    skillCoverage: 0
  };

  // Propriétés pour le template
  totalConsultants = 0;
  totalProjects = 0;
  totalEvaluations = 0;
  averageRating = 0;
  projectStatusData: ChartData[] = [];
  consultantSkillsData: ChartData[] = [];
  evaluationTrendsData: ChartData[] = [];

  recentConsultants: Consultant[] = [];
  recentProjects: Project[] = [];
  recentEvaluations: Evaluation[] = [];
  topSkills: Skill[] = [];

  loading = {
    stats: false,
    consultants: false,
    projects: false,
    evaluations: false,
    skills: false
  };

  error: {
    stats?: string;
    consultants?: string;
    projects?: string;
    evaluations?: string;
    skills?: string;
  } = {};

  consultants$: Observable<Consultant[]>;
  projects$: Observable<Project[]>;
  evaluations$: Observable<Evaluation[]>;
  skills$: Observable<Skill[]>;

  constructor(
    private consultantService: ConsultantService,
    private projectService: ProjectService,
    private evaluationService: EvaluationService,
    private skillService: SkillService
  ) {
    this.consultants$ = this.consultantService.getConsultants();
    this.projects$ = this.projectService.getProjects();
    this.evaluations$ = this.evaluationService.getEvaluations();
    this.skills$ = this.skillService.getSkills();
  }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    this.loading.stats = true;
    forkJoin({
      consultants: this.consultants$,
      projects: this.projects$,
      evaluations: this.evaluations$,
      skills: this.skills$
    }).subscribe({
      next: (data) => {
        this.totalConsultants = data.consultants.length;
        this.totalProjects = data.projects.length;
        this.totalEvaluations = data.evaluations.length;

        this.projectStatusData = this.prepareProjectStatusData(data.projects);
        this.consultantSkillsData = this.prepareConsultantSkillsData(data.consultants);
        this.evaluationTrendsData = this.prepareEvaluationTrendsData(data.evaluations);

        this.recentProjects = this.getRecentProjects(data.projects);
        this.recentConsultants = this.prepareRecentConsultants(data.consultants);
        this.recentEvaluations = this.getRecentEvaluations(data.evaluations);
        this.topSkills = this.getTopSkills(data.skills);

        this.averageRating = this.calculateAverageRating(data.consultants);

        this.stats.skillCoverage = this.calculateSkillCoverage(this.recentConsultants, data.skills);

        this.loading.stats = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.loading.stats = false;
      }
    });
  }

  private prepareProjectStatusData(projects: Project[]): ChartData[] {
    const statusCounts = new Map<ProjectStatus, number>();
    Object.values(ProjectStatus).forEach(status => statusCounts.set(status, 0));

    projects.forEach(project => {
      const count = statusCounts.get(project.status) || 0;
      statusCounts.set(project.status, count + 1);
    });

    return Array.from(statusCounts).map(([name, value]) => ({ name, value }));
  }

  private getRecentProjects(projects: Project[]): Project[] {
    return projects
      .sort((a: Project, b: Project) => new Date(b.startDate).getTime() - new Date(a.startDate).getTime())
      .slice(0, 5);
  }

  private calculateAverageRating(consultants: Consultant[]): number {
    if (!consultants.length) return 0;
    const totalRating = consultants.reduce((sum, consultant) => {
      return sum + (consultant.rating || 0);
    }, 0);
    return Math.round((totalRating / consultants.length) * 10) / 10;
  }

  private prepareConsultantSkillsData(consultants: Consultant[]): ChartData[] {
    const skillCounts = new Map<string, number>();

    consultants.forEach(consultant => {
      consultant.skills?.forEach(skill => {
        const count = skillCounts.get(skill.name) || 0;
        skillCounts.set(skill.name, count + 1);
      });
    });

    return Array.from(skillCounts)
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 10);
  }

  private prepareRecentConsultants(consultants: Consultant[]): Consultant[] {
    return consultants
      .slice()
      .sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        return dateB - dateA;
      })
      .slice(0, 5);
  }

  private prepareEvaluationTrendsData(evaluations: Evaluation[]): ChartData[] {
    // Grouper les évaluations par mois et calculer la moyenne
    const monthlyAverages = new Map<string, { sum: number; count: number }>();

    evaluations.forEach(evaluation => {
      const date = new Date(evaluation.evaluationDate);
      const monthKey = `${date.getFullYear()}-${date.getMonth() + 1}`;
      const current = monthlyAverages.get(monthKey) || { sum: 0, count: 0 };
      monthlyAverages.set(monthKey, {
        sum: current.sum + (evaluation.score || 0),
        count: current.count + 1
      });
    });

    return Array.from(monthlyAverages)
      .map(([name, { sum, count }]) => ({
        name,
        value: Math.round((sum / count) * 10) / 10
      }))
      .sort((a, b) => a.name.localeCompare(b.name));
  }

  private getRecentEvaluations(evaluations: Evaluation[]): Evaluation[] {
    return evaluations
      .sort((a, b) => new Date(b.evaluationDate).getTime() - new Date(a.evaluationDate).getTime())
      .slice(0, 5);
  }

  private getTopSkills(skills: Skill[]): Skill[] {
    return skills.slice(0, 5);
  }

  private calculateSkillCoverage(consultants: Consultant[], skills: Skill[]): number {
    if (!skills.length) return 0;

    const totalSkills = skills.length;
    const coveredSkills = new Set<number>();

    consultants.forEach(consultant => {
      consultant.skills?.forEach(skill => {
        coveredSkills.add(skill.id);
      });
    });

    return Math.round((coveredSkills.size / totalSkills) * 100);
  }
}
