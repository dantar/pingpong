import { Component } from '@angular/core';
import { SharedDataService } from './services/shared-data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  
  title = 'Dragon\'s Hoard';

  constructor(public shared: SharedDataService) {
  }

}
