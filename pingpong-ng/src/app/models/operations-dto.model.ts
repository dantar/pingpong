import { PlayerDto, TableDto } from './player.model';

export class OpResult {

    ok: boolean;
    details: {[id: string]: any};
    messages: string[];

}

export class MessageDto {
    code: string;
}

export class RegisterPlayerDto extends MessageDto {
    static CODE = 'register-player';
    code = RegisterPlayerDto.CODE;
    player: PlayerDto;
}

export class StalePlayersDto extends MessageDto {
    static CODE = 'stale-players';
    code = StalePlayersDto.CODE;
    players: PlayerDto[];
}

export class AvailableTableDto extends MessageDto {
    static CODE = 'available-table';
    code = AvailableTableDto.CODE;
    table: TableDto;
}
