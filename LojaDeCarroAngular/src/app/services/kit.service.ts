import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Kit } from '../models/kit.model';

@Injectable({
  providedIn: 'root',
})
export class KitService {
  constructor(private httpClient: HttpClient) {}

  url: string = 'http://localhost:8080/kit';

  findAllKits(): Observable<Kit[]> {
    return this.httpClient.get<Kit[]>(`${this.url}`);
  }

  findKitById(id: number): Observable<Kit> {
    return this.httpClient.get<Kit>(`${this.url}/${id}`);
  }

  saveKit(kit: Kit): Observable<Kit> {
    if (kit.id) {
      return this.httpClient.put<Kit>(`${this.url}/${kit.id}`, kit);
    } else {
      return this.httpClient.post<Kit>(`${this.url}`, kit);
    }
  }
}
