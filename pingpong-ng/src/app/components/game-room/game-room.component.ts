import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TableDto, PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { environment } from 'src/environments/environment';
import { MessageDto } from 'src/app/models/operations-dto.model';
import { FantascattiSseDto, NewPlayerDto, PlayerReadyDto, NewGuessDto, FantascattiCardDto, FantascattiPiece, PlayerPicksPieceDto } from 'src/app/models/fantascatti.model';
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

  dragonfill = '#ffff00';

  table: TableDto;
  guess: FantascattiCardDto;

  guessKey: string;

  players: PlayerDto[];
  ready: string[];
  score: {[id: string]: number};
  color: {[id: string]: string};
  colors: string[];
  mypick: FantascattiPiece;
  moves: PlayerPicksPieceDto[];

  paletteA: {[color:string]: string};
  paletteB: {[color:string]: string};

  state: 'wait-for-players' | 'wait-for-ready' | 'wait-for-guess' | 'wait-for-picks';

  pieces: FantascattiPiece[];
  piecesmap: {[shape:string]: FantascattiPiece};

  ngOnInit(): void {
    this.pieces = [
      {shape: 'dragon', color: 'red'},
      {shape: 'chest', color: 'brown'},
      {shape: 'sword', color: 'blue'},
      {shape: 'gold', color: 'yellow'},
      {shape: 'shield', color: 'green'},
    ];
    this.paletteA = {
      'red': '#ff0000',
      'brown': '#a46102',
      'blue': '#b3b3b3',
      'yellow': '#ffff00',
      'green': '#00ff00',
    }
    this.paletteB = {
      'red': '#800000',
      'brown': '#550000',
      'blue': '#333333',
      'yellow': '#806600',
      'green': '#008000',
    }
    this.colors = ['#ff9400', '#ff4600', '#ff0039', '#ff00c2', '#d300ff', '#7b00ff', '#1500ff', '#0084ff', '#00ceff', '#00ffde', '#00ff1b', '#e1ff00'];
    this.piecesmap = {};
    this.pieces.forEach(p => {
      this.piecesmap[p.shape] = p;
    });
    this.score = {};
    this.color = {};
    this.moves = [];
    this.players = [];    
    this.resetTurn();
    this.initSse(this.activatedRoute.snapshot.params['uuid']);
    this.rest.table(this.activatedRoute.snapshot.params['uuid']).subscribe(table => {
      this.table = table;
      this.players.push(table.owner);
      this.players.push(...table.seats.map(s=>s.player)); // array spread operator!
      this.players.forEach(p => {
        this.score[p.uuid] = 0;
        this.color[p.uuid] = this.colors.splice(0, 1)[0];
      })
    });
  }

  initSse(uuid: string) {
    this.shared.sse.addEventListener('message', message => {
      this.onSseEvent(JSON.parse(message.data) as MessageDto);
    })
  }

  onSseEvent(dto: FantascattiSseDto) {
    console.log('event ' + dto.code, dto);
    switch (dto.code) {
      case NewPlayerDto.CODE:
        this.onSseNewPlayerDto(dto as NewPlayerDto);
        break;
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
  onSseNewPlayerDto(dto: NewPlayerDto) {
    this.players.push(dto.player);
  }
  onPlayerPicksPiece(dto: PlayerPicksPieceDto) {
    this.moves.push(dto);
    if (dto.score) {
      this.score = dto.score;
      this.resetTurn();
    } else if (this.moves.length >= this.players.length) {
      this.resetTurn();
    }
  }
  onNewGuess(dto: NewGuessDto) {
    this.moves = [];
    this.guess = dto.guess;
    this.initGuessKey();
    this.state = 'wait-for-picks';
    this.changes.detectChanges();
  }
  initGuessKey() {
    if (this.guess.shape1.localeCompare(this.guess.shape2) < 0 ) {
      this.guessKey = this.guess.shape1 + '-' + this.guess.shape2;
    } else {
      this.guessKey = this.guess.shape2 + '-' + this.guess.shape1;
    }
  }
  onSsePlayerReady(dto: PlayerReadyDto) {
    this.ready.push(dto.player.uuid);
    if (this.ready.length >= this.players.length) {
      this.state = 'wait-for-guess';
    }
    this.changes.detectChanges();
  }

  resetTurn() {
    this.state = 'wait-for-ready';
    this.ready = [];
    this.mypick = null;
}

  iAmReady() {
    if (this.ready.includes(this.shared.player.uuid)) return;
    this.moves = [];
    this.guess = null;
    this.guessKey = null;
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

  pickByShape(shape: string) {
    if (this.mypick === null) {
      this.mypick = this.piecesmap[shape];
      this.fantascatti.pickPiece(this.table, this.shared.player, this.piecesmap[shape]).subscribe(r => {
        // loading?
      });
    }
  }

}
