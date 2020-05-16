export class SituationDto {
    players: PlayerDto[];
    tables: TableDto[];
}

export class PlayerDto {
    name: string;
    uuid?: string;
    avatar: AvatarDto;
}

export class AvatarDto {
    color: string;
}

export class TableDto {
    uuid?: string;
    seats: SeatDto[];
    owner: PlayerDto;
}

export class SeatDto {
    player: PlayerDto;
    pending: boolean;
    open: boolean;
}