import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Project, ProjectCreate, ProjectUpdate, ProjectStatus } from '@core/models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  getProject(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  createProject(project: ProjectCreate): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  updateProject(id: number, project: ProjectUpdate): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project);
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Additional methods for specific operations
  getProjectsByStatus(status: ProjectStatus): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/by-status/${status}`);
  }

  getProjectsByConsultant(consultantId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/by-consultant/${consultantId}`);
  }

  getConsultantProjects(consultantId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/consultant/${consultantId}`);
  }

  updateProjectConsultants(projectId: number, consultantIds: number[]): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/consultants`, { consultantIds });
  }

  updateProjectStatus(projectId: number, status: ProjectStatus): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/status`, { status });
  }
}
