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
    robot?: RobotDto;
    player?: PlayerDto;
    pending: boolean;
    open: boolean;
}

export class RobotDto {
    name: string;
    level: number;
    avatar: AvatarDto;
}