import { computed, Injectable, signal } from '@angular/core';
import { BaseService } from '../base/base.service';
import { HttpClient } from '@angular/common/http';
import { Observable, startWith, Subject } from 'rxjs';
import { catchError, map, shareReplay, switchMap } from 'rxjs/operators';
import { Restriction } from '../settings/permission/restriction';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
  private reload$ = new Subject<Restriction[]>();
  private readonly restrictions$ = this.reload$.pipe(
    startWith(null),
    switchMap(() => this.getRestrictions()),
    shareReplay(1),
  );
  #restrictions = signal<Restriction[]>(null);
  restrictions = this.#restrictions.asReadonly();

  constructor(private http: HttpClient) {
    super();
    this.restrictions$.pipe(takeUntilDestroyed()).subscribe((values) => {
      this.#restrictions.set(values);
    });
  }

  refreshData() {
    this.getRestrictions().pipe(map((v) => this.reload$.next(v)));
  }

  private getRestrictions(): Observable<Restriction[]> {
    return this.http
      .get<Restriction[]>(`${this.getBaseUrl()}/permissions/restrictions/ownRestrictions/`, {
        headers: this.getHeaders(),
      })
      .pipe(catchError(this.handleError));
  }

  get isLoaded() {
    return computed(() => !!this.restrictions());
  }

  getActionsForPermission(permissionName: string): string[] {
    return this.restrictions()
      .filter((entry) => entry.permission.name === permissionName)
      .map((entry) => entry.action);
  }

  hasPermission(permissionName: string, action: string): boolean {
    return (
      this.getActionsForPermission(permissionName).find((value) => value === 'ALL' || value === action) !== undefined
    );
  }
}
