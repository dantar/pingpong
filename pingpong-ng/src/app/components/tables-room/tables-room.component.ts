import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { PlayerDto, TableDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { MessageDto as SseDto, RegisterPlayerDto, StalePlayersDto, AvailableTableDto } from 'src/app/models/operations-dto.model';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tables-room',
  templateUrl: './tables-room.component.html',
  styleUrls: ['./tables-room.component.scss']
})
export class TablesRoomComponent implements OnInit {

  constructor(private http: HttpClient, 
    public shared: SharedDataService,
    private rest: RestService,
    private changes: ChangeDetectorRef,
    private router: Router) { }

  sse: EventSource;
  players: PlayerDto[];
  tables: TableDto[];

  ngOnInit(): void {
    this.players = null;
    this.tables = null;
    this.initSse();
    this.initTables();
    this.initPlayers();
  }
  
  initTables() {
    this.rest.tables().subscribe(tables => {
      this.tables = tables;
    });
  }

  initSse() {
    this.sse = new EventSource(environment.server + '/sse/' + this.shared.player.uuid);
    this.sse.addEventListener('message', message => {
      this.onSseEvent(JSON.parse(message.data) as SseDto);
    })
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
        this.onSseStalePlayers(dto as StalePlayersDto);
        break;
      case AvailableTableDto.CODE:
        this.onSseAvailableTable(dto as AvailableTableDto);
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onSseAvailableTable(dto: AvailableTableDto) {
    this.tables.push(dto.table);
    this.changes.detectChanges();
  }
  onSseStalePlayers(dto: StalePlayersDto) {
    const stale = dto.players.map(p=>p.uuid);
    this.players = this.players.filter(p=>!stale.includes(p.uuid));
    this.changes.detectChanges();
  }
  onSseRegisterPlayer(dto: RegisterPlayerDto) {
    this.players.push(dto.player);
    this.changes.detectChanges();
  }

  // table

  createTable() {
    this.rest.newTable({seats: [], owner: this.shared.player}).subscribe(table => {
      this.tables.push(table);
      this.changes.detectChanges();
    });
  }

  createTableWithPlayer(player: PlayerDto) {
    const table = {
      seats: [{player: player, open: false, pending: true}], 
      owner: this.shared.player
    }
    this.rest.newTable(table).subscribe(table => {
      this.tables.push(table);
      this.changes.detectChanges();
    });
  }

  goToTable(table: TableDto) {
    this.router.navigate(['table', table.uuid]);
  }

}
