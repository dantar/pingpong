import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TablesRoomComponent } from './tables-room.component';

describe('TablesRoomComponent', () => {
  let component: TablesRoomComponent;
  let fixture: ComponentFixture<TablesRoomComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TablesRoomComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TablesRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
