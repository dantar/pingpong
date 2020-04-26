import { Injectable } from '@angular/core';
import { PlayerDto } from '../models/player.model';

@Injectable({
  providedIn: 'root'
})
export class SharedDataService {

  player: PlayerDto;

  constructor() { }

}
