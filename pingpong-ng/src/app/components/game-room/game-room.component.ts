import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TableDto, PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { environment } from 'src/environments/environment';
import { MessageDto } from 'src/app/models/operations-dto.model';
import { FantascattiSseDto, PlayerReadyDto, NewGuessDto, FantascattiCardDto, FantascattiPiece, PlayerPicksPieceDto } from 'src/app/models/fantascatti.model';
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
    private router: Router,
  ) { }

  table: TableDto;
  sse: EventSource;
  guess: FantascattiCardDto;

  players: PlayerDto[];
  ready: string[];
  score: {[id: string]: number};
  mypick: FantascattiPiece;

  pieces: FantascattiPiece[];

  ngOnInit(): void {
    this.pieces = [
      {shape: 'dragon', color: 'red'},
      {shape: 'chest', color: 'brown'},
      {shape: 'sword', color: 'blue'},
      {shape: 'gold', color: 'yellow'},
      {shape: 'shield', color: 'green'},
    ];
    this.mypick = null;
    this.ready = [];
    this.score = {};
    this.initSse(this.activatedRoute.snapshot.params['uuid']);
    this.rest.table(this.activatedRoute.snapshot.params['uuid']).subscribe(table => {
      this.table = table;
      this.players = [];
      this.players.push(table.owner);
      this.players.push(...table.seats.map(s=>s.player)); // array spread operator!
      this.players.forEach(p => {
        this.score[p.uuid] = 0;
      })
    });
  }

  initSse(uuid: string) {
    this.sse = new EventSource(environment.server + '/fantascatti/sse/' + uuid + '/' + this.shared.player.uuid);
    this.sse.addEventListener('message', message => {
      this.onSseEvent(JSON.parse(message.data) as MessageDto);
    })
    this.sse.addEventListener('error', message => {
      this.router.navigate(['']);
    })
  }

  onSseEvent(dto: FantascattiSseDto) {
    console.log('event ' + dto.code, dto);
    switch (dto.code) {
      case PlayerReadyDto.CODE:
        this.onSsePlayerReady(dto as PlayerReadyDto);
        break;
      case NewGuessDto.CODE:
        this.onNewGuess(dto as NewGuessDto);
        break;
      case PlayerPicksPieceDto.CODE:
        this.onPlayerPicksPiece(dto as PlayerPicksPieceDto)
        break;
      default:
        console.log('cannot handle event', dto);
        break;
    }
  }
  onPlayerPicksPiece(dto: PlayerPicksPieceDto) {
    if (dto.piece.shape === this.guess.correct) {
      this.score[dto.player.uuid] += 1;
      this.ready = [];
      this.mypick = null;
    }
  }
  onNewGuess(dto: NewGuessDto) {
    this.guess = dto.guess;
    this.changes.detectChanges();
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

  pick(piece: FantascattiPiece) {
    if (this.mypick === null) {
      this.mypick = piece;
      this.fantascatti.pickPiece(this.table, this.shared.player, piece).subscribe(r => {
        // loading?
      });
    }
  }

}
