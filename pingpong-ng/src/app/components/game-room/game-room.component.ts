import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TableDto, PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { environment } from 'src/environments/environment';
import { MessageDto, PingDto } from 'src/app/models/operations-dto.model';
import { FantascattiSseDto, NewPlayerDto, PlayerReadyDto, NewGuessDto, FantascattiCardDto, 
  FantascattiPiece, PlayerPicksPieceDto, PlayerQuitDto } from 'src/app/models/fantascatti.model';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { FantascattiService } from 'src/app/services/fantascatti.service';

@Component({
  selector: 'app-game-room',
  templateUrl: './game-room.component.html',
  styleUrls: ['./game-room.component.scss']
})
export class GameRoomComponent implements OnInit, OnDestroy {

  constructor(
    private activatedRoute: ActivatedRoute,
    private rest: RestService,
    private changes: ChangeDetectorRef,
    public shared: SharedDataService,
    private fantascatti: FantascattiService,
    private router: Router,
  ) { }

  table: TableDto;
  guess: FantascattiCardDto;

  guessKey: string;

  players: PlayerDto[];
  ready: string[];
  score: {[id: string]: number};
  mypick: FantascattiPiece;
  moves: PlayerPicksPieceDto[];

  paletteA: {[color:string]: string};
  paletteB: {[color:string]: string};

  state: 'wait-for-players' | 'wait-for-ready' | 'wait-for-guess' | 'wait-for-picks';

  pieces: FantascattiPiece[];
  piecesmap: {[shape:string]: FantascattiPiece};

  quitting: boolean;

  ngOnInit(): void {
    this.quitting = false;
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
    this.piecesmap = {};
    this.pieces.forEach(p => {
      this.piecesmap[p.shape] = p;
    });
    this.score = {};
    this.moves = [];
    this.players = [];    
    this.resetTurn();
    this.initSse();
    this.rest.table(this.activatedRoute.snapshot.params['uuid']).subscribe(table => {
      this.table = table;
      this.players.push(table.owner);
      this.players.push(...table.seats.map(s=>s.player)); // array spread operator!
      this.players.forEach(p => {
        this.score[p.uuid] = 0;
      })
    });
  }
  ngOnDestroy(): void {
    this.destroySse();
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
    this.iQuit();
  }
  messageEventListener = (
    message => {
      this.onSseEvent(JSON.parse(message.data) as MessageDto);
    }    
  );

  onSseEvent(dto: FantascattiSseDto) {
    if (dto.code !== PingDto.CODE) {
      console.log('event ' + dto.code, dto);
    }
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
        this.onPlayerPicksPiece(dto as PlayerPicksPieceDto);
        break;
      case PlayerQuitDto.CODE:
        this.onPlayerQuit(dto as PlayerQuitDto);
        break;
      case PingDto.CODE:
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
  onPlayerQuit(dto: PlayerQuitDto) {
    if (dto.player.uuid === this.table.owner.uuid) {
      this.router.navigate(['tables']);
    }
    // ready score moves
    if (this.ready.includes(dto.player.uuid)) this.ready.splice(this.ready.indexOf(dto.player.uuid), 1);
    delete this.score[dto.player.uuid];
    this.moves = this.moves.filter(m => m.player.uuid!==dto.player.uuid);
    this.table = dto.table;
    this.players = this.players.filter(p => p.uuid != dto.player.uuid);
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

  iQuit() {
    if (!this.quitting) {
      this.fantascatti.quitGame(this.table, this.shared.player).subscribe(r => {
        this.router.navigate(['tables']);
      });
    }
    this.quitting = true;
  }

  clickPlayer(player: PlayerDto) {
    if (player.uuid === this.shared.player.uuid) this.iQuit();
  }

  pick(piece: FantascattiPiece) {
    if (this.mypick === null && this.state === 'wait-for-picks') {
      this.mypick = piece;
      this.fantascatti.pickPiece(this.table, this.shared.player, piece).subscribe(r => {
        // loading?
      });
    }
  }

  pickByShape(shape: string) {
    if (this.mypick === null && this.state === 'wait-for-picks') {
      this.mypick = this.piecesmap[shape];
      this.fantascatti.pickPiece(this.table, this.shared.player, this.piecesmap[shape]).subscribe(r => {
        // loading?
      });
    }
  }

}
