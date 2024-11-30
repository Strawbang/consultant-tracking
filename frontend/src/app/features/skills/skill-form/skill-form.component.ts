import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { SkillService } from '@core/services/skill.service';
import { Skill } from '@core/models/skill.model';

@Component({
  selector: 'app-skill-form',
  templateUrl: './skill-form.component.html',
  styleUrls: ['./skill-form.component.scss']
})
export class SkillFormComponent implements OnInit {
  skillForm: FormGroup;
  isEditMode = false;
  skillId?: number;
  parentSkills: Skill[] = [];
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private skillService: SkillService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.skillForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      category: ['', Validators.required],
      level: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      isRequired: [false],
      parentId: [null]
    });
  }

  async ngOnInit() {
    await this.loadParentSkills();
    const params = this.route.snapshot.params;
    if (params['id']) {
      this.isEditMode = true;
      this.skillId = +params['id'];
      await this.loadSkill();
    }
  }

  private async loadSkill() {
    if (this.skillId) {
      this.isLoading = true;
      try {
        const skills = await firstValueFrom(this.skillService.getSkills());
        const skill = skills.find(s => s.id === this.skillId);
        if (skill) {
          this.skillForm.patchValue({
            name: skill.name,
            description: skill.description,
            category: skill.category,
            level: skill.level,
            isRequired: skill.isRequired,
            parentId: skill.parentId
          });

          if (skill.parentId) {
            const parentSkill = this.parentSkills.find(s => s.id === skill.parentId);
            if (parentSkill) {
              this.skillForm.get('parentId')?.setValue(parentSkill.id);
            }
          }
        }
      } catch (error) {
        console.error('Error loading skill:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  private async loadParentSkills() {
    this.isLoading = true;
    try {
      const skills = await firstValueFrom(this.skillService.getSkills());
      this.parentSkills = skills.filter(skill => !skill.parentId);
    } catch (error) {
      console.error('Error loading parent skills:', error);
    } finally {
      this.isLoading = false;
    }
  }

  async onSubmit() {
    if (this.skillForm.valid) {
      this.isLoading = true;
      try {
        const skillData = this.skillForm.value;
        if (this.isEditMode && this.skillId) {
          await firstValueFrom(this.skillService.updateSkill(this.skillId, skillData));
        } else {
          await firstValueFrom(this.skillService.createSkill(skillData));
        }
        this.router.navigate(['/skills']);
      } catch (error) {
        console.error('Error saving skill:', error);
      } finally {
        this.isLoading = false;
      }
    }
  }

  onCancel() {
    this.router.navigate(['/skills']);
  }
}
