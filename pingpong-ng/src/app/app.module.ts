import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TablesRoomComponent } from './components/tables-room/tables-room.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterPlayerComponent } from './components/register-player/register-player.component';
import { GameRoomComponent } from './components/game-room/game-room.component';
import { PlayerAvatarComponent } from './components/player-avatar/player-avatar.component';


@NgModule({
  declarations: [
    AppComponent,
    TablesRoomComponent,
    RegisterPlayerComponent,
    GameRoomComponent,
    PlayerAvatarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
