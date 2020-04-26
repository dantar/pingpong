import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableDto, PlayerDto } from '../models/player.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FantascattiService {

  constructor(private http: HttpClient) { }

  playerReady(table: TableDto, player: PlayerDto) {
    return this.http.post<TableDto>(environment.server + '/fantascatti/' + table.uuid + '/ready', player);
  }


}
