import { Component, OnInit } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { firstValueFrom } from 'rxjs';
import { SkillService } from '@core/services/skill.service';
import { Skill } from '@core/models/skill.model';

interface SkillNode {
  id: number;
  name: string;
  level: number;
  category: string;
  children?: SkillNode[];
}

@Component({
  selector: 'app-skill-tree',
  templateUrl: './skill-tree.component.html',
  styleUrls: ['./skill-tree.component.scss']
})
export class SkillTreeComponent implements OnInit {
  treeControl = new NestedTreeControl<SkillNode>(node => node.children);
  dataSource = new MatTreeNestedDataSource<SkillNode>();
  
  originalSkills: Skill[] = [];
  skillCategories: string[] = [];
  selectedCategory: string = '';
  isLoading = false;
  error: string | null = null;

  constructor(private skillService: SkillService) {}

  async ngOnInit() {
    await this.loadSkills();
  }

  async loadSkills() {
    this.isLoading = true;
    this.error = null;
    try {
      const skills = await firstValueFrom(this.skillService.getSkills());
      this.originalSkills = skills;
      this.skillCategories = await firstValueFrom(this.skillService.getCategories());
      this.updateTree();
    } catch (error) {
      console.error('Error loading skills:', error);
      this.error = 'Error loading skills. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  updateTree() {
    const filteredSkills = this.selectedCategory
      ? this.originalSkills.filter(skill => skill.category === this.selectedCategory)
      : this.originalSkills;
    
    this.dataSource.data = this.buildSkillTree(filteredSkills);
  }

  buildSkillTree(skills: Skill[]): SkillNode[] {
    const rootSkills = skills.filter(skill => !skill.parentId);
    return rootSkills.map(skill => this.buildSkillNode(skill, skills));
  }

  buildSkillNode(skill: Skill, allSkills: Skill[]): SkillNode {
    const children = allSkills
      .filter(s => s.parentId === skill.id)
      .map(s => this.buildSkillNode(s, allSkills));

    const node: SkillNode = {
      id: skill.id!,
      name: skill.name,
      level: skill.level || 1,
      category: skill.category || 'Uncategorized'
    };

    if (children.length > 0) {
      node.children = children;
    }

    return node;
  }

  hasChild = (_: number, node: SkillNode) => !!node.children && node.children.length > 0;

  onCategoryChange() {
    this.updateTree();
  }

  getLevelBadgeClass(level: number): string {
    const baseClass = 'badge ';
    switch (level) {
      case 1: return baseClass + 'bg-success';
      case 2: return baseClass + 'bg-info';
      case 3: return baseClass + 'bg-primary';
      case 4: return baseClass + 'bg-warning';
      case 5: return baseClass + 'bg-danger';
      default: return baseClass + 'bg-secondary';
    }
  }

  getCategoryBadgeClass(category: string): string {
    const baseClass = 'badge ';
    switch (category.toLowerCase()) {
      case 'technique': return baseClass + 'bg-primary';
      case 'soft skills': return baseClass + 'bg-success';
      case 'management': return baseClass + 'bg-warning';
      case 'métier': return baseClass + 'bg-info';
      default: return baseClass + 'bg-secondary';
    }
  }
}
