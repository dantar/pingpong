import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TableDto, PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { environment } from 'src/environments/environment';
import { MessageDto } from 'src/app/models/operations-dto.model';
import { FantascattiSseDto, PlayerReadyDto } from 'src/app/models/fantascatti.model';
import { SharedDataService } from 'src/app/services/shared-data.service';

@Component({
  selector: 'app-game-room',
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.scss']
})
export class GameRoomComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private rest: RestService,
    private changes: ChangeDetectorRef,
    public shared: SharedDataService,
  ) { }

  table: TableDto;
  sse: EventSource;

  players: PlayerDto[];
  ready: string[];

  ngOnInit(): void {
    this.ready = [];
    this.initSse(this.activatedRoute.snapshot.params['uuid']);
    this.rest.table(this.activatedRoute.snapshot.params['uuid']).subscribe(table => {
      this.table = table;
      this.players = table.seats.map(s=>s.player);
    });
  }

  initSse(uuid: string) {
    this.sse = new EventSource(environment.server + '/table/sse/' + uuid);
    const component = this;
    this.sse.onopen = function (evt) {
        console.log('open', evt);
    };
    this.sse.onmessage = function (event: MessageEvent) {
      console.log('message', event);
      component.onSseEvent(JSON.parse(event.data) as MessageDto);
    };    
  }

  onSseEvent(dto: FantascattiSseDto) {
    console.log('event ' + dto.code, dto);
    switch (dto.code) {
      case PlayerReadyDto.CODE:
        this.onSsePlayerReady(dto as PlayerReadyDto);
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onSsePlayerReady(dto: PlayerReadyDto) {
    this.ready.push(dto.player.uuid);
    this.changes.detectChanges();
  }

  iAmReady() {
    this.rest.playerReady(this.table, this.shared.player).subscribe(
      // loading? 
    );
  }

}
