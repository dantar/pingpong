import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TablesRoomComponent } from './components/tables-room/tables-room.component';
import { RegisterPlayerComponent } from './components/register-player/register-player.component';
import { LoggedGuardService } from './services/logged-guard.service';


const routes: Routes = [
  {path: '', component: RegisterPlayerComponent},
  {path: 'tables', component: TablesRoomComponent, canActivate: [LoggedGuardService]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
