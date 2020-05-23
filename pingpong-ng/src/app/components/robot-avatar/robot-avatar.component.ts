import { Component, OnInit, Input } from '@angular/core';
import { RobotDto } from 'src/app/models/player.model';

@Component({
  selector: 'app-robot-avatar',
  templateUrl: './robot-avatar.component.html',
  styleUrls: ['./robot-avatar.component.scss']
})
export class RobotAvatarComponent implements OnInit {

  constructor() { }

  @Input() robot: RobotDto;
  @Input() glyph: string;

  ngOnInit(): void {
    if (!this.glyph) this.glyph = this.robot.name.length > 0 ? this.robot.name[0] : '';
  }

}
