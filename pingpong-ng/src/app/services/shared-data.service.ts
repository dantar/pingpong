import { Injectable } from '@angular/core';
import { PlayerDto, TableDto } from '../models/player.model';

@Injectable({
  providedIn: 'root'
})
export class SharedDataService {

  player: PlayerDto;

  table: TableDto;

  constructor() { }

}
