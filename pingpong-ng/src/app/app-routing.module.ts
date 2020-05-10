import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TablesRoomComponent } from './components/tables-room/tables-room.component';
import { RegisterPlayerComponent } from './components/register-player/register-player.component';
import { LoggedGuardService } from './services/logged-guard.service';
import { GameRoomComponent } from './components/game-room/game-room.component';
import { SseConnectionComponent } from './components/sse-connection/sse-connection.component';


const routes: Routes = [
  {path: '', component: TablesRoomComponent, canActivate: [LoggedGuardService]},
  {path: 'register', component: RegisterPlayerComponent},
  {path: 'connecting', component: SseConnectionComponent},
  {path: 'tables', component: TablesRoomComponent, canActivate: [LoggedGuardService]},
  {path: 'table/:uuid', component: GameRoomComponent, canActivate: [LoggedGuardService]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
