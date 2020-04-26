export class PlayerDto {

    name: string;
    uuid?: string;

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