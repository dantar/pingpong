import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-tables-room',
  templateUrl: './tables-room.component.html',
  styleUrls: ['./tables-room.component.scss']
})
export class TablesRoomComponent implements OnInit {

  constructor(private http: HttpClient) { }

  sse: EventSource;

  ngOnInit(): void {
    this.sse = new EventSource('http://localhost:8080/sse');
    const component = this;
    this.sse.onopen = function (evt) {
        console.log('open', evt);
        component.join();
    };
    this.sse.onmessage = function (evt) {
        console.log('message', evt);
    };
  }

  join() {
    this.http.get('http://localhost:8080/hello').subscribe(result => {
      console.log(result);
    },
    errors => {
      console.log(errors);
    });
  }
  
}
