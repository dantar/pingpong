import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { PlayerDto, TableDto, SeatDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { MessageDto as SseDto, RegisterPlayerDto, StalePlayersDto, AvailableTableDto,
  TablePlayerAcceptSseDto, TablePlayerInvitationSseDto } from 'src/app/models/operations-dto.model';
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

  players: PlayerDto[];
  tables: TableDto[];
  tablesmap: {[id:string]: TableDto};

  connected: boolean;

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
      this.tablesmap = {};
      this.tables.forEach(t=> {
        this.tablesmap[t.uuid] = t;
      });
    });
  }

  dropTable(table: TableDto) {
    console.log(table);
  }

  initSse() {
    this.connected = false;
    this.shared.sse.addEventListener('message', message => {
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
      case TablePlayerInvitationSseDto.CODE:
        this.onSseTablePlayerInvitation(dto as TablePlayerInvitationSseDto);
        break;
      case TablePlayerAcceptSseDto.CODE:
        this.onSseTablePlayerAccept(dto as TablePlayerAcceptSseDto);
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onSseTablePlayerInvitation(dto: TablePlayerInvitationSseDto) {
    const index = this.tables.indexOf(this.tablesmap[dto.table.uuid]);
    if (index === -1) {
      this.tables.push(dto.table);
    } else {
      this.tables.splice(index, 1, dto.table);
    }
    this.tablesmap[dto.table.uuid] = dto.table;
    this.changes.detectChanges();
  }
  onSseTablePlayerAccept(dto: TablePlayerAcceptSseDto) {
    const index = this.tables.indexOf(this.tablesmap[dto.table.uuid]);
    if (index === -1) {
      this.tables.push(dto.table);
    } else {
      this.tables.splice(index, 1, dto.table);
    }
    this.tablesmap[dto.table.uuid] = dto.table;
    this.changes.detectChanges();
  }
  onSseAvailableTable(dto: AvailableTableDto) {
    this.tables.push(dto.table);
    this.tablesmap[dto.table.uuid] = dto.table;
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
      console.log(table);
    });
  }

  createTableWithPlayer(player: PlayerDto) {
    const table = {
      seats: [{player: player, open: false, pending: true}], 
      owner: this.shared.player
    }
    this.rest.newTable(table).subscribe(table => {
      this.tables.push(table);
      this.tablesmap[table.uuid] = table;
      this.changes.detectChanges();
    });
  }

  goToTable(table: TableDto) {
    this.router.navigate(['table', table.uuid]);
  }

  includePlayer(table: TableDto, player: PlayerDto) {
    this.rest.newTablePlayer(table, player).subscribe(table => {
      console.log(table);
      // something
    });
  }

  acceptInvitation(table: TableDto, seat: SeatDto, accept: boolean) {
    this.rest.acceptInvitation(table, seat, accept).subscribe(t => {
      console.log(t);
    });
    if (! accept) {
      this.tables.splice(this.tables.indexOf(table), 1);
    }
  }

  tableAvailablePlayers(table: TableDto): PlayerDto[] {
    const seats = table.seats.filter(s=>s.player!=null).map(s => s.player.uuid);
    return this.players.filter(p => p.uuid != table.owner.uuid && !seats.includes(p.uuid));
  }

}
