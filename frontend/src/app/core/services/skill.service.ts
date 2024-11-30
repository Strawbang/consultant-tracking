import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Skill, SkillCreate, SkillUpdate, SkillFilter } from '../models/skill.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SkillService {
  private apiUrl = `${environment.apiUrl}/skills`;
  private readonly categories = ['Technique', 'Soft Skills', 'Management', 'Métier'];

  constructor(private http: HttpClient) {}

  getSkills(filter?: SkillFilter): Observable<Skill[]> {
    let params = new HttpParams();
    if (filter) {
      if (filter.name) {
        params = params.set('name', filter.name);
      }
      if (filter.parentId) {
        params = params.set('parentId', filter.parentId.toString());
      }
    }
    return this.http.get<Skill[]>(this.apiUrl, { params });
  }

  getSkillById(id: number): Observable<Skill> {
    return this.http.get<Skill>(`${this.apiUrl}/${id}`);
  }

  createSkill(skill: SkillCreate): Observable<Skill> {
    return this.http.post<Skill>(this.apiUrl, skill);
  }

  updateSkill(id: number, skill: SkillUpdate): Observable<Skill> {
    return this.http.put<Skill>(`${this.apiUrl}/${id}`, skill);
  }

  deleteSkill(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Méthodes spécifiques
  getChildSkills(parentId: number): Observable<Skill[]> {
    return this.http.get<Skill[]>(`${this.apiUrl}/parent/${parentId}`);
  }

  getRootSkills(): Observable<Skill[]> {
    return this.http.get<Skill[]>(`${this.apiUrl}/root`);
  }

  getCategories(): Observable<string[]> {
    return new Observable(observer => {
      observer.next(this.categories);
      observer.complete();
    });
  }
}
