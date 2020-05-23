import { PlayerDto, TableDto, RobotDto } from './player.model';

export class OpResult {

    ok: boolean;
    details: {[id: string]: any};
    messages: string[];

}

export class MessageDto {
    code: string;
}

export class PingDto extends MessageDto {
    static CODE = 'ping';
    code = PingDto.CODE;
}
export class RegisterPlayerDto extends MessageDto {
    static CODE = 'register-player';
    code = RegisterPlayerDto.CODE;
    player: PlayerDto;
    table: TableDto;
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
export class TablePlayerInvitationSseDto extends MessageDto {
    static CODE = 'table-invitation';
    code = TablePlayerInvitationSseDto.CODE;
    table: TableDto;
    player: PlayerDto;
}
export class TableRobotInvitationSseDto extends MessageDto {
    static CODE = 'table-robot';
    code = TableRobotInvitationSseDto.CODE;
    table: TableDto;
    robot: RobotDto;
}
export class TablePlayerAcceptSseDto extends MessageDto {
    static CODE = 'table-accept';
    code = TablePlayerAcceptSseDto.CODE;
    table: TableDto;
    player: PlayerDto;
    accepted: boolean;
}
export class TableStartSseDto extends MessageDto {
    static CODE = 'table-start';
    code = TableStartSseDto.CODE;
    table: TableDto;
}
export class TableDropSseDto extends MessageDto {
    static CODE = 'table-drop';
    code = TableDropSseDto.CODE;
    table: TableDto;
}
export class TableUpdateSseDto extends MessageDto {
    static CODE = 'table-update';
    code = TableUpdateSseDto.CODE;
    table: TableDto;
}
