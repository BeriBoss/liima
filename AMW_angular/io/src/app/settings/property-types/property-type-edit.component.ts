import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DatePickerComponent } from '../../shared/date-picker/date-picker.component';
import { PropertyType } from './property-type';
import { IconComponent } from '../../shared/icon/icon.component';
import { PropertyTag } from './property-tag';

@Component({
  selector: 'amw-property-type-edit',
  templateUrl: './property-type-edit.component.html',
  standalone: true,
  imports: [DatePickerComponent, IconComponent, FormsModule],
})
export class PropertyTypeEditComponent implements OnInit {
  @Input() propertyType: PropertyType;
  @Output() savePropertyType: EventEmitter<PropertyType> = new EventEmitter<PropertyType>();

  title = 'property type';
  newTag: string = '';

  constructor(public activeModal: NgbActiveModal) {
    this.activeModal = activeModal;
  }

  ngOnInit(): void {}

  getTitle(): string {
    return this.propertyType.id ? `Edit ${this.title}` : `Add ${this.title}`;
  }

  cancel() {
    this.activeModal.close();
  }

  save() {
    const propertyType: PropertyType = {
      name: this.propertyType.name,
      id: this.propertyType.id ? this.propertyType.id : null,
      validationRegex: this.propertyType.validationRegex,
      encrypted: this.propertyType.encrypted,
      propertyTags: this.propertyType.propertyTags,
    };
    this.savePropertyType.emit(propertyType);
    this.activeModal.close();
  }

  deleteTag(tag: PropertyTag) {
    this.propertyType.propertyTags = this.propertyType.propertyTags.filter((value) => {
      return value.id !== tag.id || value.name !== tag.name;
    });
  }

  onEnter() {
    this.propertyType.propertyTags.push({ id: 0, name: this.newTag.trim() });
    this.newTag = '';
  }
}
