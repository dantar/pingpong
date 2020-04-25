import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-tables-room',
  templateUrl: './tables-room.component.html',
  styleUrls: ['./tables-room.component.scss']
})
export class TablesRoomComponent implements OnInit {

  constructor() { }

  sse: EventSource;

  ngOnInit(): void {
    this.sse = new EventSource('http://localhost:8080/sse');
    this.sse.onmessage = function (evt) {
        console.log(this, evt);
    };
  }

  
}
