import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableDto, PlayerDto } from '../models/player.model';
import { environment } from 'src/environments/environment';
import { FantascattiPiece } from '../models/fantascatti.model';

@Injectable({
  providedIn: 'root'
})
export class FantascattiService {

  constructor(private http: HttpClient) { }

  playerReady(table: TableDto, player: PlayerDto) {
    return this.http.post<TableDto>(environment.server + '/fantascatti/' + table.uuid + '/ready', player);
  }

  pickPiece(table: TableDto, player: PlayerDto, piece: FantascattiPiece) {
    return this.http.post<TableDto>(environment.server + '/fantascatti/' + table.uuid + '/' + player.uuid +  '/pick', piece);
  }

  quitGame(table: TableDto, player: PlayerDto) {
    return this.http.post<TableDto>(environment.server + '/fantascatti/' + table.uuid + '/quit', player);
  }

}
