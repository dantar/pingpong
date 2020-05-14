import { Component, ChangeDetectorRef } from '@angular/core';
import { SharedDataService } from './services/shared-data.service';
import { RestService } from './services/rest.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  
  title = 'Dragon\'s Hoard';

  constructor(
    private changes: ChangeDetectorRef,
    public shared: SharedDataService,
    private rest: RestService) {
  }

  refresh() {
    console.log('refresh');
    this.changes.detectChanges();
  }

  sendHello() {
    this.rest.hello().subscribe(result => {
      this.changes.detectChanges();
    });
  }

}
