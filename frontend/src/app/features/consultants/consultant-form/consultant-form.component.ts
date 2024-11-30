import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { ConsultantService } from '@core/services/consultant.service';
import { SkillService } from '@core/services/skill.service';
import { Consultant } from '@core/models/consultant.model';
import { Skill } from '@core/models/skill.model';
import { firstValueFrom } from 'rxjs';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';

@Component({
  selector: 'app-consultant-form',
  templateUrl: './consultant-form.component.html',
  styleUrls: ['./consultant-form.component.scss']
})
export class ConsultantFormComponent implements OnInit {
  consultantForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  consultantId?: number;
  selectedSkills: Skill[] = [];
  availableSkills: Skill[] = [];
  filteredSkills: Observable<Skill[]>;
  skillInputControl = new FormControl('');
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  constructor(
    private fb: FormBuilder,
    private consultantService: ConsultantService,
    private skillService: SkillService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.consultantForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      rating: [0, [Validators.min(0), Validators.max(5)]],
      skills: [[]]
    });

    this.filteredSkills = this.skillInputControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterSkills(
        typeof value === 'string' 
          ? value 
          : value && (value as Skill).name 
            ? (value as Skill).name 
            : ''
      ))
    );
  }

  async ngOnInit(): Promise<void> {
    await this.loadSkills();
    const params = this.route.snapshot.params;
    if (params['id']) {
      this.isEditMode = true;
      this.consultantId = +params['id'];
      await this.loadConsultant();
    }
  }

  private async loadConsultant(): Promise<void> {
    if (this.consultantId) {
      this.isLoading = true;
      try {
        const consultant = await firstValueFrom(this.consultantService.getConsultant(this.consultantId));
        this.consultantForm.patchValue({
          name: consultant.name,
          email: consultant.email,
          phone: consultant.phone,
          rating: consultant.rating
        });
        this.selectedSkills = consultant.skills || [];
      } catch (error) {
        console.error('Error loading consultant:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  private async loadSkills(): Promise<void> {
    this.isLoading = true;
    try {
      this.availableSkills = await firstValueFrom(this.skillService.getSkills());
    } catch (error) {
      console.error('Error loading skills:', error);
    } finally {
      this.isLoading = false;
    }
  }

  addSkill(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      const skill = this.availableSkills.find(s => 
        s.name.toLowerCase() === value.toLowerCase()
      );
      if (skill && !this.selectedSkills.find(s => s.id === skill.id)) {
        this.selectedSkills = [...this.selectedSkills, skill];
        this.consultantForm.patchValue({ skills: this.selectedSkills });
      }
    }
    event.chipInput?.clear();
    this.skillInputControl.setValue('');
  }

  removeSkill(skill: Skill): void {
    this.selectedSkills = this.selectedSkills.filter(s => s.id !== skill.id);
    this.consultantForm.patchValue({ skills: this.selectedSkills });
  }

  async onSubmit(): Promise<void> {
    if (this.consultantForm.valid) {
      this.isLoading = true;
      const consultantData = {
        ...this.consultantForm.value,
        skills: this.selectedSkills
      };

      try {
        if (this.isEditMode && this.consultantId) {
          await firstValueFrom(this.consultantService.updateConsultant(this.consultantId, consultantData));
        } else {
          await firstValueFrom(this.consultantService.createConsultant(consultantData));
        }
        this.router.navigate(['/consultants']);
      } catch (error) {
        console.error('Error saving consultant:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  private _filterSkills(value: string): Skill[] {
    const filterValue = value.toLowerCase();
    return this.availableSkills.filter(skill => 
      skill.name.toLowerCase().includes(filterValue) &&
      !this.selectedSkills.find(s => s.id === skill.id)
    );
  }

  displaySkill(skill: Skill): string {
    return skill ? skill.name : '';
  }

  onSkillSelected(event: MatAutocompleteSelectedEvent): void {
    const skill = event.option.value as Skill;
    if (!this.selectedSkills.find(s => s.id === skill.id)) {
      this.selectedSkills = [...this.selectedSkills, skill];
      this.consultantForm.patchValue({ skills: this.selectedSkills });
    }
    this.skillInputControl.setValue('');
  }

  onCancel(): void {
    this.router.navigate(['/consultants']);
  }
}
