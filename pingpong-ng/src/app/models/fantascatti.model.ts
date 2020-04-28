import { PlayerDto } from './player.model';

export class FantascattiSseDto {
    code: string;
}
export class PlayerReadyDto extends FantascattiSseDto {
    static CODE = 'player-ready';
    code = PlayerReadyDto.CODE;
    player: PlayerDto;
}
export class NewGuessDto extends FantascattiSseDto {
    static CODE = 'new-guess';
    code = NewGuessDto.CODE;
    guess: FantascattiCardDto;
}
export class PlayerPicksPieceDto extends FantascattiSseDto {
    static CODE = 'pick-piece';
    code = PlayerPicksPieceDto.CODE;
    piece: FantascattiPiece;
    player: PlayerDto;
}

export class FantascattiCardDto {
    correct: string;
    shape1: string;
    shape2: string;
    color1: string;
    color2: string;
}

export class FantascattiPiece {
    shape: string;
    color: string;
}