import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PlayerDto, TableDto } from '../models/player.model';
import { Observable } from 'rxjs';
import { OpResult } from '../models/operations-dto.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestService {
  playerReady(table: TableDto, player: PlayerDto) {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/ready', player);
  }
  table(uuid: string): Observable<TableDto> {
    return this.http.get<TableDto>(environment.server + '/table/' + uuid);
  }
  
  constructor(private http: HttpClient) { }

  register(player: PlayerDto): Observable<PlayerDto> {
    return this.http.post<PlayerDto>(environment.server + '/register', player);
  }

  newTable(table: TableDto): Observable<TableDto> {
    return this.http.post<TableDto>(environment.server + '/table', table);
  }

  players(): Observable<PlayerDto[]> {
    return this.http.get<PlayerDto[]>(environment.server + '/players');
  }

  tables(): Observable<TableDto[]> {
    return this.http.get<TableDto[]>(environment.server + '/tables');
  }

}
