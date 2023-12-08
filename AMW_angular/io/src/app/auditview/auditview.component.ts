import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuditviewService } from './auditview.service';
import { ResourceService } from '../resource/resource.service';
import { AuditLogEntry } from './auditview-entry';
import { AuditviewTableComponent } from './auditview-table/auditview-table.component';
import { NotificationComponent } from '../shared/elements/notification/notification.component';
import { NgIf } from '@angular/common';
import { LoadingIndicatorComponent } from '../shared/elements/loading-indicator.component';

@Component({
  selector: 'amw-auditview',
  templateUrl: './auditview.component.html',
  standalone: true,
  imports: [LoadingIndicatorComponent, NgIf, NotificationComponent, AuditviewTableComponent],
})
export class AuditviewComponent implements OnInit {
  name: string;
  auditLogEntries: AuditLogEntry[] = [];
  filterQuery: string = '';
  errorMessage: string;
  successMessage: string;
  resourceId: number;
  isLoading: boolean = true;

  constructor(
    private auditViewService: AuditviewService,
    private resourceService: ResourceService,
    private activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((param: any) => {
      if (param['resourceId']) {
        try {
          this.resourceId = JSON.parse(param['resourceId']);
        } catch (e) {
          console.error(e);
          this.errorMessage = 'Error parsing resourceId';
        }
      }
    });

    if (this.resourceId) {
      this.resourceService.getResourceName(this.resourceId).subscribe(
        /* happy path */ (r) => (this.name = r.name),
        /* error path */ (e) => (this.errorMessage = e),
        /* onComplete */ () => {},
      );
      this.auditViewService.getAuditLogForResource(this.resourceId).subscribe(
        /* happy path */ (r) => (this.auditLogEntries = r),
        /* error path */ (e) => (this.errorMessage = e),
        /* onComplete */ () => (this.isLoading = false),
      );
    } else {
      console.error('Resource Id must be set');
    }
  }
}
