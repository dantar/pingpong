import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { MessageDto as SseDto, RegisterPlayerDto, StalePlayersDto } from 'src/app/models/operations-dto.model';

@Component({
  selector: 'app-tables-room',
  templateUrl: './tables-room.component.html',
  styleUrls: ['./tables-room.component.scss']
})
export class TablesRoomComponent implements OnInit {

  constructor(private http: HttpClient, 
    private shared: SharedDataService,
    private rest: RestService,
    private changes: ChangeDetectorRef) { }

  sse: EventSource;
  players: PlayerDto[];

  ngOnInit(): void {
    this.initSse();
    this.initPlayers();
  }
  
  initSse() {
    this.sse = new EventSource('http://localhost:8080/sse/' + this.shared.player.uuid);
    const component = this;
    this.sse.onopen = function (evt) {
        console.log('open', evt);
    };
    this.sse.onmessage = function (event: MessageEvent) {
      console.log('message', event);
      component.onSseEvent(JSON.parse(event.data) as SseDto);
      component.changes.detectChanges();
    };    
  }

  initPlayers() {
    this.rest.players().subscribe(players=> {
      this.players = players;
    });
  }

  onSseEvent(dto: SseDto) {
    console.log('event ' + dto.code, dto);
    switch (dto.code) {
      case RegisterPlayerDto.CODE:
        this.onSseRegisterPlayer(dto as RegisterPlayerDto);
        break;
      case StalePlayersDto.CODE:
        this.onSseStlePlayers(dto as StalePlayersDto);
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onSseStlePlayers(dto: StalePlayersDto) {
    const stale = dto.players.map(p=>p.uuid);
    this.players = this.players.filter(p=>!stale.includes(p.uuid));
  }

  onSseRegisterPlayer(dto: RegisterPlayerDto) {
    this.players.push(dto.player);
  }

}
