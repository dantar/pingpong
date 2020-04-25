import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TablesRoomComponent } from './components/tables-room/tables-room.component';


const routes: Routes = [
  {path: '', component: TablesRoomComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
