import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consultant, ConsultantCreate, ConsultantUpdate } from '../models/consultant.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConsultantService {
  private apiUrl = `${environment.apiUrl}/consultants`;

  constructor(private http: HttpClient) {}

  getConsultants(): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(this.apiUrl);
  }

  getConsultant(id: number): Observable<Consultant> {
    return this.http.get<Consultant>(`${this.apiUrl}/${id}`);
  }

  getConsultantById(id: number): Observable<Consultant> {
    return this.http.get<Consultant>(`${this.apiUrl}/${id}`);
  }

  createConsultant(consultant: ConsultantCreate): Observable<Consultant> {
    return this.http.post<Consultant>(this.apiUrl, consultant);
  }

  updateConsultant(id: number, consultant: ConsultantUpdate): Observable<Consultant> {
    return this.http.put<Consultant>(`${this.apiUrl}/${id}`, consultant);
  }

  deleteConsultant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchConsultants(query: string): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(`${this.apiUrl}/search`, {
      params: { query }
    });
  }

  getConsultantsBySkill(skillId: number): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(`${this.apiUrl}/by-skill/${skillId}`);
  }

  getConsultantsByProject(projectId: number): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(`${this.apiUrl}/by-project/${projectId}`);
  }

  getConsultantEvaluations(consultantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${consultantId}/evaluations`);
  }

  getConsultantMissions(consultantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${consultantId}/missions`);
  }

  getConsultantTrainings(consultantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${consultantId}/trainings`);
  }

  getConsultantSkills(consultantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${consultantId}/skills`);
  }

  addConsultantSkill(consultantId: number, skillData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${consultantId}/skills`, skillData);
  }

  updateConsultantSkill(consultantId: number, skillId: number, skillData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${consultantId}/skills/${skillId}`, skillData);
  }

  getActiveConsultants(): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(`${this.apiUrl}/active`);
  }

  getAvailableConsultants(): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(`${this.apiUrl}/available`);
  }

  getConsultantStats(consultantId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${consultantId}/stats`);
  }
}
