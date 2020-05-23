import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PlayerDto, TableDto, SituationDto, RobotDto, SeatDto } from '../models/player.model';
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

  newTableRobot(table: TableDto, robot: RobotDto) {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/robot', robot);
  }

  acceptInvitation(table: TableDto, player: PlayerDto, accept: boolean) {
    const action = accept ? '/accept' : '/reject';
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + action, player);
  }

  dropSeat(table: TableDto, seat: SeatDto) {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/seat/drop', seat);
  }

  dropTable(table: TableDto) {
    return this.http.post<TableDto>(environment.server + '/table/' + table.uuid + '/drop', null);
  }

  situation(): Observable<SituationDto> {
    return this.http.get<SituationDto>(environment.server + '/situation');
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

  ackSse(player: PlayerDto) {
    return this.http.post<TableDto>(environment.server + '/sse/ack', player);
  }

}
