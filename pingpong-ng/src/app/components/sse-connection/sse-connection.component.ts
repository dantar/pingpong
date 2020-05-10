import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { SharedDataService } from 'src/app/services/shared-data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sse-connection',
  templateUrl: './sse-connection.component.html',
  styleUrls: ['./sse-connection.component.scss']
})
export class SseConnectionComponent implements OnInit {

  constructor(
    public changes: ChangeDetectorRef, 
    public shared: SharedDataService,
    public router: Router) { }

  ngOnInit(): void {
  }

  refresh() {
    console.log('refresh');
    this.changes.detectChanges();
  }

}
