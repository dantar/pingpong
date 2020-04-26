import { PlayerDto } from './player.model';

export class FantascattiSseDto {
    code: string;
}

export class PlayerReadyDto extends FantascattiSseDto {
    static CODE = 'player-ready';
    code = PlayerReadyDto.CODE;
    player: PlayerDto;
}
