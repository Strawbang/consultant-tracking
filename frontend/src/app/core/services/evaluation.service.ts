import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evaluation, EvaluationCreate, EvaluationUpdate, EvaluationFilter, EvaluationType } from '../models/evaluation.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EvaluationService {
  private apiUrl = `${environment.apiUrl}/evaluations`;

  constructor(private http: HttpClient) {}

  // Méthodes CRUD de base
  getEvaluations(filters?: EvaluationFilter): Observable<Evaluation[]> {
    let params = new HttpParams();
    if (filters?.consultantId) {
      params = params.set('consultantId', filters.consultantId.toString());
    }
    if (filters?.projectId) {
      params = params.set('projectId', filters.projectId.toString());
    }
    if (filters?.fromDate) {
      params = params.set('fromDate', filters.fromDate.toISOString());
    }
    if (filters?.toDate) {
      params = params.set('toDate', filters.toDate.toISOString());
    }
    return this.http.get<Evaluation[]>(this.apiUrl, { params });
  }

  getEvaluationById(id: number): Observable<Evaluation> {
    return this.http.get<Evaluation>(`${this.apiUrl}/${id}`);
  }

  createEvaluation(evaluation: EvaluationCreate): Observable<Evaluation> {
    return this.http.post<Evaluation>(this.apiUrl, evaluation);
  }

  updateEvaluation(id: number, evaluation: EvaluationUpdate): Observable<Evaluation> {
    return this.http.put<Evaluation>(`${this.apiUrl}/${id}`, evaluation);
  }

  deleteEvaluation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Méthodes de filtrage
  getEvaluationsByFilter(filter: EvaluationFilter): Observable<Evaluation[]> {
    return this.getEvaluations(filter);
  }

  // Méthodes de convenance
  getEvaluationsByType(type: EvaluationType): Observable<Evaluation[]> {
    return this.getEvaluationsByFilter({ type });
  }

  getEvaluationsByDateRange(startDate: Date, endDate: Date): Observable<Evaluation[]> {
    return this.getEvaluationsByFilter({ startDate, endDate });
  }

  // Méthodes spécifiques aux consultants
  getConsultantEvaluations(consultantId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${environment.apiUrl}/consultants/${consultantId}/evaluations`);
  }

  // Méthodes spécifiques aux projets
  getProjectEvaluations(projectId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${environment.apiUrl}/projects/${projectId}/evaluations`);
  }

  // Méthodes de workflow
  submitEvaluation(id: number): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.apiUrl}/${id}/submit`, {});
  }

  approveEvaluation(id: number): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectEvaluation(id: number, reason: string): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.apiUrl}/${id}/reject`, { reason });
  }

  // Méthodes utilitaires
  searchEvaluations(query: string): Observable<Evaluation[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Evaluation[]>(`${this.apiUrl}/search`, { params });
  }

  getPendingEvaluations(): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${this.apiUrl}/pending`);
  }

  getCompletedEvaluations(): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${this.apiUrl}/completed`);
  }

  getConsultantProjectEvaluation(projectId: number, consultantId: number): Observable<Evaluation> {
    return this.http.get<Evaluation>(`${this.apiUrl}/project/${projectId}/consultant/${consultantId}`);
  }

  saveEvaluation(evaluation: Evaluation): Observable<Evaluation> {
    if (evaluation.id) {
      return this.http.put<Evaluation>(`${this.apiUrl}/${evaluation.id}`, evaluation);
    }
    return this.http.post<Evaluation>(this.apiUrl, evaluation);
  }

  getEvaluationHistory(consultantId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${this.apiUrl}/consultant/${consultantId}/history`);
  }

  getEvaluationStatistics(consultantId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/consultant/${consultantId}/statistics`);
  }
}
