import { Component, OnInit } from '@angular/core';
import { PlayerDto } from 'src/app/models/player.model';
import { RestService } from 'src/app/services/rest.service';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register-player',
  templateUrl: './register-player.component.html',
  styleUrls: ['./register-player.component.scss']
})
export class RegisterPlayerComponent implements OnInit {

  constructor(
    public rest: RestService, 
    private shared: SharedDataService, 
    private router: Router) { }

  player: PlayerDto;

  ngOnInit(): void {
    this.player = {name: '', avatar: {color: '#ff0000'}};
  }

  register() {
    this.rest.register(this.player).subscribe(result => {
      this.shared.setPlayer(result);
      this.shared.initSse();
      this.router.navigate(['tables']);
    });
  }

}
