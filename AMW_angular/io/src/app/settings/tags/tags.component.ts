import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

type Tag = { id: number; name: string };

@Component({
  selector: 'amw-tags',
  templateUrl: './tags.component.html',
  styleUrl: './tags.component.scss',
})
export class TagsComponent {
  tagName: string = '';
  tags: Tag[] = [];
  private tagId = 0;

  constructor(private http: HttpClient) {
    http.get<Tag[]>('AMW_rest/resources/settings/tags').subscribe((data) => {
      this.tags = data;
    });
  }

  addTag(): void {
    if (this.tagName.trim().length > 0) {
      this.http.post<Tag>('AMW_rest/resources/settings/tags', { name: this.tagName }).subscribe((newTag) => {
        this.tags.push(newTag);
      });
    }
  }

  deleteTag(tagId: number): void {
    this.tags = this.tags.filter((tag) => tag.id !== tagId);
  }
}
