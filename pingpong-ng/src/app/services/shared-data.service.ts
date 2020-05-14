import { Injectable } from '@angular/core';
import { PlayerDto, TableDto } from '../models/player.model';
import { RestService } from './rest.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SharedDataService {

  player: PlayerDto;
  sse: EventSource;
  connected: boolean;

  table: TableDto;

  constructor(private rest: RestService) {
    this.connected = false;
    const fetch = localStorage.getItem('player-dto');
    this.sse = null;
    if (fetch != null) {
      this.player = JSON.parse(fetch);
      this.reconnect();
    }
  }

  initSse() {
    this.sse = new EventSource(environment.server + '/sse/' + this.player.uuid);
    this.sse.addEventListener('open', open => {
      this.connected = true;
      console.log(this, 'open', open);
    })
    this.sse.addEventListener('error', error => {
      console.log(this, 'error', error);
      //this.sse.close();
      //this.sse = null;
      //this.connected = false;
    })
    this.sse.addEventListener('message', message => {
      console.log(this, 'message', message);
    })
  }
 
  setPlayer(player: PlayerDto) {
    this.player = player;
    if (environment.store) {
      localStorage.setItem('player-dto', JSON.stringify(this.player));
    }
  }

  reconnect() {
    this.rest.register(this.player).subscribe(player => {
      this.setPlayer(player);
      this.initSse();
    },
    error => {
      console.log('error', error);
      this.sse = null;
    });
  }

}
