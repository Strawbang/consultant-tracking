import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, combineLatest } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EvaluationService } from '../../../core/services/evaluation.service';
import { ConsultantService } from '../../../core/services/consultant.service';
import { ProjectService } from '../../../core/services/project.service';
import { Evaluation } from '../../../core/models/evaluation.model';
import { Consultant } from '../../../core/models/consultant.model';
import { Project } from '../../../core/models/project.model';

interface EvaluationStats {
  overallPerformance: number;
  technicalSkills: number;
  softSkills: number;
  projectContribution: number;
  teamwork: number;
}

interface ConsultantStats {
  consultant: Consultant;
  currentProject?: Project;
  evaluations: Evaluation[];
  averageStats: EvaluationStats;
  skillProgress: any[];
  evaluationHistory: {
    date: Date;
    score: number;
  }[];
}

@Component({
  selector: 'app-evaluation-stats',
  templateUrl: './evaluation-stats.component.html',
  styleUrls: ['./evaluation-stats.component.scss']
})
export class EvaluationStatsComponent implements OnInit {
  consultantStats$: Observable<ConsultantStats[]>;
  selectedConsultant: ConsultantStats | null = null;
  
  // Chart options
  radarChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      r: {
        min: 0,
        max: 5,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  lineChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        min: 0,
        max: 5,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  constructor(
    private route: ActivatedRoute,
    private evaluationService: EvaluationService,
    private consultantService: ConsultantService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadConsultantStats();
  }

  private loadConsultantStats(): void {
    this.consultantStats$ = this.consultantService.getAllConsultants().pipe(
      switchMap(consultants => {
        const consultantStats$ = consultants.map(consultant =>
          combineLatest([
            this.evaluationService.getConsultantEvaluations(consultant.id),
            this.evaluationService.getEvaluationStatistics(consultant.id),
            this.projectService.getCurrentProject(consultant.id)
          ]).pipe(
            map(([evaluations, stats, currentProject]) => ({
              consultant,
              currentProject,
              evaluations,
              averageStats: this.calculateAverageStats(evaluations),
              skillProgress: stats.skillProgress || [],
              evaluationHistory: this.processEvaluationHistory(evaluations)
            }))
          )
        );
        return combineLatest(consultantStats$);
      })
    );
  }

  private calculateAverageStats(evaluations: Evaluation[]): EvaluationStats {
    if (!evaluations.length) {
      return {
        overallPerformance: 0,
        technicalSkills: 0,
        softSkills: 0,
        projectContribution: 0,
        teamwork: 0
      };
    }

    return {
      overallPerformance: this.calculateAverage(evaluations.map(e => e.overallPerformance)),
      technicalSkills: this.calculateAverage(evaluations.map(e => e.technicalSkills)),
      softSkills: this.calculateAverage(evaluations.map(e => e.softSkills)),
      projectContribution: this.calculateAverage(evaluations.map(e => e.projectContribution)),
      teamwork: this.calculateAverage(evaluations.map(e => e.teamwork))
    };
  }

  private calculateAverage(numbers: number[]): number {
    return numbers.reduce((a, b) => a + b, 0) / numbers.length;
  }

  private processEvaluationHistory(evaluations: Evaluation[]): { date: Date; score: number }[] {
    return evaluations
      .map(e => ({
        date: new Date(e.evaluationDate),
        score: e.overallPerformance
      }))
      .sort((a, b) => a.date.getTime() - b.date.getTime());
  }

  selectConsultant(consultant: ConsultantStats): void {
    this.selectedConsultant = consultant;
  }

  getRadarChartData(stats: EvaluationStats): any {
    return {
      labels: ['Overall', 'Technical', 'Soft Skills', 'Contribution', 'Teamwork'],
      datasets: [{
        label: 'Performance Metrics',
        data: [
          stats.overallPerformance,
          stats.technicalSkills,
          stats.softSkills,
          stats.projectContribution,
          stats.teamwork
        ],
        fill: true,
        backgroundColor: 'rgba(75,192,192,0.2)',
        borderColor: 'rgb(75,192,192)',
        pointBackgroundColor: 'rgb(75,192,192)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgb(75,192,192)'
      }]
    };
  }

  getLineChartData(history: { date: Date; score: number }[]): any {
    return {
      labels: history.map(h => h.date.toLocaleDateString()),
      datasets: [{
        label: 'Performance Over Time',
        data: history.map(h => h.score),
        fill: false,
        borderColor: 'rgb(75,192,192)',
        tension: 0.1
      }]
    };
  }
}
