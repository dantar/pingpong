import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { PlayerDto, TableDto, SeatDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { MessageDto as SseDto, RegisterPlayerDto, StalePlayersDto, AvailableTableDto,
  TablePlayerAcceptSseDto, TablePlayerInvitationSseDto, TableStartSseDto } from 'src/app/models/operations-dto.model';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { PlayerQuitDto } from 'src/app/models/fantascatti.model';

@Component({
  selector: 'app-tables-room',
  templateUrl: './tables-room.component.html',
  styleUrls: ['./tables-room.component.scss']
})
export class TablesRoomComponent implements OnInit, OnDestroy {

  constructor(private http: HttpClient, 
    public shared: SharedDataService,
    private rest: RestService,
    private changes: ChangeDetectorRef,
    private router: Router) { }

  players: PlayerDto[];
  tables: TableDto[];
  tablesmap: {[id:string]: TableDto};
  ownership: {[id:string]: TableDto};
  seatedmap: {[id:string]: TableDto};

  mytable: TableDto;

  ngOnInit(): void {
    this.players = null;
    this.tables = null;
    this.initSse();
    this.initTables();
    this.initPlayers();
  }
  ngOnDestroy(): void {
    this.destroySse();
  }

  initTables() {
    this.tablesmap = {};
    this.ownership = {};
    this.seatedmap = {};
    this.rest.allTables().subscribe(tables => {
      this.tables = tables;
      this.tables.forEach(t=> {
        this.tablesmap[t.uuid] = t;
        this.ownership[t.owner.uuid] = t;
        this.seatedmap[t.owner.uuid] = t;
        t.seats.forEach(s => {
          this.seatedmap[s.player.uuid] = t;
        });
      });
    });
  }

  dropTable(table: TableDto) {
    console.log(table);
  }

  initSse() {
    this.shared.sse.addEventListener('message', this.messageEventListener)
    console.log("init SSE", this);
  }
  destroySse() {
    if (this.shared.sse) {
      console.log("destroy SSE: removed event listener", this);
      this.shared.sse.removeEventListener('message', this.messageEventListener);
    } else {
      console.log("destroy SSE", this);
    }
  }
  messageEventListener = (
    message => {
      this.onSseEvent(JSON.parse(message.data) as SseDto);
    }    
  );

  initPlayers() {
    this.rest.players().subscribe(players=> {
      this.players = players.filter(p => p.uuid != this.shared.player.uuid);
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
      case TableStartSseDto.CODE:
        this.onSseTableStartSseDto(dto as TableStartSseDto);
        break;
      case PlayerQuitDto.CODE:
        this.onPlayerQuit(dto as PlayerQuitDto);
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onSseTableStartSseDto(dto: TableStartSseDto) {
    this.router.navigate(['table', dto.table.uuid]);
    this.changes.detectChanges();
  }
  onSseTablePlayerInvitation(dto: TablePlayerInvitationSseDto) {
    const index = this.tables.indexOf(this.tablesmap[dto.table.uuid]);
    if (index === -1) {
      this.tables.push(dto.table);
    } else {
      this.tables.splice(index, 1, dto.table);
    }
    this.tablesmap[dto.table.uuid] = dto.table;
    this.ownership[dto.table.owner.uuid] = dto.table;
    this.seatedmap[dto.player.uuid] = dto.table;
    if (dto.player.uuid === this.shared.player.uuid || dto.table.owner.uuid === this.shared.player.uuid ||
      (this.mytable && this.mytable.uuid === dto.table.uuid)) {
      this.mytable = dto.table;
    }
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
    this.ownership[dto.table.owner.uuid] = dto.table;
    if (!dto.accepted) {
      delete this.seatedmap[dto.player.uuid];
      if (dto.player.uuid === this.shared.player.uuid) {
        this.mytable = null;
      }
    }
    if (this.mytable && this.mytable.uuid === dto.table.uuid) {
      this.mytable = dto.table;
    }    
    this.changes.detectChanges();
  }
  onSseAvailableTable(dto: AvailableTableDto) {
    this.tables.push(dto.table);
    this.tablesmap[dto.table.uuid] = dto.table;
    this.ownership[dto.table.owner.uuid] = dto.table;
    this.seatedmap[dto.table.owner.uuid] = dto.table;
    if (dto.table.owner.uuid === this.shared.player.uuid) {
      this.mytable = dto.table;
    }
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
  onPlayerQuit(dto: PlayerQuitDto) {
    delete this.seatedmap[dto.player.uuid];
    if (this.ownership.hasOwnProperty(dto.player.uuid)) {
      const table = this.ownership[dto.player.uuid];
      table.seats.forEach(s => {
        if (s.player && this.seatedmap.hasOwnProperty(s.player.uuid)) {
          delete this.seatedmap[s.player.uuid];
        }
      });
      delete this.tablesmap[table.uuid];
    }
    this.changes.detectChanges();
  }

  // table

  createTable() {
    this.rest.newTable({seats: [], owner: this.shared.player}).subscribe(table => {
      console.log(table);
      this.mytable = table;
    });
  }

  clickPlayer(player: PlayerDto) {
    if (this.mytable) {
      this._invitePlayer(player);
    } else {
      this.rest.newTable({seats: [], owner: this.shared.player}).subscribe(table => {
        this.mytable = table;
        this._invitePlayer(player);
      });
    }
  }
  _invitePlayer(player: PlayerDto) {
    this.rest.newTablePlayer(this.mytable, player).subscribe(table => {
      console.log(table);
    });
  }

  startTable(table: TableDto) {
    this.rest.startTable(table).subscribe(t => {
      console.log(t);
    });
    this.router.navigate(['table', table.uuid]);
  }

  includePlayer(table: TableDto, player: PlayerDto) {
    this.rest.newTablePlayer(table, player).subscribe(table => {
      console.log(table);
      // something
    });
  }

  acceptInvitation(table: TableDto, accept: boolean) {
    this.rest.acceptInvitation(table, this.shared.player, accept).subscribe(t => {
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

  tableCanStart(table: TableDto) {
    return table.seats.filter(s=>s.pending || s.open).length === 0;
  }

  isPending(table: TableDto) {
    return table.seats.filter(s=>s.pending && s.player && s.player.uuid === this.shared.player.uuid).length > 0;
  }

  goSolo() {
    this.rest.newTable({seats: [], owner: this.shared.player}).subscribe(table => {
      console.log(table);
      this.mytable = table;
      this.startTable(this.mytable);
    });
  }

}
