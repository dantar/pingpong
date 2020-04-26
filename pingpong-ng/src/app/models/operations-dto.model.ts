import { PlayerDto } from './player.model';

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