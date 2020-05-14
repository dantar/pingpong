import { Component, OnInit, Input } from '@angular/core';
import { PlayerDto } from 'src/app/models/player.model';

@Component({
  selector: 'app-player-avatar',
  templateUrl: './player-avatar.component.html',
  styleUrls: ['./player-avatar.component.scss']
})
export class PlayerAvatarComponent implements OnInit {

  @Input() player: PlayerDto;
  @Input() name: string;
  @Input() glyph: string;

  constructor() { }

  ngOnInit(): void {
    if (!this.name) this.name = this.player.name ? this.player.name : '?';
    if (!this.glyph) this.glyph = this.name.length > 0 ? this.name[0] : '';
  }

}
