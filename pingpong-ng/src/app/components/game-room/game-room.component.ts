import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TableDto, PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { environment } from 'src/environments/environment';
import { MessageDto } from 'src/app/models/operations-dto.model';
import { FantascattiSseDto, PlayerReadyDto } from 'src/app/models/fantascatti.model';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { FantascattiService } from 'src/app/services/fantascatti.service';

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
    private fantascatti: FantascattiService,
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
      this.players = [];
      this.players.push(table.owner);
      this.players.push(...table.seats.map(s=>s.player)); // array spread operator!
    });
  }

  initSse(uuid: string) {
    this.sse = new EventSource(environment.server + '/fantascatti/sse/' + uuid);
    this.sse.addEventListener('message', message => {
      this.onSseEvent(JSON.parse(message.data) as MessageDto);
    })
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
    this.fantascatti.playerReady(this.table, this.shared.player).subscribe(
      // loading? 
    );
  }

}
