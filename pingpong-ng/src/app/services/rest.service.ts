import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PlayerDto } from '../models/player.model';
import { Observable } from 'rxjs';
import { OpResult } from '../models/operations-dto.model';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  constructor(private http: HttpClient) { }

  register(player: PlayerDto): Observable<PlayerDto> {
    return this.http.post<PlayerDto>('http://localhost:8080/register', player);
  }

  players(): Observable<PlayerDto[]> {
    return this.http.get<PlayerDto[]>('http://localhost:8080/players');
  }


}
