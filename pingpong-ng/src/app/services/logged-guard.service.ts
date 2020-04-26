import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { SharedDataService } from './shared-data.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedGuardService {

  constructor(
    private shared: SharedDataService,
    private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.shared.player && this.shared.player.uuid) {
      return true;
    }
    this.router.navigate(['']);
    return false;
  }

}
