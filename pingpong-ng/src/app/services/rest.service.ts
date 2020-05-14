import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PlayerDto, TableDto, SeatDto } from '../models/player.model';
import { Observable } from 'rxjs';
import { OpResult } from '../models/operations-dto.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestService {
  constructor(private http: HttpClient) { }

  register(player: PlayerDto): Observable<PlayerDto> {
    return this.http.post<PlayerDto>(environment.server + '/register', player);
  }

  newTable(table: TableDto): Observable<TableDto> {
    return this.http.post<TableDto>(environment.server + '/table', table);
  }

  newTablePlayer(table: TableDto, player: PlayerDto): Observable<TableDto> {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/invite', player);
  }

  acceptInvitation(table: TableDto, player: PlayerDto, accept: boolean) {
    const action = accept ? '/accept' : '/reject';
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + action, player);
  }
  
  players(): Observable<PlayerDto[]> {
    return this.http.get<PlayerDto[]>(environment.server + '/players');
  }

  tables(player: PlayerDto): Observable<TableDto[]> {
    return this.http.get<TableDto[]>(environment.server + '/tables/' + player.uuid);
  }

  allTables(): Observable<TableDto[]> {
    return this.http.get<TableDto[]>(environment.server + '/tables');
  }

  table(uuid: string): Observable<TableDto> {
    return this.http.get<TableDto>(environment.server + '/table/' + uuid);
  }

  startTable(table: TableDto) {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/start', {});
  }

  hello() {
    return this.http.get<TableDto>(environment.server + '/hello');
  }

}
